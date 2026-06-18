package uy.edu.um.tad;

import org.junit.jupiter.api.Test;
import uy.edu.um.doors.*;

import static org.junit.jupiter.api.Assertions.*;

public class ProcessManagerImplTest {

    @Test
    public void prepareProcessesTest(){
        //ARRANGE
        ProcessManagerImpl manager = new ProcessManagerImpl();
        Usuario usuario = new Usuario(1,"admin", UserType.ADMIN);
        Proceso proceso = new Proceso(1, "notepad.exe", usuario);

        manager.getProcesosNew().enqueue(proceso);

        //ACT
        manager.prepareProcesses();

        //ASSERT
        assertTrue(manager.getProcesosNew().isEmpty()); //la cola de new tiene que estar vacia
        assertEquals(EstadoProceso.PENDING, proceso.getEstado()); //el estado tuvo que haber pasado a PENDING
        assertFalse(manager.getProcesosPending().isEmpty()); //el heap de pending tiene que tener el proceso
    }

    @Test
    public void executeNextProcessTest(){
        //ARRANGE
        ProcessManagerImpl manager = new ProcessManagerImpl();
        Usuario usuario = new Usuario(1,"admin", UserType.ADMIN);
        Proceso proceso1 = new Proceso(1, "notepad.exe", usuario);
        Proceso proceso2 = new Proceso(2, "notepad.exe",usuario);
        proceso1.setPrioridad(100);
        proceso2.setPrioridad(150);
        proceso1.setEstado(EstadoProceso.PENDING);
        proceso2.setEstado(EstadoProceso.PENDING);

        manager.getProcesosPending().insert(proceso1);
        manager.getProcesosPending().insert(proceso2);

        //ACT
        manager.executeNextProcess();

        //ASSERT
        assertNotNull(manager.getProcesoEnEjecucion()); //tiene que estar el proceso2 en ejecucion
        assertEquals(proceso2, manager.getProcesoEnEjecucion()); //el proceso2 es el que tiene que estar en ejecucion
        assertEquals(EstadoProceso.PENDING, proceso1.getEstado()); //el estado de proceso1 tiene que ser PENDING
        assertEquals(EstadoProceso.RUNNING, proceso2.getEstado()); //el estado de proceso2 tiene que ser RUNNING
    }

    @Test
    public void finishProcessOkTest(){
        // ARRANGE
        ProcessManagerImpl manager = new ProcessManagerImpl();
        Usuario usuario = new Usuario(1, "admin", UserType.ADMIN);
        Proceso proceso = new Proceso(1, "notepad.exe", usuario);
        proceso.setEstado(EstadoProceso.RUNNING);
        manager.setProcesoEnEjecucion(proceso);

        // ACT
        manager.finishProcessOk();

        // ASSERT
        assertNull(manager.getProcesoEnEjecucion());
        assertEquals(EstadoProceso.FINISHED, proceso.getEstado());
        assertFalse(manager.getProcesosFinished().isEmpty());
    }

    @Test
    public void finishProcessErrorTest(){
        //ARRANGE
        ProcessManagerImpl manager = new ProcessManagerImpl();
        Usuario usuario = new Usuario(1, "admin", UserType.ADMIN);
        Proceso proceso = new Proceso(1, "notepad.exe", usuario);
        proceso.setEstado(EstadoProceso.RUNNING);
        manager.setProcesoEnEjecucion(proceso);

        //ACT
        manager.finishProcessError();

        //ASERT
        assertNull(manager.getProcesoEnEjecucion());
        assertEquals(EstadoProceso.FINISHED,proceso.getEstado());
        assertFalse(manager.getProcesosFinished().isEmpty());
    }

    @Test
    public void terminateProcessTest(){
        // ARRANGE
        ProcessManagerImpl manager = new ProcessManagerImpl();
        Usuario admin = new Usuario(1, "admin", UserType.ADMIN);
        Usuario terminador = new Usuario(2, "terminador", UserType.ADMIN);
        Proceso proceso = new Proceso(1, "notepad.exe", admin);
        proceso.setEstado(EstadoProceso.RUNNING);
        manager.setProcesoEnEjecucion(proceso);
        manager.getUsuarios().put(2, terminador);

        // ACT
        manager.terminateProcess(2);

        // ASSERT
        assertNull(manager.getProcesoEnEjecucion());
        assertEquals(EstadoProceso.FINISHED, proceso.getEstado());
        assertFalse(manager.getProcesosFinished().isEmpty());
    }
}
