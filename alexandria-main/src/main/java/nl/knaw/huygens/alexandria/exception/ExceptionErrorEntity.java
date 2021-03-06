package nl.knaw.huygens.alexandria.exception;

/*
 * #%L
 * alexandria-main
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

public class ExceptionErrorEntity extends ErrorEntity {

  private Exception exception;

  private ExceptionErrorEntity(Exception e) {
    super(e.getMessage());
    this.exception = e;
  }

  public static ExceptionErrorEntity of(Exception e) {
    return new ExceptionErrorEntity(e);
  }

  public String getException() {
    return exception.getClass().getName();
  }

}
