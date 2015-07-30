package com.tecacet.simulator;

public class SimulationException extends Exception {

    private static final long serialVersionUID = 6868880967150806582L;

    public SimulationException(String s) {
        super(s);
    }

    public SimulationException(Exception e) {
        super(e);
    }

}
