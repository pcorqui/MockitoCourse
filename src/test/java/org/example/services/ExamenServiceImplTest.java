package org.example.services;

import org.example.models.Examen;
import org.example.repositories.ExamenRepository;
import org.example.repositories.PreguntasRespository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)//se habilita la inyeccion de dependencias, se debe incluir la libreria de mockito-junit
class ExamenServiceImplTest {

    @Mock
    ExamenRepository repository;
    @Mock
    PreguntasRespository preguntasRespository;

    @InjectMocks
    ExamenServiceImpl service; //aqui se inyectan los mocks

    //para capturar argumentos de los when
    @Captor
    ArgumentCaptor<Long> capture;

    @BeforeEach
    void setUp() {

//        MockitoAnnotations.openMocks(this);
//        repository = mock(ExamenRepository.class);
//        preguntasRespository = mock(PreguntasRespository.class);
//        service = new ExamenServiceImpl(repository, preguntasRespository);
    }

    @Test
    void findExamPorNombre() {
        List<Examen> datos = Arrays.asList(new Examen(5L, "Matematicas"),
                new Examen(6L, "languaje"),
                new Examen(7L, "Historia"));

        when(repository.findAll()).thenReturn(datos);//solo se puede hacer mock de metodos publicos
        Optional<Examen> examen = service.findExamPorNombre("Matematicas");

        assertTrue(examen.isPresent());
        assertEquals(5L,examen.orElseThrow().getId());
        assertEquals("Matematicas",examen.get().getNombre());
    }

    @Test
    void findExamPorNombreListaVacia() {
        List<Examen> datos = Collections.EMPTY_LIST;

        when(repository.findAll()).thenReturn(datos);//solo se puede hacer mock de metodos publicos
        Optional<Examen> examen = service.findExamPorNombre("Matematicas");

        assertFalse(examen.isPresent());
    }


    @Test
    void testPreguntasExamen(){
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntasRespository.findPreguntasPorExamen(5L)).thenReturn(Datos.PREGUNTAS);
        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
        assertEquals(5,examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmetica"));
    }


    @Test
    void testPreguntasExamenVerify(){
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntasRespository.findPreguntasPorExamen(5L)).thenReturn(Datos.PREGUNTAS);
        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
        assertEquals(5,examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmetica"));
        verify(repository).findAll(); //para verificar si se invoco el metodo
        verify(preguntasRespository).findPreguntasPorExamen(5L);
    }

    @Test
    void testExamenVerify(){
        when(repository.findAll()).thenReturn(Collections.emptyList());//cuando se invoque al metodo se retornara una lista vacia
        when(preguntasRespository.findPreguntasPorExamen(5L)).thenReturn(Datos.PREGUNTAS);
        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas2");;
        assertNull(examen);
        verify(repository).findAll(); //para verificar si se invoco el metodo
        verify(preguntasRespository).findPreguntasPorExamen(5L);
    }

    @Test
    void testGuardarExamen(){
        //esto es BDD(Behavior Development Driven)
        // por que se hace given(dado): un entorno de prueba
        // when(cuando): se ejecuten los repository
        // then(entonces): los asserts
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);

        //con esta implementacion se puede controlar que se va a devolver
        when(repository.guardar(any(Examen.class))).then(new Answer<Examen>() {

            Long secuencia = 8L;

            @Override
            public Examen answer(InvocationOnMock invocationOnMock) throws Throwable {
                Examen examen = invocationOnMock.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        });
        Examen examen = service.guardar(Datos.EXAMEN);
        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("Fisica",examen.getNombre());
        verify(repository).guardar(any(Examen.class));
        verify(preguntasRespository).guardarVarias(anyList());
    }

    @Test
    void testManejoException(){
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        //cuando se consulte findPreguntas disparar una excepcion
        when(preguntasRespository.findPreguntasPorExamen(anyLong())).thenThrow(IllegalArgumentException.class);

        //cuando se consulta el service se lanza la excepcion
        Exception exception = assertThrows(IllegalArgumentException.class, ()->{
            service.findExamenPorNombreConPreguntas("Matematicas");
        });

        assertEquals(IllegalArgumentException.class, exception.getClass());
        verify(repository).findAll();
        verify(preguntasRespository).findPreguntasPorExamen(anyLong());
    }

    @Test
    void testArgumentMatchers(){
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntasRespository.findPreguntasPorExamen(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matematicas");
        //cuando se llaman los Argument matcher es para verficacion
        //que los argumentos que se pasan sean los correctos
        verify(repository).findAll();
        verify(preguntasRespository).findPreguntasPorExamen(argThat(arg -> arg != null && arg.equals(5L)));
    }

    @Test
    void testArgumentMatchers2(){
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntasRespository.findPreguntasPorExamen(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matematicas");
        //cuando se llaman los Argument matcher es para verficacion
        //que los argumentos que se pasan sean los correctos
        verify(repository).findAll();
        verify(preguntasRespository).findPreguntasPorExamen(argThat(new MiArgsMatchers()));
    }

    //una forma de personalizar los matchers
    public static class MiArgsMatchers implements ArgumentMatcher<Long>{

        //se ponen las condiciones personalizadas
        //a las cuales se expondra el angular matcher.
        Long argument;
        @Override
        public boolean matches(Long argument) {
            this.argument = argument;
            return argument != null && argument > 0;
        }

        @Override
        public String toString(){
            return "es para un mensaje personalizado de error" +
                    "que imprime mockito en caso de que falle el test"
                    + argument + " debe ser un entero positivo";
        }
    }

    @Test
    void testArgumentCaptor() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        //no se valida la invocacion
        //when(preguntasRespository.findPreguntasPorExamen(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matematicas");
        //la intencion es validar el argumento que se pasa
        //en esta caso se captura el argumento de tipo long
        //ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        verify(preguntasRespository).findPreguntasPorExamen(capture.capture());

        assertEquals(5L, capture.getValue());
    }

    @Test
    void testDoThrow() {
        Examen examen = Datos.EXAMEN;
        examen.setPreguntas(Datos.PREGUNTAS);

        //para poder lanzar una excepcion cuando el metodo guardarVarias de preguntasRepository
        //se lanze
        doThrow(IllegalArgumentException.class).when(preguntasRespository).guardarVarias(anyList());
        assertThrows(IllegalArgumentException.class, () -> {
            service.guardar(examen);
        });
    }


    //esta es otra forma de hacer el answer que esta mas arriba
    @Test
    void testDoAnswer() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);

        doAnswer(invocationOnMock -> {
            Long id = invocationOnMock.getArgument(0);
            return id == 5L ? Datos.PREGUNTAS : Collections.emptyList();
        }).when(preguntasRespository).findPreguntasPorExamen(anyLong());

        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
        assertEquals(5, examen.getPreguntas().size());
        assertTrue((examen.getPreguntas()).contains("geometria"));
        assertEquals(5L, examen.getId());
        assertEquals("Matematicas",examen.getNombre());
        assertEquals("Matematicas", examen.getNombre());
    }

    @Test
    void testGuardarExamen2(){
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);

        doAnswer(invocationOnMock ->  {

            Long secuencia = 8L;

            Examen examen = invocationOnMock.getArgument(0);
            examen.setId(secuencia++);
            System.out.println(secuencia);
            return examen;
        }).when(repository).guardar(any(Examen.class));

        Examen examen = service.guardar(Datos.EXAMEN);
        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("Fisica",examen.getNombre());
        verify(repository).guardar(any(Examen.class));
        verify(preguntasRespository).guardarVarias(anyList());
    }

    @Test
    void testDocallRealMethod() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntasRespository.findPreguntasPorExamen())
    }
}