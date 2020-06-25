package org.kitteh.pastegg;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Narimm on 4/03/2020.
 */
public class PasteFileTest {
    private PasteFile test;
    @Before
    public void Setup(){
        test = new PasteFile("1","Test",
              new PasteContent(PasteContent.ContentType.TEXT,"HELLO WORLD"));
    }
    @Test
    public void getContent() {
        assertEquals(test.getContent().getValue(),"HELLO WORLD");
    }

    @Test
    public void getId() {
        assertEquals(test.getId(),"1");
    }

    @Test
    public void getName() {
        assertEquals("Test",test.getName());
    }
}