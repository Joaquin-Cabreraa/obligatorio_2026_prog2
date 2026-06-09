package uy.edu.um.doors;
import uy.edu.um.tad.list.MyList;

public class Evento{
    private TipoEvento tipo;
    private MyList<String> instrucciones;

    //constructor:
    public Evento(TipoEvento tipo, MyList<String> instrucciones){
        this.tipo = tipo;
        this.instrucciones = instrucciones;
    }

    //getters:
    public TipoEvento getTipo() {
        return tipo;
    }

    public MyList<String> getInstrucciones() {
        return instrucciones;
    }
}