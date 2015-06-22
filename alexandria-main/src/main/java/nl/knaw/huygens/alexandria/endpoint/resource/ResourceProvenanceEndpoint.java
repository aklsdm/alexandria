package nl.knaw.huygens.alexandria.endpoint.resource;

import io.swagger.annotations.Api;

import javax.inject.Inject;
import javax.ws.rs.PathParam;

import nl.knaw.huygens.alexandria.endpoint.AccountableProvenanceEndpoint;
import nl.knaw.huygens.alexandria.endpoint.LocationBuilder;
import nl.knaw.huygens.alexandria.endpoint.UUIDParam;
import nl.knaw.huygens.alexandria.model.Accountable;
import nl.knaw.huygens.alexandria.service.AlexandriaService;

@Api("resources")
public class ResourceProvenanceEndpoint extends AccountableProvenanceEndpoint {
  @Inject
  public ResourceProvenanceEndpoint(AlexandriaService service, //
      @PathParam("uuid") final UUIDParam uuidParam, LocationBuilder locationBuilder) {
    super(service, uuidParam, locationBuilder);
  }

  @Override
  protected Accountable getAccountable() {
    return service.readResource(uuid).get();
  };

}
