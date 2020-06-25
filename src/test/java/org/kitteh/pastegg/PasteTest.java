package org.kitteh.pastegg;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by Narimm on 4/03/2020.
 */
public class PasteTest {
    private Paste paste;
    @Before
    public void Setup(){
        paste = new Paste("1");
    }

    @Test
    public void getId() {
        assertEquals("1",paste.getId());
    }

    @Test
    public void getDeletionKey() {
        assertFalse(paste.getDeletionKey().isPresent());
    }

    @Test
    public void getVisibility() {
        assertEquals(Visibility.PUBLIC,paste.getVisibility());
    }
}