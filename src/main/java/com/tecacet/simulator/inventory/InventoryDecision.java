package com.tecacet.simulator.inventory;

class InventoryDecision {
    private int orderAmount;

    public InventoryDecision(int amount) {
        orderAmount = amount;
    }

    public int getOrderAmount() {
        return orderAmount;
    }
}
