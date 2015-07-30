package com.tecacet.simulator.bank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BankState {
    private final Logger logger = LoggerFactory.getLogger(BankState.class);
    
    private Teller[] tellers;

    /**
     * teller with shortest queue
     */
    private int bestTeller = 0;

    /**
     * flag indicating that the bank closed it's doors and no more arrivals are
     * possible
     */
    private boolean bankClosed = false;

    public BankState(int iTellers) {
        logger.debug("The bank has " + iTellers + " tellers");
        tellers = new Teller[iTellers];
        for (int i = 0; i < iTellers; i++) {
            tellers[i] = new Teller();
        }
    }

    public boolean isBankClosed() {
        return bankClosed;
    }

    public boolean hasCustomers() {
        for (Teller teller : tellers) {
            if (teller.isBusy()) {
                return true;
            }
        }
        return false;
    }

    public int getNumberInQueue() {
        int number = 0;
        for (Teller teller : tellers) {
            number += teller.queueSize();
        }
        return number;
    }

    public void setBankClosed(boolean b) {
        bankClosed = b;
    }

    public Teller getTeller(int teller) {
        return tellers[teller];
    }

    private Teller getBestTeller() {
        return tellers[bestTeller];
    }

    public BankCustomer getCustomerInService(int tellerIndex) {
        return tellers[tellerIndex].getCustomerInService();
    }

    public void update(BankEvent event) {
        Teller teller;

        switch (event.getType()) {

        case BankEvent.ARRIVAL:
            // Add customer to shortest queue.If possible start service.
            teller = getBestTeller();
            event.getCustomer().setTeller(bestTeller);
            teller.addCustomer(event.getCustomer());
            report("Arrival of customer " + event.getCustomer() + " at teller " + bestTeller);
            bestTeller();
            break;
        case BankEvent.DEPARTURE:
            // remove customer. Start service on next
            int tellerIndex = event.getCustomer().getTeller();
            teller = getTeller(tellerIndex);
            report("Customer " + event.getCustomer() + " departs from teller " + tellerIndex);
            // puts the next customer in service
            teller.removeFirstCustomer();
            /*
             * upon departure, the current teller is the only possible candidate
             * for best teller
             */
            if (leftIsBetter(tellerIndex, bestTeller)) {
                bestTeller = tellerIndex;
                jockey();
            }
            break;
        case BankEvent.BANK_CLOSES:
            report("The Bank closes at " + event.getTime());
            break;
        }
    } // update

    // find bestTeller, the teller with the shortest queue
    private void bestTeller() {
        for (int i = 0; i < tellers.length; i++) {
            if (leftIsBetter(i, bestTeller)) {
                bestTeller = i;
            }
        }
        logger.debug("The best teller is " + bestTeller);
    }

    private boolean leftIsBetter(int left, int right) {
        if (left == right)
            return true; // checking teller against itself

        Teller leftTeller = tellers[left];
        Teller rightTeller = tellers[right];
        return ((leftTeller.queueSize() < rightTeller.queueSize()) || (leftTeller.queueSize() == rightTeller
                .queueSize() && left < right)); // enforce
        // the 'leftmost' rule
    }

    private void jockey() {
        for (int i = 1; i < tellers.length; i++) {
            // look i places to the left
            int index = bestTeller - i;
            if (index >= 0) {
                if (tellers[index].queueSize() > tellers[bestTeller].queueSize() + 1) {
                    jockey(index, bestTeller);
                    break;
                }
            }
            // look i places to the right
            index = bestTeller + i;
            if (index < tellers.length) {
                if (tellers[index].queueSize() > tellers[bestTeller].queueSize() + 1) {
                    jockey(index, bestTeller);
                    break;
                }
            }
        } // end for
    }

    private void jockey(int from, int to) {
        BankCustomer customer = tellers[from].removeLastCustomer();
        report("Customer " + customer + " jockeys from teller " + from + " to teller " + to);
        tellers[to].addCustomer(customer);
        bestTeller();
    }

    protected void report(String message) {
        logger.debug(message);
    }
}