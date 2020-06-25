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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public class PasteBuilder {

    @SuppressWarnings("unused")
    public static class PasteResult {

        private String status;
        private Paste result;
        private String message;


        public Optional<Paste> getPaste() {
            return Optional.ofNullable(this.result);
        }

        public Optional<String> getMessage() {
            return Optional.ofNullable(this.message);
        }

        public String getStatus() {
            return status;
        }
    }

    private Visibility visibility = Visibility.getDefault();
    private String name;
    private String expires = null;
    private boolean debug = false;
    private String apiKey;
    @SuppressWarnings({"TypeMayBeWeakened", "MismatchedQueryAndUpdateOfCollection"})
    private final List<PasteFile> files = new LinkedList<>();

    public PasteBuilder name(String name) {
        this.name = name;
        return this;
    }

    // ZonedDateTime.now( ZoneOffset.UTC ).plusSeconds(10)
    public PasteBuilder expires(ZonedDateTime when) {
        this.expires = when == null ? null : when.format(DateTimeFormatter.ISO_INSTANT);
        return this;
    }

    public PasteBuilder setApiKey(String key) {
        this.apiKey = key;
        return this;
    }

    public PasteBuilder visibility(Visibility visibility) {
        this.visibility = visibility;
        return this;
    }

    /**
     * debug the connection.
     * @param debug boolean
     * @return PasteBuilder
     */
    public PasteBuilder debug(boolean debug) {
        this.debug = debug;
        return this;
    }


    public PasteBuilder addFile(PasteFile file) {
        files.add(file);
        return this;
    }

    public PasteResult build() throws InvalidPasteException {
        if (visibility == Visibility.PRIVATE && apiKey == null) {
            throw new InvalidPasteException("No API Key Provided for Private Paste...");
        }
        String toString = GsonProviderLol.GSON.toJson(this);
        try {
            String result = ConnectionProvider.processPasteRequest(apiKey, toString,debug);
            PasteResult pasteResult = GsonProviderLol.GSON.fromJson(result, PasteResult.class);
            if (pasteResult.getPaste().isPresent()) {
                PasteManager.addPaste(pasteResult.getPaste().get());
            }
            return pasteResult;
        } catch (IOException e) {

            InvalidPasteException invalid =
                  new InvalidPasteException("Paste could not be sent to past.gg: "
                        + e.getMessage());
            invalid.addSuppressed(e);
            throw invalid;
        }
    }
}
