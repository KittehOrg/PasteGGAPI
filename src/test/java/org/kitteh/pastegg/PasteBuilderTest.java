package org.kitteh.pastegg;

import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.logging.Logger;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Narimm on 28/02/2020.
 */
public class PasteBuilderTest {

    @Test
    public void build() {
        PasteContent content = new PasteContent(PasteContent.ContentType.TEXT, "HELLO WORLD");
        assertEquals(content.getValue(),"HELLO WORLD");
        ZonedDateTime time = ZonedDateTime.now().plusMinutes(1);
        PasteBuilder.PasteResult result = new PasteBuilder().name("TEST!").addFile(
              new PasteFile("jkcclemens.txt",
                    new PasteContent(PasteContent.ContentType.TEXT, "HELLO WORLD")))
              .visibility(Visibility.UNLISTED)
              .expires(time)
              .debug(true)
              .build();
        assertTrue(result.getPaste().isPresent());
        assertFalse(result.getMessage().isPresent());
        Paste paste = result.getPaste().get();
        assertNotNull(paste.getId());
        assertNotNull(paste.getExpires());
        assert(paste.getVisibility().equals(Visibility.UNLISTED));
        assertTrue(paste.getDeletionKey().isPresent());
        Integer val = 201;
        assertEquals(val,ConnectionProvider.getLastResponseCode());
        try {
            boolean yes = ConnectionProvider.deletePaste(paste.getId(), paste.getDeletionKey().get());
            assertTrue(yes);
        }catch (IOException ignored){}
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY,1);
        time = ZonedDateTime.ofInstant(cal.toInstant(),cal.getTimeZone().toZoneId());
        Date localDate = cal.getTime();
        PasteBuilder builder = new PasteBuilder().name("TEST!").addFile(
              new PasteFile("jkcclemens.txt",
                    new PasteContent(PasteContent.ContentType.TEXT, "HELLO WORLD")))
              .visibility(Visibility.UNLISTED)
              .expires(time);
        PasteBuilder.PasteResult result1 = builder.build();
        assertTrue(result1.getPaste().isPresent());
        assertEquals(localDate.toString(),result1.getPaste().get().getExpires().toString());
        assertNotNull(result1.getPaste().get().getCreatedAt());
        assertNotNull(result1.getPaste().get().getUpdatedAt());
        assertEquals(result1.getPaste().get().getCreatedAt().toString(),
              result1.getPaste().get().getUpdatedAt().toString());
    }

    @Test
    public void buildWithAuthentication(){
        try {
            String apiKey = System.getenv("PasteGGAPIKey");
            if((apiKey == null) || apiKey.length()>0) {
                Logger.getAnonymousLogger().info("No ApI Key given for testing");
                return;
            }
            PasteContent content = new PasteContent(PasteContent.ContentType.TEXT,"Hello World");
            PasteBuilder.PasteResult result = new PasteBuilder()
                  .name("Test")
                  .addFile(new PasteFile("Test.txt",content))
                  .setApiKey(apiKey)
                  .visibility(Visibility.PRIVATE)
                  .debug(true)
                  .build();
            assertNotNull(result);
            assertTrue(result.getPaste().isPresent());
            Paste paste = result.getPaste().get();
            assert(paste.getVisibility().equals(Visibility.PRIVATE));
            assertFalse(paste.getDeletionKey().isPresent());
            assertNotNull(paste.getId());
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }
    @Test(expected = InvalidPasteException.class)
    public void buildNullKeyPrivate(){
        InvalidPasteException e =  new InvalidPasteException("Some Message");
        assertEquals("Some Message", e.getMessage());
        PasteContent content = new PasteContent(PasteContent.ContentType.TEXT,"Hello World");
        new PasteBuilder()
              .name("Test")
              .addFile(new PasteFile("Test.txt",content))
              .setApiKey(null)
              .visibility(Visibility.PRIVATE)
              .build();
    }



    @Test(expected = InvalidPasteException.class)
    public void buildWithBadKey(){
        PasteContent content = new PasteContent(PasteContent.ContentType.TEXT,"Hello World");
        new PasteBuilder()
              .name("Test")
              .addFile(new PasteFile("Test.txt",content))
              .setApiKey("someKey")
              .visibility(Visibility.PRIVATE)
              .build();
    }
    @Test
    public void testPasteResult(){
        PasteBuilder.PasteResult pasteResult = new PasteBuilder.PasteResult();
        assertFalse(pasteResult.getMessage().isPresent());
    }
}
