/*
 * * Copyright (C) 2018-2020 Matt Baxter https://kitteh.org
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.kitteh.pastegg;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Narimm  on 4/03/2020.
 */
public class PasteManager {
    private static final Map<String, Paste> sessionPastes = new HashMap<>();
    private static String apiKey;

    /**
     * Return the API key.
     * @return the key
     */
    public static String getApiKey() {
        return apiKey;
    }

    /**
     * Set the managers API key.
     * @param key api Key
     */
    public static void setApiKey(String key) {
        apiKey = key;
    }

    /**
     * Clear all stored pastes.
     */
    public static void clearPastes() {
        sessionPastes.clear();
    }

    /**
     * Add a paste.
     * @param paste the paste to add
     * @return always null - Paste if the paste ID was already taken (should never happen.)
     */
    static Paste addPaste(Paste paste) {
        return sessionPastes.put(paste.getId(), paste);
    }

    /**
     * This method will search for a paste id made this session and remove it - either using a stored
     * deletion key OR the api key if provided - if neither are present it will return false.
     *
     * @param id the paste ID
     * @return boolean
     */
    public static boolean deletePaste(String id) {
        Paste paste = sessionPastes.get(id);
        if (paste == null) {
            return false;
        }
        String pasteKey;
        if (paste.getDeletionKey().isPresent()) {
            pasteKey = paste.getDeletionKey().get();
        } else {
            if (apiKey != null) {
                pasteKey = apiKey;
            } else {
                return false;
            }
        }
        return deletePaste(paste.getId(),pasteKey);
    }

    /**
     * Attempt to delete a paste.
     *
     * @param id String
     * @param deletionKey String
     * @return boolean
     */
    public static boolean deletePaste(String id,String deletionKey) {
        try {
            return ConnectionProvider.deletePaste(id, deletionKey);
        } catch (IOException e) {
            InvalidPasteException inv = new InvalidPasteException("Paste could not be deleted: "
                  + ConnectionProvider.getLastResponseCode());
            inv.addSuppressed(e);
            throw inv;
        }
    }
}
