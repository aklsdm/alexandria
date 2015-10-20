package nl.knaw.huygens.alexandria.diagnostics;

/*
 * #%L
 * alexandria-acceptance-tests
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

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import nl.knaw.huygens.Log;
import nl.knaw.huygens.alexandria.concordion.AlexandriaAcceptanceTest;
import nl.knaw.huygens.alexandria.endpoint.about.AboutEndpoint;
import nl.knaw.huygens.alexandria.endpoint.resource.ResourcesEndpoint;
import nl.knaw.huygens.alexandria.jersey.exceptionmappers.NotFoundExceptionMapper;

@RunWith(ConcordionRunner.class)
public class DiagnosticsFixture extends AlexandriaAcceptanceTest {

  @BeforeClass
  public static void registerEndpoints() {
    Log.trace("Registering endpoints");
    register(AboutEndpoint.class);
    register(ResourcesEndpoint.class);
    register(NotFoundExceptionMapper.class);
  }
}
