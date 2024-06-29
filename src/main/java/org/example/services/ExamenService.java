package org.example.services;

import org.example.models.Examen;

import java.util.Optional;

public interface ExamenService {

    Optional<Examen> findExamPorNombre(String nombre);

    Examen findExamenPorNombreConPreguntas(String nombre);

    Examen guardar(Examen examen);
}
