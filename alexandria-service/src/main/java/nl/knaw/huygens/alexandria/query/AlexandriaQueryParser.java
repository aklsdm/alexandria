package nl.knaw.huygens.alexandria.query;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.tinkerpop.gremlin.process.traversal.Traverser;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import nl.knaw.huygens.Log;
import nl.knaw.huygens.alexandria.endpoint.LocationBuilder;
import nl.knaw.huygens.alexandria.endpoint.search.AlexandriaQuery;
import nl.knaw.huygens.alexandria.model.AlexandriaResource;
import nl.knaw.huygens.alexandria.storage.frames.AlexandriaVF;
import nl.knaw.huygens.alexandria.storage.frames.AnnotationVF;
import nl.knaw.huygens.alexandria.storage.frames.ResourceVF;

public class AlexandriaQueryParser {

  private static LocationBuilder locationBuilder;

  static List<String> parseErrors = Lists.newArrayList();

  @Inject
  public AlexandriaQueryParser(final LocationBuilder locationBuilder) {
    this.locationBuilder = locationBuilder;
  }

  public ParsedAlexandriaQuery parse(final AlexandriaQuery query) {
    parseErrors.clear();

    final ParsedAlexandriaQuery paq = new ParsedAlexandriaQuery()//
        .setVFClass(parseFind(query.getFind()))//
        .setPredicate(parseWhere(query.getWhere()))//
        .setResultComparator(parseSort(query.getSort()));
    parseReturn(query.getFields(), paq);

    if (!parseErrors.isEmpty()) {
      throw new AlexandriaQueryParseException(parseErrors);
    }

    return paq;
  }

  private Class<? extends AlexandriaVF> parseFind(final String find) {
    if (find.equals("annotation")) {
      return AnnotationVF.class;

    } else if (find.equals("resource")) {
      parseErrors.add("find: type 'resource' not supported yet");
      return ResourceVF.class;

    } else {
      parseErrors.add("find: unknown type '" + find + "', should be 'annotation'");
      // parseErrors.add("unknown type '" + find + "' in find, should be 'annotation' or 'resource'");
    }
    return null;
  }

  static final Map<String, Function<AnnotationVF, Object>> valueMapping = ImmutableMap.<String, Function<AnnotationVF, Object>> builder()//
      .put("id", AnnotationVF::getUuid)//
      .put("when", AnnotationVF::getProvenanceWhen)//
      .put("who", AnnotationVF::getProvenanceWho)//
      .put("why", AnnotationVF::getProvenanceWhy)//
      .put("type", AnnotationVF::getType)//
      .put("value", AnnotationVF::getValue)//
      .put("resource.id", AnnotationVF::getResourceId)//
      .put("subresource.id", AnnotationVF::getSubResourceId)//
      .put("resource.url", AlexandriaQueryParser::getResourceURL)//
      .put("subresource.url", AlexandriaQueryParser::getSubResourceURL)//
      .build();

  static final String ALLOWEDFIELDS = ", available fields: " + Joiner.on(", ").join(valueMapping.keySet());

  private static Predicate<Traverser<AnnotationVF>> parseWhere(final String whereString) {
    final List<WhereToken> tokens = tokenize(whereString);
    return createPredicate(tokens);
  }

  static List<WhereToken> tokenize(final String whereString) {
    List<String> strings = splitToList(whereString);
    List<WhereToken> list = Lists.newArrayListWithExpectedSize(strings.size());
    for (String string : strings) {
      // TODO
      WhereToken token = new WhereToken();
      list.add(token);
    }
    return list;
  }

  private static Predicate<Traverser<AnnotationVF>> createPredicate(List<WhereToken> tokens) {
    return annotationVFTraverser -> {
      boolean pass = true;
      final AnnotationVF annotationVF = annotationVFTraverser.get();
      for (WhereToken token : tokens) {
        pass = pass && assertMatch(annotationVF, token);
      }
      return pass;
    };
  }

  private static final WhereToken STATE_IS_CONFIRMED = new WhereToken()//
      .setProperty("state").setFunction(MatchFunction.eq).setParameters(ImmutableList.of("CONFIRMED"));

  private static boolean assertMatch(AnnotationVF annotationVF, WhereToken token) {
    if (STATE_IS_CONFIRMED.equals(token)) {
      return annotationVF.isConfirmed();
    }
    return true;
  }

  private static String getResourceURL(final AnnotationVF avf) {
    return id2url(avf.getResourceId());
  }

  private static String id2url(String resourceId) {
    if (StringUtils.isNotEmpty(resourceId) && !AnnotationVF.NO_VALUE.equals(resourceId)) {
      return locationBuilder.locationOf(AlexandriaResource.class, resourceId).toString();
    }
    return ":null";
  }

  private static String getSubResourceURL(final AnnotationVF avf) {
    return id2url(avf.getSubResourceId());
  }

  private static Comparator<AnnotationVF> parseSort(final String sortString) {
    // TODO: cache resultcomparator?
    boolean errorInSort = false;
    final List<SortToken> sortTokens = parseSortString(sortString);
    final List<Function<AnnotationVF, Object>> valueFunctions = Lists.newArrayList();
    for (final SortToken token : sortTokens) {
      final String field = token.getField();
      final Function<AnnotationVF, Object> valueMapper = valueMapping.get(field);
      if (valueMapper == null) {
        parseErrors.add("sort: unknown field: " + field + ALLOWEDFIELDS);
        errorInSort = true;
      } else {
        valueFunctions.add(valueMapper);
      }
    }
    if (!errorInSort) {
      return (getComparator(sortKeyGenerator(valueFunctions)));
      // return getComparator(valueFunctions);
    }
    return null;
  }

  private static List<SortToken> parseSortString(final String sortString) {
    final List<SortToken> sortFields = splitToList(sortString).stream().map(f -> sortToken(f)).collect(toList());
    return sortFields;
  }

  static SortToken sortToken(final String f) {
    final SortToken token = new SortToken();
    token.setAscending(!f.startsWith("-"));
    token.setField(f.replaceFirst("^[\\-\\+]", ""));
    return token;
  }

  private static Comparator<AnnotationVF> getComparator(final List<Function<AnnotationVF, Object>> valueFunctions) {
    // valueFunctions.stream().map(f -> new )
    // convert to Order<AnnotationVF> functions, return compound
    final List<Ordering<AnnotationVF>> subOrders = Lists.newArrayList();
    for (final Function<AnnotationVF, Object> function : valueFunctions) {
      subOrders.add(ordering(function));
    }
    Ordering<AnnotationVF> order = subOrders.remove(0);
    for (final Ordering<AnnotationVF> suborder : subOrders) {
      order = order.compound(suborder);
    }
    return order;
  }

  private static Ordering<AnnotationVF> ordering(final Function<AnnotationVF, Object> function) {
    return new Ordering<AnnotationVF>() {
      @Override
      public int compare(final AnnotationVF left, final AnnotationVF right) {
        return ((Comparable) function.apply(left)).compareTo(function.apply(right));
      }
    };
  }

  private static Function<AnnotationVF, String> sortKeyGenerator(final List<Function<AnnotationVF, Object>> valueFunctions) {
    return (final AnnotationVF avf) -> {
      final StringBuilder sb = new StringBuilder();
      for (final Function<AnnotationVF, Object> function : valueFunctions) {
        sb.append(function.apply(avf));
        sb.append("|");
      }
      Log.debug("sortKey=" + sb);
      return sb.toString();
    };
  }

  private static void parseReturn(final String fieldString, final ParsedAlexandriaQuery paq) {
    final List<String> fields = splitToList(fieldString);
    final Set<String> allowedFields = valueMapping.keySet();
    final List<String> unknownFields = Lists.newArrayList(fields);
    unknownFields.removeAll(allowedFields);
    if (!unknownFields.isEmpty()) {
      parseErrors.add("return: unknown field(s) " + Joiner.on(", ").join(unknownFields) + ALLOWEDFIELDS);

    } else {
      paq.setReturnFields(fields);

      final Function<AnnotationVF, Map<String, Object>> mapper = avf -> fields.stream().collect(toMap(Function.identity(), f -> valueMapping.get(f).apply(avf)));
      // TODO: cache resultmapper?
      paq.setResultMapper(mapper);
    }
  }

  private static List<String> splitToList(final String fieldString) {
    return Splitter.on(",").trimResults().splitToList(fieldString);
  }

  private static Comparator<AnnotationVF> getComparator(final Function<AnnotationVF, String> sortKeyGenerator) {
    return (a1, a2) -> sortKeyGenerator.apply(a1).compareTo(sortKeyGenerator.apply(a2));
  }

  static class SortToken {
    private String field = "";
    private boolean ascending = true;

    public String getField() {
      return field;
    }

    public void setField(final String field) {
      this.field = field;
    }

    public boolean isAscending() {
      return ascending;
    }

    public void setAscending(final boolean ascending) {
      this.ascending = ascending;
    }
  }

  enum MatchFunction {
    eq, match, inSet, inRange
  }

  static class WhereToken {
    String property;
    MatchFunction function;
    List<Object> parameters = Lists.newArrayList();

    public String getProperty() {
      return property;
    }

    public WhereToken setProperty(String property) {
      this.property = property;
      return this;
    }

    public MatchFunction getFunction() {
      return function;
    }

    public WhereToken setFunction(MatchFunction function) {
      this.function = function;
      return this;
    }

    public List<Object> getParameters() {
      return parameters;
    }

    public WhereToken setParameters(List<Object> parameters) {
      this.parameters = parameters;
      return this;
    }

  }
}
