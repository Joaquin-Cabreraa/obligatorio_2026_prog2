package uy.edu.um.doors;
import uy.edu.um.tad.queue.EmptyQueueException;
import uy.edu.um.tad.queue.MyQueue;
import uy.edu.um.tad.queue.MyQueueImpl;
import uy.edu.um.tad.heap.EmptyHeapException;
import uy.edu.um.tad.heap.MyHeap;
import uy.edu.um.tad.heap.MyHeapImpl;
import uy.edu.um.tad.stack.MyStack;
import uy.edu.um.tad.stack.MyStackImpl;
import uy.edu.um.tad.hash.MyHash;
import uy.edu.um.tad.hash.MyHashImpl;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ProcessManagerImpl implements ProcessManager{
    private MyQueue<Proceso> procesosNew;
    private MyHeap<Proceso> procesosPending;
    private Proceso procesoEnEjecucion;
    private MyStack<Proceso> procesosFinished;
    private MyHash<Integer, Usuario> usuarios;

    public ProcessManagerImpl() {
        this.procesosNew = new MyQueueImpl<>();
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
        while (!procesosNew.isEmpty()){
            try {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                Proceso p = procesosNew.dequeue();
                int prioridad = p.calcularPrioridad();
                p.setPrioridad(prioridad);
                procesosPending.insert(p);
                writeLog("[" + timestamp + "]: " + "NEW PENDING PROCESS: PID=" + p.getPID() + " | " + p.getNombre() + " | USER:" + p.getUsuario().getAlias() + " UID:" + p.getUsuario().getUid() + " | P=" + p.calcularPrioridad());
            } catch(EmptyQueueException e) {}
        }

        
    }

    @Override
    public void executeNextProcess() {
        while (!procesosPending.isEmpty()) {
            try {
                if (procesoEnEjecucion == null) {
                    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    Proceso max_proceso = procesosPending.remove();
                    procesoEnEjecucion = max_proceso;

                    writeLog("[" + timestamp + "]: EXECUTING PROCESS: PID=" + max_proceso.getPID() + " | USER:" + max_proceso.getUsuario().getAlias() + " UID:" + max_proceso.getUsuario().getUid());

                    for (int i = 0; i < max_proceso.getEventos().size(); i++) {
                        Evento event = max_proceso.getEventos().get(i);
                        String instrucciones = "";
                        for (int j = 0; j < event.getInstrucciones().size(); j++) {
                            if (j > 0) instrucciones += ", ";
                            instrucciones += event.getInstrucciones().get(j);
                        }
                        writeLog("  EVENT: " + event.getTipo() + " | Instructions [" + instrucciones + "]");
                    }
                }  
            } catch (EmptyHeapException e) {}
        }
    }

    @Override
    public void finishProcessOk() {
        if(procesoEnEjecucion == null){
            System.out.printf("No hay proceso en ejecucion");
            return;
        }
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        procesosFinished.push(procesoEnEjecucion);
        writeLog("[" + timestamp + "]: ENDING PROCESS: PID=" + procesoEnEjecucion.getPID() + " | STATE: OK");
        procesoEnEjecucion = null;
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
        System.out.println("IMPLEMENTAR");
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
    private void writeLog(String mensaje) {
        String fecha = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        try (FileWriter fw = new FileWriter("DOORS_PROCESS_LOG_" + fecha, true)) {
            fw.write(mensaje + "\n");
        } catch (IOException e) {}
    }
}

