package nl.knaw.huygens.alexandria.service;

import java.util.UUID;

import nl.knaw.huygens.alexandria.exception.NotFoundException;
import nl.knaw.huygens.alexandria.exception.ResourceExistsException;
import nl.knaw.huygens.alexandria.model.AlexandriaAnnotation;

public interface AnnotationService {
  AlexandriaAnnotation createAnnotation(String type, String value) throws ResourceExistsException;

  AlexandriaAnnotation createAnnotation(UUID uuid, String type, String value) throws ResourceExistsException;

  AlexandriaAnnotation readAnnotation(UUID uuid) throws NotFoundException;
}
