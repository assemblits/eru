package org.assemblits.eru.jfx.links;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Created by marlontrujillo1080 on 10/16/17.
 */
public class LinksContainerTest {

    private LinksContainer linksContainer = new LinksContainer();


    @Test
    public void testCanAddDifferentObjects() throws Exception {
        Linker linker = Mockito.mock(Linker.class);
        Boolean booleanObject = Boolean.FALSE;
        Integer integerObject = 0;
        Double doubleObject = 0.0;
        linksContainer.addLink(booleanObject, linker);
        linksContainer.addLink(integerObject, linker);
        linksContainer.addLink(doubleObject, linker);

        assertTrue("Must accept different types.", linksContainer.containsLinksFor(booleanObject));
        assertTrue("Must accept different types.", linksContainer.containsLinksFor(integerObject));
        assertTrue("Must accept different types.", linksContainer.containsLinksFor(doubleObject));
    }
}