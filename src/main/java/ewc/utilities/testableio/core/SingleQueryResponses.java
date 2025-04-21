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

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class provides configured responses for a single I/O opersation stub. The responses could
 * be in a form of a single response or an array of responses. The single response will be returned
 * forever, while the array of responses will be returned sequentially until it runs out of
 * responses. In such case, a {@link NoSuchElementException} will be thrown.
 *
 * @since 0.1
 */
public class SingleQueryResponses {
    /**
     * The counter providing the index of the next response to be returned.
     */
    private final Counter index;

    /**
     * The array of responses to be returned sequentially.
     */
    private final GenericResponse<?>[] values;

    /**
     * The short description of this instance. Used primarily for identifying the
     * stub that ran out of configured responses.
     */
    private final String description;

    /**
     * Single response constructor. It means that the same response will be returned forever.
     *
     * @param stub The short description of this stub instance.
     * @param value The response to be returned forever.
     */
    public SingleQueryResponses(final String stub, final GenericResponse<?> value) {
        this(stub, new GenericResponse<?>[]{value}, new ConstantIndex());
    }

    /**
     * Array of responses constructor. It means that the responses will be returned sequentially
     * until the array runs out of responses.
     *
     * @param stub The short description of this stub instance.
     * @param values The array of responses to be returned sequentially.
     */
    public SingleQueryResponses(final String stub, final GenericResponse<?>... values) {
        this(stub, values, new IncrementalIndex());
    }

    /**
     * Primary constructor.
     *
     * @param stub The short description of this stub instance.
     * @param values The array of responses to be returned sequentially.
     * @param index The counter providing the index of the next response to be returned.
     */
    private SingleQueryResponses(
        final String stub, final GenericResponse<?>[] values, final Counter index
    ) {
        this.index = index;
        this.values = values;
        this.description = stub;
    }

    /**
     * Returns the next response in the iterator. If the iterator has no more
     * responses, a {@link NoSuchElementException} will be thrown. If the response
     * contains a RuntimeException, it will be thrown immediately.
     *
     * @return The next configured response.
     */
    public GenericResponse<?> next() {
        if (this.values == null) {
            throw new IllegalStateException("No response to send");
        }
        if (this.index.isTheLast(this.values.length)) {
            throw new NoSuchElementException(
                String.format("No more configured responses for %s", this.description)
            );
        }
        final GenericResponse<?> value = this.values[this.index.getAndIncrement()];
        if (value.contents() instanceof RuntimeException runtimeException) {
            throw runtimeException;
        }
        return value;
    }

    /**
     * The value of the counter. This value is used to determine the index of the response to
     * be returned.
     *
     * @since 0.1
     */
    private interface Counter {
        /**
         * The value of the counter. This value is used to determine the index of the response to
         * be returned.
         *
         * @return The current value of the counter.
         */
        int currentValue();

        /**
         * Increments the counter by one. This method is used to move to the next response in the
         * array.
         *
         * @return The value of the counter before incrementing.
         */
        int getAndIncrement();

        /**
         * Checks if the current value of the counter is the last one in the array.
         *
         * @param size The size of the array of responses.
         * @return True if the current value is the last one, false otherwise.
         * @since 0.1
         */
        default boolean isTheLast(final int size) {
            return this.currentValue() >= size;
        }
    }

    private static final class ConstantIndex implements Counter {
        @Override
        public int currentValue() {
            return 0;
        }

        @Override
        public int getAndIncrement() {
            return 0;
        }
    }

    private static final class IncrementalIndex implements Counter {
        /**
         * The thread-safe counter providing the index of the next response to be returned.
         */
        private final AtomicInteger index = new AtomicInteger(0);

        @Override
        public int currentValue() {
            return this.index.intValue();
        }

        @Override
        public int getAndIncrement() {
            return this.index.getAndIncrement();
        }
    }
}
