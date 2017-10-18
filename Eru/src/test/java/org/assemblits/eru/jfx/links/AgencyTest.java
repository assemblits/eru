package org.assemblits.eru.jfx.links;

import org.assemblits.eru.contract.Agency;
import org.assemblits.eru.contract.Contract;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Created by marlontrujillo1080 on 10/16/17.
 */
public class AgencyTest {

    private Agency agency = new Agency();


    @Test
    public void testCanAddDifferentObjects() throws Exception {
        Contract contract = Mockito.mock(Contract.class);
        Boolean booleanObject = Boolean.FALSE;
        Integer integerObject = 0;
        Double doubleObject = 0.0;
        agency.add(booleanObject, contract);
        agency.add(integerObject, contract);
        agency.add(doubleObject, contract);

        assertTrue("Must accept different types.", agency.containsBusAgent(booleanObject));
        assertTrue("Must accept different types.", agency.containsBusAgent(integerObject));
        assertTrue("Must accept different types.", agency.containsBusAgent(doubleObject));
    }
}