package uy.edu.um.doors;
import lombok.Getter;
import uy.edu.um.tad.list.MyList;
import uy.edu.um.tad.list.Node;

@Getter
public class Proceso implements Comparable<Proceso>{
    private int PID;
    private String nombre;
    private Usuario usuario;
    private int prioridad;
    private EstadoProceso estado;
    private MyList<Evento> eventos;

    //constructor:
    public Proceso(int PID, String nombre, Usuario usuario, MyList<Evento> eventos){
        this.PID = PID;
        this.nombre = nombre;
        this.usuario = usuario;
        this.eventos = eventos;
        this.estado = EstadoProceso.NEW;
        this.prioridad = 0; //se calcula al pasar a PENDING con pprepare
    }

    //setters:
    public void setPrioridad(int p){
        this.prioridad = p;
    }

    public void setEstado(EstadoProceso e){
        this.estado = e;
    }

    //calcular prioridad:
    public int calcularPrioridad(){
        int nCPU = 0;
        int nRAM = 0;
        int nDISK = 0;
        int nTotal = 0;

        Node<Evento> actual = this.getEventos().getFirst();

        while (actual!=null){
            Evento evento = actual.getValue();

            if (evento.getTipo() == TipoEvento.CPU){
                nCPU++;
            } else if (evento.getTipo() == TipoEvento.RAM){
                nRAM++;
            } else if (evento.getTipo() == TipoEvento.DISK){
                nDISK++;
            }

            actual = actual.getNext();
        }

        int nEventos = this.getEventos().size();
        if (nEventos == 0){
            return 0;
        }

        int WUser = 16;
        if (this.getUsuario().getType() == UserType.ADMIN){
            WUser = 32;
        }

        double izq = ((8 * nCPU) + (2 * nRAM) + (2 * nDISK)) / (double) nEventos;
        double der = WUser * nEventos;

        return (int) (izq + der);
    }

    @Override
    public int compareTo(Proceso otro) {
        return Integer.compare(otro.getPrioridad(), this.getPrioridad());
    }
    
    @Override
    public int compareTo(Proceso other) {
            return Integer.compare(this.prioridad, other.prioridad);
        }
}

