/*
 * MIT License
 *
 * Copyright (c) 2025 Eugene Terekhov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ewc.utilities.testableio.core;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * This class provides configured responses for all the I/O operations.
 *
 * @since 0.1
 */
public class ConfiguredResponse {
    /**
     * The iterator of configured responses. This iterator is used to provide
     * responses sequentially.
     */
    private final Iterator<? extends GenericResponse<?>> values;

    /**
     * The short description of this instance. Used primarily for identifying the
     * stub that ran out of configured responses.
     */
    private final String description;

    public ConfiguredResponse(final String name, final GenericResponse<?> value) {
        this(Stream.generate(() -> value).iterator(), name);
    }

    public ConfiguredResponse(final String name, final GenericResponse<?>... values) {
        this(Arrays.stream(values).iterator(), name);
    }

    private ConfiguredResponse(
        final Iterator<? extends GenericResponse<?>> values, final String name
    ) {
        this.values = values;
        this.description = name;
    }

    /**
     * Returns the next response in the iterator. If the iterator has no more
     * responses, a NoSuchElementException will be thrown. If the response
     * contains a RuntimeException, it will be thrown immediately.
     *
     * @return The next configured response.
     */
    public GenericResponse<?> next() {
        if (this.values == null) {
            throw new IllegalStateException("No response to send");
        }
        if (!this.values.hasNext()) {
            throw new NoSuchElementException(
                String.format("No more configured responses for %s", this.description)
            );
        }
        final GenericResponse<?> value = this.values.next();
        if (value.contents() instanceof RuntimeException runtimeException) {
            throw runtimeException;
        }
        return value;
    }
}
