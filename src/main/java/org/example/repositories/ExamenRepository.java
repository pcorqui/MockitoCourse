package org.example.repositories;

import org.example.models.Examen;
import org.example.services.ExamenService;

import java.util.List;

public interface ExamenRepository {

    Examen guardar(Examen examen);
    List<Examen> findAll();
}
