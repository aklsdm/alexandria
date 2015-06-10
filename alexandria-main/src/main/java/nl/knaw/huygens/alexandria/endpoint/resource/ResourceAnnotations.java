package nl.knaw.huygens.alexandria.endpoint.resource;

import javax.inject.Inject;
import javax.ws.rs.PathParam;

import nl.knaw.huygens.alexandria.endpoint.AnnotatableObjectAnnotations;
import nl.knaw.huygens.alexandria.endpoint.AnnotationCreationRequestBuilder;
import nl.knaw.huygens.alexandria.endpoint.LocationBuilder;
import nl.knaw.huygens.alexandria.endpoint.UUIDParam;
import nl.knaw.huygens.alexandria.model.AnnotatableObject;
import nl.knaw.huygens.alexandria.service.AlexandriaService;

public class ResourceAnnotations extends AnnotatableObjectAnnotations {

  // TODO: how to remove this duplicated inject/constructor?
  @Inject
  public ResourceAnnotations(AlexandriaService service, //
      AnnotationCreationRequestBuilder requestBuilder, //
      LocationBuilder locationBuilder, //
      @PathParam("uuid") final UUIDParam uuidParam) {
    super(service, requestBuilder, locationBuilder, uuidParam);
  }

  @Override
  protected AnnotatableObject getAnnotableObject() {
    return service.readResource(uuid);
  };

  @Override
  protected AnnotationCreationRequestBuilder getAnnotationCreationRequestBuilder() {
    return requestBuilder.ofResource(uuid);
  };

}
