package uy.edu.um.tad;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import uy.edu.um.doors.*;
import uy.edu.um.tad.list.MyLinkedListImpl;
import uy.edu.um.tad.list.MyList;


public class ProcesoTest {
    @Test
    public void testCalcularPrioridad(){
        //ARRANGE (parte donde se preparan los datos necesarios)
        Usuario usuario = new Usuario(1, "admin", UserType.ADMIN);

        //Crear eventos
        MyList<String> instrucciones = new MyLinkedListImpl<>();
        instrucciones.add("sum");

        Evento eventoCPU1 = new Evento(TipoEvento.CPU, instrucciones);
        Evento eventoCPU2 = new Evento(TipoEvento.CPU, instrucciones);
        Evento eventoRAM = new Evento(TipoEvento.RAM, instrucciones);
        Evento eventoDISK = new Evento(TipoEvento.DISK, instrucciones);

        //Crear proceso
        Proceso proceso = new Proceso(1, "notepad.exe", usuario);
        proceso.agregarEvento(eventoCPU1);
        proceso.agregarEvento(eventoCPU2);
        proceso.agregarEvento(eventoRAM);
        proceso.agregarEvento(eventoDISK);

        //ACT (parte donde se llama el metodo que queremos testear)
        int resultado = proceso.calcularPrioridad();

        //ASSERT (se verifica que el resultado es el esperado, en este caso tiene que dar 133)s
        assertEquals(133,resultado);
    }

    @Test
    public void testCalcularPrioridadSinEventos(){
        //ARRANGE
        Usuario usuario = new Usuario(3, "admin", UserType.ADMIN);
        Proceso proceso = new Proceso(3,"notepad.exe", usuario);
        //no se agregan eventos, por lo que la lista queda vacia

        //ACT
        int resultado = proceso.calcularPrioridad();

        //ASSERT
        assertEquals(0,resultado);
    }
}
