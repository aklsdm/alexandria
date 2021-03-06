package nl.knaw.huygens.alexandria.api.model;

/*
 * #%L
 * alexandria-api
 * =======
 * Copyright (C) 2015 - 2016 Huygens ING (KNAW)
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("baseLayerDefinition")
public class BaseLayerDefinitionPrototype extends JsonWrapperObject implements Prototype {
  private List<BaseElementDefinition> baseElements = new ArrayList<>();
  private List<String> subresourceElements = new ArrayList<>();

  public BaseLayerDefinitionPrototype setBaseElements(BaseElementDefinition... baseElements) {
    this.baseElements.addAll(Arrays.asList(baseElements));
    return this;
  }

  public List<BaseElementDefinition> getBaseElements() {
    return baseElements;
  }

  public BaseLayerDefinitionPrototype setSubresourceElements(String... subresourceElements) {
    this.subresourceElements.addAll(Arrays.asList(subresourceElements));
    return this;
  }

  public List<String> getSubresourceElements() {
    return subresourceElements;
  }

}
