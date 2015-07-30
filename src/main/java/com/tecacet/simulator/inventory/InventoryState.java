package com.tecacet.simulator.inventory;

import com.tecacet.simulator.SimulationException;

public class InventoryState {
    private int inventoryLevel;

    public InventoryState(int initialInventoryLevel) {
        inventoryLevel = initialInventoryLevel;
    }

    public int getInventory() {
        return inventoryLevel;
    }

    public void update(InventoryEvent event, InventoryDecision decision) throws SimulationException {

        switch (event.getType()) {

        case InventoryEvent.ORDER_ARRIVAL:
            inventoryLevel += event.getOrderAmount();
            break;
        case InventoryEvent.DEMAND:
            /* SPECK seems to allow negative inventory */
            inventoryLevel -= event.getDemandAmount();
            /*
             * 666 (zEvent.getDemandAmount() > m_iInventoryLevel)?
             * m_iInventoryLevel : zEvent.getDemandAmount();
             */
            break;
        case InventoryEvent.EVALUATE:
            // doesn't change the state
            break;
        default:
            throw new SimulationException("Invalid event type");
        } // end switch
    }
    
    @Override
    public String toString() {
        return Integer.toString(inventoryLevel);
    }

}
