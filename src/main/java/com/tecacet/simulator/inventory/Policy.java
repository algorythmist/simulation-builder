package com.tecacet.simulator.inventory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Policy {
    
    protected final Logger logger = LoggerFactory.getLogger(Policy.class);

    protected int threshold = 20;
    protected int maxStorage = 40;

    public Policy(int threshold, int maxStorage) {
        this.threshold = threshold;
        this.maxStorage = maxStorage;
    }

    public void setThreshold(int threshold) {
        if (threshold >= 0) {
            this.threshold = threshold;
        } else {
            logger.warn("Neggative threshold not allowed. Theshold unchanged.");
        }
    }

    public void setMaxStorage(int storage) {
        if (storage >= 0) {
            maxStorage = storage;
        } else {
            logger.warn("Neggative storage value not allowed. Maximum storage unchanged.");
        }
    }

    public int getThreshold() {
        return threshold;
    }

    public int getMaxStorage() {
        return maxStorage;
    }

    public InventoryDecision getDecision(InventoryState state) {
        int amount = 0;

        if (state.getInventory() < threshold) {
            amount = maxStorage - state.getInventory();
        }
        logger.debug("Controller decided to order {} items.", amount);
        return new InventoryDecision(amount);
    }
}
