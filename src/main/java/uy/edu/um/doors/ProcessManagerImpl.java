package uy.edu.um.doors;

import uy.edu.um.tad.hash.MyHash;
import uy.edu.um.tad.hash.MyHashImpl;
import uy.edu.um.tad.heap.MyHeap;
import uy.edu.um.tad.heap.MyHeapImpl;
import uy.edu.um.tad.queue.MyQueue;
import uy.edu.um.tad.queue.MyQueueImpl;
import uy.edu.um.tad.stack.MyStack;
import uy.edu.um.tad.stack.MyStackImpl;

public class ProcessManagerImpl implements ProcessManager{

    //EL DISEÑO DE LA ESTRUCTURA DE ALMACENAMIENTO DEBE IMPLEMENTARSE EN ESTA CLASE EN RELACIÓN CON LAS ENTIDADES QUE DEFINA
    private MyQueue<Proceso> procesosNew;
    private MyHeap<Proceso> procesosPending;
    private Proceso procesoEnEjecucion;
    private MyStack<Proceso> procesosFinished;
    private MyHash<Integer,Usuario> usuarios;

    public ProcessManagerImpl(){
        this.procesosNuevos = new MyQueueImpl<>();
        this.procesosPending = new MyHeapImpl<>(false);
        this.procesoEnEjecucion = null;
        this.procesosFinished = new MyStackImpl<>();
        this.usuarios = new MyHashImpl<>();
    }
    @Override
    public void loadProcessAndUserData(String processCsvPath, String usersCsvPath) {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void prepareProcesses() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void executeNextProcess() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void finishProcessOk() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void finishProcessError() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void terminateProcess(int uid) {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatus() {
        //Muestra el proceso en ejecución, los pendientes y los finalizados
        System.out.println("PROCESS STATUS");

        //Mostrar el EXECUTING
        System.out.println("EXECUTING:");
        Proceso p = procesoEnEjecucion;
        System.out.print("        ");
        System.out.println("PID=" + p.getPID() + " | " + p.getNombre() + " | " + "USER:" + p.getUsuario().getType() + " " + p.getUsuario().getUid() + " | " + "P=" + p.getPrioridad());

        //Mostrar el heap de PENDING
        System.out.println("PENDING:");
            //HACER UN HEAP AUXILIAR QUE SEA UNA COPIA DEL PRIMERO, Y TRABAJAR CON ESO. PARA ASÍ NO MODIFICO EL HEAP ORIGINAL
        MyHeap<Proceso> aux = new MyHeapImpl<>(false);
        while(!procesosPending.isEmpty()){
            Proceso p = procesosPending.remove();
            System.out.println("PID=" + p.getPID() + " | " + p.getNombre() + " | USER:" + p.getUsuario().getAlias() + " UID:" + p.getUsuario().getUid() + " | P=" + p.getPrioridad());
            aux.insert(p);

            while(!aux.isEmpty()){
                procesosPending.insert(aux.remove());
            }
        }

        //Mostrar el stack de FINISHED
        MyStack<Proceso> aux = new MyStackImpl<>();
        System.out.println("FINISHED:");
        while(!procesosFinished.isEmpty()){
            Proceso p = procesosFinished.pop();
            System.out.print("      ");
            System.out.println("PID=" + p.getPID() + " " + p.getNombre() + " | STATE: " + p.getEstado() + " | USER:" + p.getUsuario().getAlias() + " UID:" + p.getUsuario().getUid());
            aux.push(p);
        }
        while(!aux.isEmpty()){
            procesosFinished.push(aux.pop());
        }
    }

    @Override
    public void printStatusVerbose() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatusByUser(int uid) {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatusByProcess(int pid) {
        System.out.println("IMPLEMENTAR");
    }
}
