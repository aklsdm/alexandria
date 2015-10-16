package nl.knaw.huygens.alexandria.endpoint;

/*
 * #%L
 * alexandria-main
 * =======
 * Copyright (C) 2015 Huygens ING (KNAW)
 * =======
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;

import nl.knaw.huygens.alexandria.model.AlexandriaState;

public abstract class AbstractAccountablePrototype extends JsonWrapperObject implements Prototype {
  private UUIDParam id;
  private ProvenancePrototype provenance;
  private AlexandriaState state;

  public UUIDParam getId() {
    return id;
  }

  public void setId(UUIDParam uuid) {
    id = uuid;
  }

  public AlexandriaState getState() {
    return state;
  }

  public Optional<ProvenancePrototype> getProvenance() {
    return Optional.ofNullable(provenance);
  }

  @JsonIgnore
  public void setState(AlexandriaState state) {
    this.state = state;
  }

}
