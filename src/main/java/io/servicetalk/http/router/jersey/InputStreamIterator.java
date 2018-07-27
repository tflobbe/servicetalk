/*
 * Copyright © 2018 Apple Inc. and the ServiceTalk project authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.servicetalk.http.router.jersey;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static java.lang.System.arraycopy;

// TODO this is interesting to DirectAction too: share it somewhere?
final class InputStreamIterator implements Iterator<byte[]> {
    private final InputStream is;
    private final byte[] buffer = new byte[4096];
    private int nextAvailable;

    InputStreamIterator(final InputStream is) {
        this.is = is;
    }

    @Override
    public boolean hasNext() {
        if (nextAvailable == 0) {
            fetchNext();
        }
        return nextAvailable > 0;
    }

    @Override
    public byte[] next() {
        if (nextAvailable <= 0) {
            throw new NoSuchElementException();
        }
        final byte[] next = new byte[nextAvailable];
        arraycopy(buffer, 0, next, 0, nextAvailable);
        nextAvailable = 0;
        return next;
    }

    private void fetchNext() {
        try {
            nextAvailable = is.read(buffer);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to read: " + is, e);
        }
    }
}
