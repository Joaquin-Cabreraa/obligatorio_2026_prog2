package uy.edu.um.doors;

import uy.edu.um.tad.list.MyLinkedListImpl;
import uy.edu.um.tad.list.MyList;

public class Proceso {
        private int PID;
        private String nombre;
        private Usuario usuario;
        private int prioridad;
        private EstadoProceso estado;
        private MyList<Evento> eventos;

        public Proceso(int PID, String nombre, Usuario usuario) {
            this.PID = PID;
            this.nombre = nombre;
            this.usuario = usuario;
            this.estado = EstadoProceso.NEW;
            this.prioridad = 0;
            this.eventos = new MyLinkedListImpl<>();
        }

        public int getPID() { return this.PID; }
        public String getNombre() { return this.nombre; }
        public Usuario getUsuario() { return this.usuario; }
        public int getPrioridad() { return this.prioridad; }
        public EstadoProceso getEstado() { return this.estado; }
        public MyList<Evento> getEventos() { return this.eventos; }

        public void setPrioridad(int p) { this.prioridad = p; }
        public void setEstado(EstadoProceso e) { this.estado = e; }

        public void agregarEvento(Evento e) {
            this.eventos.add(e);
        }

    //calcular prioridad:
    public int calcularPrioridad(){
        return;
        //COMPLETAR CODIGO.
    }
}