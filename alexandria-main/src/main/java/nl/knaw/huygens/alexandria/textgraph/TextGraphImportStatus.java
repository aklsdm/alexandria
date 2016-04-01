package nl.knaw.huygens.alexandria.textgraph;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import com.google.common.collect.Lists;

import nl.knaw.huygens.alexandria.api.model.Entity;
import nl.knaw.huygens.alexandria.api.model.JsonWrapperObject;
import nl.knaw.huygens.alexandria.api.model.PropertyPrefix;

@JsonTypeName("textGraphImportStatus")
@JsonInclude(Include.NON_NULL)
public class TextGraphImportStatus extends JsonWrapperObject implements Entity {
  enum State {
    waiting, processing, done
  }

  private boolean done = false;
  private State state = State.waiting;
  private Duration processingTime;
  private Instant startTime;
  private List<String> validationErrors = Lists.newArrayList();
  private URI baseLayerDefinitionURI;
  private URI baseLayerURI;
  private Instant expires;

  public URI getBaseLayerURI() {
    return baseLayerURI;
  }

  public State getState() {
    return state;
  }

  public void setStarted() {
    this.state = State.processing;
    this.startTime = Instant.now();
  }

  public void setDone() {
    this.done = true;
    this.state = State.done;
    this.processingTime = Duration.between(startTime, Instant.now());
    this.expires = Instant.now().plus(1l, ChronoUnit.HOURS);
  }

  public boolean isDone() {
    return done;
  }

  @JsonIgnore
  public boolean isExpired() {
    return expires != null && Instant.now().isAfter(expires);
  }

  @JsonProperty(PropertyPrefix.LINK + "baseLayerDefinition")
  public URI getBaseLayerDefinitionURI() {
    return baseLayerDefinitionURI;
  }

  public void setBaseLayerDefinitionURI(URI baseLayerDefinitionURI) {
    this.baseLayerDefinitionURI = baseLayerDefinitionURI;
  }

  @JsonProperty(PropertyPrefix.LINK + "baseLayer")
  public void setBaseLayerURI(URI baseLayerURI) {
    this.baseLayerURI = baseLayerURI;
  }

  public List<String> getValidationErrors() {
    return validationErrors;
  }

  @JsonSerialize(using = InstantSerializer.class)
  public Instant getExpires() {
    return expires;
  }

  @JsonSerialize(using = InstantSerializer.class)
  public Instant getStartTime() {
    return startTime;
  }

  @JsonSerialize(using = DurationSerializer.class)
  public Duration getProcessingTime() {
    return processingTime;
  }

}
