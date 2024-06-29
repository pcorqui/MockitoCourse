package org.example.services;

import org.example.models.Examen;
import org.example.repositories.ExamenRepository;
import org.example.repositories.PreguntasRespository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public class ExamenServiceImpl implements ExamenService{

    private ExamenRepository examenRepository;
    private PreguntasRespository preguntasRespository;

    public ExamenServiceImpl(ExamenRepository examenRepository, PreguntasRespository preguntasRespository) {
        this.examenRepository = examenRepository;
        this.preguntasRespository = preguntasRespository;
    }

    @Override
    public Optional<Examen> findExamPorNombre(String nombre) {
         return  examenRepository
                 .findAll()
                 .stream()
                 .filter(e -> e.getNombre().contains(nombre))
                 .findFirst();
    }

    @Override
    public Examen findExamenPorNombreConPreguntas(String nombre) {
        Optional<Examen> examenOptional = findExamPorNombre(nombre);
        Examen examen = null;
        if(examenOptional.isPresent()){
            examen = examenOptional.orElseThrow();
            List<String> preguntas = preguntasRespository.findPreguntasPorExamen(examen.getId());
            examen.setPreguntas(preguntas);
        }
        return examen;
    }

    @Override
    public Examen guardar(Examen examen) {
        if(!examen.getPreguntas().isEmpty()){
            preguntasRespository.guardarVarias(examen.getPreguntas());
        }
        return examenRepository.guardar(examen);
    }
}
