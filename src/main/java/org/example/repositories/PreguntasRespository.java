package org.example.repositories;

import java.util.List;

public interface PreguntasRespository {

    List<String> findPreguntasPorExamen(Long id);
    void guardarVarias(List<String> preguntas);
}
