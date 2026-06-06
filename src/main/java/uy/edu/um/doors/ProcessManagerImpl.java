package uy.edu.um.doors;
import uy.edu.um.tad.hash.MyHash;
import uy.edu.um.tad.hash.MyHashImpl;
import uy.edu.um.tad.list.MyLinkedListImpl;
import uy.edu.um.tad.list.MyList;
import uy.edu.um.tad.queue.MyQueue;
import uy.edu.um.tad.queue.MyQueueImpl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ProcessManagerImpl implements ProcessManager{
    private MyQueue<Proceso> procesosNew = new MyQueueImpl<>();
    private MyHash<Integer, Usuario> usuarios = new MyHashImpl<>();
    private MyHash<Integer, Integer> hashPids;

    //EL DISEÑO DE LA ESTRUCTURA DE ALMACENAMIENTO DEBE IMPLEMENTARSE EN ESTA CLASE EN RELACIÓN CON LAS ENTIDADES QUE DEFINA

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
                loadedPids.put(pid, true);
                System.out.println("Proceso cargado: PID=" + pid + " | " + nombre);
            }
        } catch (IOException e) {
            System.out.println("Error leyendo procesos: " + e.getMessage());
        }
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
}
