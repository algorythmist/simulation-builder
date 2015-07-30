package com.tecacet.simulator.bank;

import junit.framework.TestCase;

import com.tecacet.simulator.Simulator;
import com.tecacet.simulator.bank.Bank;
import com.tecacet.simulator.bank.BankCustomer;
import com.tecacet.simulator.bank.BankEvent;
import com.tecacet.simulator.bank.BankState;

public class BankStateTest extends TestCase {

    public BankStateTest(String sName) {
        super(sName);
    }

    public void testBankState() throws Exception {
        BankState state = new BankState(3);
        BankCustomer c1 = new BankCustomer((0.0));
        BankEvent event = new BankEvent(BankEvent.ARRIVAL, 0.0, c1);
        state.update(event);
        assertEquals(event.getCustomer().getTeller(), 0);

        BankCustomer c2 = new BankCustomer((0.2));
        event = new BankEvent(BankEvent.ARRIVAL, 0.2, c2);
        state.update(event);
        assertEquals(event.getCustomer().getTeller(), 1);

        BankCustomer c3 = new BankCustomer((1.2));
        event = new BankEvent(BankEvent.ARRIVAL, 1.2, c3);
        state.update(event);
        assertEquals(event.getCustomer().getTeller(), 2);

        BankCustomer c4 = new BankCustomer((1.3));
        event = new BankEvent(BankEvent.ARRIVAL, 1.3, c4);
        state.update(event);
        assertEquals(event.getCustomer().getTeller(), 0);

        BankCustomer c5 = new BankCustomer((1.5));
        event = new BankEvent(BankEvent.ARRIVAL, 1.5, c5);
        state.update(event);
        assertEquals(event.getCustomer().getTeller(), 1);

        BankCustomer c6 = new BankCustomer((1.6));
        event = new BankEvent(BankEvent.ARRIVAL, 1.6, c6);
        state.update(event);
        assertEquals(event.getCustomer().getTeller(), 2);

        BankCustomer c7 = new BankCustomer((1.7));
        event = new BankEvent(BankEvent.ARRIVAL, 1.7, c7);
        state.update(event);
        assertEquals(event.getCustomer().getTeller(), 0);
        assertEquals(state.getTeller(0).queueSize(), 3);

        // test for jockey
        event = new BankEvent(BankEvent.DEPARTURE, 1.5, c2);
        state.update(event);
        assertEquals(2,state.getTeller(0).queueSize());
        assertEquals(2,state.getTeller(1).queueSize());
    }

    public void testFourTellers() throws Exception {
        final Bank bank = new Bank(4);
        
        Simulator<BankState> simulator = new Simulator<BankState>(bank,bank);
        simulator.runSimulation();
        //TODO System.out.println(bank.customerDelay.getMean());
        //TODO this produces extremely unstable results
        //assertTrue(bank.customerDelay.getAverage() > 21  && bank.customerDelay.getAverage() < 24);
    }

}
