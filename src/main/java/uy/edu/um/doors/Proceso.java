package uy.edu.um.doors;

import uy.edu.um.tad.list.MyList;

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

    //getters:
    public int getPID(){
        return this.PID;
    }

    public String getNombre(){
        return this.nombre;
    }

    public Usuario getUsuario(){
        return this.usuario;
    }

    public int getPrioridad(){
        return this.prioridad;
    }

    public EstadoProceso getEstado(){
        return this.estado;
    }

    public MyList<Evento> getEventos(){
        return this.eventos;
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
        // Implementación mínima: devolver la prioridad actual.
        // Completar según la especificación del TP si es necesario.
        return this.prioridad;
    }
    
    @Override
    public int compareTo(Proceso other) {
            return Integer.compare(this.prioridad, other.prioridad);
        }
}

