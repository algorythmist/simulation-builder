package com.tecacet.simulator.inventory;

import com.tecacet.simulator.SimulationEvent;

class InventoryEvent extends SimulationEvent {
    public static final int ORDER_ARRIVAL = 0;
    public static final int DEMAND = 1;
    public static final int EVALUATE = 2;

    private int orderAmount; // for order events
    private int demandAmount; // for demand orders

    public InventoryEvent(int type, double time) {
        super(type, time);
    }

    public InventoryEvent(int type, double time, int iAmount) {
        super(type, time);
        if (type == ORDER_ARRIVAL) {
            orderAmount = iAmount;
        } else if (type == DEMAND) {
            demandAmount = iAmount;
        }
    }

    public int getOrderAmount() {
        return orderAmount;
    }

    public int getDemandAmount() {
        return demandAmount;
    }

    @Override
    public String toString() {
        switch (type) {
        case ORDER_ARRIVAL:
            return "Arrival";
        case DEMAND:
            return "Demand";
        case EVALUATE:
            return "Evaluate";
        default:
            return "INVALID";
        }
    }
}
