package com.tecacet.simulator.bank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.tecacet.simulator.bank.BankCustomer;
import com.tecacet.simulator.bank.Teller;

public class TellerTest {

    @Test
    public void testQueueSize() {
        Teller teller = new Teller();
        teller.addCustomer(new BankCustomer(10.0));
        assertEquals(1,teller.queueSize());
        assertNotNull(teller.getCustomerInService());
        assertTrue(teller.isBusy());
    }
    

    @Test
    public void testRemove() {
        Teller teller = new Teller();
        BankCustomer bankCustomer1 = new BankCustomer(10.0);
        BankCustomer bankCustomer2 = new BankCustomer(15.0);
        BankCustomer bankCustomer3 = new BankCustomer(17.0);
        teller.addCustomer(bankCustomer1); 
        teller.addCustomer(bankCustomer2);
        teller.addCustomer(bankCustomer3);
        assertEquals(bankCustomer1, teller.getCustomerInService());
        teller.removeFirstCustomer();
        assertEquals(2,teller.queueSize());
        assertEquals(bankCustomer2, teller.getCustomerInService());
        teller.removeLastCustomer();
        assertEquals(bankCustomer2, teller.getCustomerInService());
    }
    

}
