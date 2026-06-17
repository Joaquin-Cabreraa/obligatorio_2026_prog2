 package uy.edu.um.doors;

import uy.edu.um.tad.queue.EmptyQueueException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import uy.edu.um.tad.hash.MyHash;
import uy.edu.um.tad.hash.MyHashImpl;
import uy.edu.um.tad.heap.EmptyHeapException;
import uy.edu.um.tad.heap.MyHeap;
import uy.edu.um.tad.heap.MyHeapImpl;
import uy.edu.um.tad.list.MyLinkedListImpl;
import uy.edu.um.tad.list.MyList;
import uy.edu.um.tad.queue.MyQueue;
import uy.edu.um.tad.queue.MyQueueImpl;
import uy.edu.um.tad.stack.EmptyStackException;
import uy.edu.um.tad.stack.MyStack;
import uy.edu.um.tad.stack.MyStackImpl;

public class ProcessManagerImpl implements ProcessManager{
    private MyQueue<Proceso> procesosNew;
    private MyHeap<Proceso> procesosPending;
    private Proceso procesoEnEjecucion;
    private MyStack<Proceso> procesosFinished;
    private MyHash<Integer, Usuario> usuarios;
    private MyHash<Integer, Proceso> hashPids;

    public ProcessManagerImpl() {
        this.procesosNew = new MyQueueImpl<>();
        this.procesosPending = new MyHeapImpl<>(false);
        this.procesoEnEjecucion = null;
        this.procesosFinished = new MyStackImpl<>();
        this.usuarios = new MyHashImpl<>();
        this.hashPids = new MyHashImpl<>();
    }

    @Override
    public void loadProcessAndUserData(String processCsvPath, String usersCsvPath) {

        // 1) Cargar usuarios
        try (BufferedReader br = new BufferedReader(new FileReader(usersCsvPath))) {
            String line = br.readLine(); // saltar header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(";");
                int uid = Integer.parseInt(parts[0].trim());
                String alias = parts[1].trim();
                UserType type = UserType.valueOf(parts[2].trim().toUpperCase());

                // Evitar usuarios duplicados
                if (usuarios.contains(uid)) {
                    System.out.println("UID " + uid + " duplicado, saltando.");
                    continue;
                }

                usuarios.put(uid, new Usuario(uid, alias, type));
            }
        } catch (IOException e) {
            System.out.println("Error leyendo usuarios: " + e.getMessage());
            return;
        }

        // 2) Cargar procesos
        try (BufferedReader br = new BufferedReader(new FileReader(processCsvPath))) {
            String line = br.readLine(); // saltar header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Formato: PID;UID;nombre;{eventos}
                String[] parts = line.split(";", 4);
                int pid = Integer.parseInt(parts[0].trim());
                int uid = Integer.parseInt(parts[1].trim());
                String nombre = parts[2].trim();
                String eventosRaw = parts[3].trim();

                // Evitar procesos duplicados
                if (hashPids.contains(pid)) {
                    System.out.println("PID " + pid + " duplicado, saltando.");
                    continue;
                }

                // Buscar usuario dueño
                Usuario owner = usuarios.get(uid);
                if (owner == null) {
                    System.out.println("UID " + uid + " no encontrado, saltando PID " + pid);
                    continue;
                }

                Proceso proceso = new Proceso(pid, nombre, owner);

                // Parsear eventos: quitar { y }
                eventosRaw = eventosRaw.substring(1, eventosRaw.length() - 1);
                String[] eventTokens = eventosRaw.split("#");

                for (String token : eventTokens) {
                    token = token.trim();
                    int bracketOpen  = token.indexOf('[');
                    int bracketClose = token.indexOf(']');
                    String tipoPart  = token.substring(0, bracketOpen).replace(":", "").trim();
                    String instrPart = token.substring(bracketOpen + 1, bracketClose).trim();

                    TipoEvento tipo = TipoEvento.valueOf(tipoPart.toUpperCase());
                    MyList<String> instrucciones = new MyLinkedListImpl<>();
                    for (String instr : instrPart.split(",")) {
                        instrucciones.add(instr.trim());
                    }

                    proceso.agregarEvento(new Evento(tipo, instrucciones));
                }

                procesosNew.enqueue(proceso);
                hashPids.put(pid, proceso);
                System.out.println("Proceso cargado: PID=" + pid + " | " + nombre);
            }
        } catch (IOException e) {
            System.out.println("Error leyendo procesos: " + e.getMessage());
        }
    }

    @Override
    public void prepareProcesses() {
        while (!procesosNew.isEmpty()){
            try {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                Proceso p = procesosNew.dequeue();
                int prioridad = p.calcularPrioridad();
                p.setPrioridad(prioridad);
                p.setEstado(EstadoProceso.PENDING);
                procesosPending.insert(p);
                writeLog("[" + timestamp + "]: " + "NEW PENDING PROCESS: PID=" + p.getPID() + " | " + p.getNombre() + " | USER:" + p.getUsuario().getAlias() + " UID:" + p.getUsuario().getUid() + " | P=" + p.calcularPrioridad());
            } catch(EmptyQueueException e) {}
        }
    }

    @Override
    public void executeNextProcess() {
        if (procesoEnEjecucion != null) {
            System.out.println("Ya hay un proceso en ejecucion: PID=" + procesoEnEjecucion.getPID());
        } else if (procesosPending.isEmpty()) {
            System.out.println("No hay procesos pendientes");
        } else {
            try {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                Proceso max_proceso = procesosPending.remove();
                max_proceso.setEstado(EstadoProceso.RUNNING);
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
        procesoEnEjecucion.setEstado(EstadoProceso.FINISHED);
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
        //Muestra el proceso en ejecución, los pendientes y los finalizados
        System.out.println("PROCESS STATUS");

        //Mostrar el EXECUTING
        System.out.println("EXECUTING:");
        Proceso proceso = procesoEnEjecucion;
        System.out.print("        ");
        System.out.println("PID=" + proceso.getPID() + " | " + proceso.getNombre() + " | " + "USER:" + proceso.getUsuario().getAlias() + " UID:" + proceso.getUsuario().getUid() + " | " + "P=" + proceso.getPrioridad());

        //Mostrar el heap de PENDING
        System.out.println("PENDING:");
        MyHeap<Proceso> heapAux = new MyHeapImpl<>(false);
        try {
            while(!procesosPending.isEmpty()){
                Proceso p = procesosPending.remove();
                System.out.print("        ");
                System.out.println("PID=" + p.getPID() + " | " + p.getNombre() + " | USER:" + p.getUsuario().getAlias() + " UID:" + p.getUsuario().getUid() + " | " + "P=" + p.getPrioridad());
                heapAux.insert(p);
            }
            while(!heapAux.isEmpty()){
                procesosPending.insert(heapAux.remove());
            }
        } catch (EmptyHeapException e) {
            System.out.println("Error al recorrer pending: " + e.getMessage());
        }

        //Mostrar el stack de FINISHED
        MyStack<Proceso> stackAux = new MyStackImpl<>();
        System.out.println("FINISHED:");
        try {
            while(!procesosFinished.isEmpty()){
                Proceso p = procesosFinished.pop();
                System.out.print("      ");
                System.out.println("PID=" + p.getPID() + " | " + p.getNombre() + " | " + "STATE: " + p.getEstado() + " | " + "USER:" + p.getUsuario().getAlias() + " UID:" + p.getUsuario().getUid());
                stackAux.push(p);
            }
            while(!stackAux.isEmpty()){
                procesosFinished.push(stackAux.pop());
            }
        } catch (EmptyStackException e) {
            System.out.println("Error al recorrer finished: " + e.getMessage());
        }
    }

    @Override
    public void printStatusVerbose() {
        System.out.println("PROCESS STATUS");

        // EXECUTING
        System.out.println("EXECUTING:");
        Proceso p = procesoEnEjecucion;
        System.out.print("        ");
        System.out.println("PID=" + p.getPID() + " | " + p.getNombre() + " | " + "USER:" + p.getUsuario().getAlias() + " UID:" + p.getUsuario().getUid() + " | " + "P=" + p.getPrioridad());
        printEventos(p);

        // PENDING
        System.out.println("PENDING:");
        MyHeap<Proceso> heapAux = new MyHeapImpl<>(false);
        try {
            while(!procesosPending.isEmpty()){
                Proceso proc = procesosPending.remove();
                System.out.print("        ");
                System.out.println("PID=" + proc.getPID() + " | " + proc.getNombre() + " | USER:" + proc.getUsuario().getAlias() + " UID:" + proc.getUsuario().getUid() + " | " + "P=" + proc.getPrioridad());
                printEventos(proc);
                heapAux.insert(proc);
            }
            while(!heapAux.isEmpty()){
                procesosPending.insert(heapAux.remove());
            }
        } catch (EmptyHeapException e) {
            System.out.println("Error al recorrer pending: " + e.getMessage());
        }

        // FINISHED
        MyStack<Proceso> stackAux = new MyStackImpl<>();
        System.out.println("FINISHED:");
        try {
            while(!procesosFinished.isEmpty()){
                Proceso proc = procesosFinished.pop();
                System.out.print("        ");
                System.out.println("PID=" + proc.getPID() + " | " + proc.getNombre() + " | " + "STATE: " + proc.getEstado() + " | " + "USER:" + proc.getUsuario().getAlias() + " UID:" + proc.getUsuario().getUid());
                printEventos(proc);
                stackAux.push(proc);
            }
            while(!stackAux.isEmpty()){
                procesosFinished.push(stackAux.pop());
            }
        } catch (EmptyStackException e) {
            System.out.println("Error al recorrer finished: " + e.getMessage());
        }
    }

    private void printEventos(Proceso p) {
        MyList<Evento> eventos = p.getEventos();
        for (int i = 0; i < eventos.size(); i++) {
            Evento e = eventos.get(i);
            StringBuilder instrucciones = new StringBuilder("[");
            MyList<String> instr = e.getInstrucciones();
            for (int j = 0; j < instr.size(); j++) {
                instrucciones.append(instr.get(j));
                if (j < instr.size() - 1) {
                    instrucciones.append(", ");
                }
            }
            instrucciones.append("]");
            System.out.println("        EVENT: " + e.getTipo() + " | Instructions " + instrucciones);
        }
    }

    @Override
    public void printStatusByUser(int uid) {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatusByProcess(int pid) {
        Proceso p = hashPids.get(pid);
        if (p == null) {
            System.out.println("No se encontró proceso con PID=" + pid);
            return;
        }
        System.out.println("PID=" + p.getPID() + " | " + p.getNombre() 
            + " | USER:" + p.getUsuario().getAlias() 
            + " UID:" + p.getUsuario().getUid() 
            + " | P=" + p.getPrioridad());
        printEventos(p);
    }
    
    private void writeLog(String mensaje) {
        String fecha = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        try (FileWriter fw = new FileWriter("DOORS_PROCESS_LOG_" + fecha, true)) {
            fw.write(mensaje + "\n");
        } catch (IOException e) {}
    }
}

