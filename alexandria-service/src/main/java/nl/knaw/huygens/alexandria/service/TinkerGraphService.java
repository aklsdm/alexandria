package nl.knaw.huygens.alexandria.service;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import nl.knaw.huygens.alexandria.endpoint.LocationBuilder;
import nl.knaw.huygens.alexandria.storage.Storage;

@Singleton
public class TinkerGraphService extends TinkerPopService {
  protected static final Storage STORAGE = getStorage();

  @Inject
  public TinkerGraphService(LocationBuilder locationBuilder) {
    super(STORAGE, locationBuilder);
  }

  private static Storage getStorage() {
    TinkerGraph tg = TinkerGraph.open();
    tg.createIndex(Storage.IDENTIFIER_PROPERTY, Vertex.class);
    return new Storage(tg);
  }
}
