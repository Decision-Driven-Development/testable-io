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

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * This class provides configured responses for a single I/O operation stub. The responses could
 * be in a form of a single response or an array of responses. The single response will be returned
 * forever, while the array of responses will be returned sequentially until it runs out of
 * responses. In such case, a {@link NoSuchElementException} will be thrown.
 *
 * @param <T> The type of the response to be returned.
 *
 * @since 0.3
 */
public class StubbedQuery<T> {
    /**
     * The default function that converts the {@link RawResponse} to a string.
     */
    private static final BiFunction<Object, Map<String, Object>, String> STRING_CONVERTER =
        (content, metadata) -> content.toString();

    /**
     * The counter providing the index of the next response to be returned.
     */
    private final Counter index;

    /**
     * The array of responses to be returned sequentially.
     */
    private final StubbedResponse[] responses;

    /**
     * The identity of this instance. Used primarily for identifying the
     *  stub that ran out of configured responses.
     */
    private final QueryId id;

    /**
     * Instance specific function that converts the {@link RawResponse} to the desired type.
     */
    private final BiFunction<Object, Map<String, Object>, T> converter;

    /**
     * Single response constructor. It means that the same response will be returned forever.
     *
     * @param stub The short description of this stub instance.
     * @param value The response to be returned forever.
     * @param converter The function that converts the {@link RawResponse} to the desired type.
     */
    public StubbedQuery(
        final String stub,
        final StubbedResponse value,
        final BiFunction<Object, Map<String, Object>, T> converter) {
        this(stub, new StubbedResponse[]{value}, converter, new ConstantIndex());
    }

    /**
     * Array of responses constructor. It means that the responses will be returned
     * sequentially until the array runs out of responses.
     *
     * @param stub The short description of this stub instance.
     * @param responses The array of responses to be returned sequentially.
     * @param converter The function that converts the {@link RawResponse} to the desired type.
     */
    public StubbedQuery(
        final String stub,
        final StubbedResponse[] responses,
        final BiFunction<Object, Map<String, Object>, T> converter) {
        this(stub, responses, converter, new IncrementalIndex());
    }

    /**
     * Primary constructor.
     *
     * @param stub The short description of this stub instance.
     * @param responses The array of responses to be returned sequentially.
     * @param converter The function that converts the {@link RawResponse} to the desired type.
     * @param index The counter providing the index of the next response to be returned.
     */
    private StubbedQuery(
        final String stub,
        final StubbedResponse[] responses,
        BiFunction<Object, Map<String, Object>, T> converter,
        final Counter index) {
        this.id = new QueryId(stub);
        this.responses = responses;
        this.converter = converter;
        this.index = index;
    }

    static StubbedQuery<String> from(final String stub, final StubbedResponse value) {
        return new StubbedQuery<>(
            stub,
            new StubbedResponse[]{value},
            STRING_CONVERTER,
            new ConstantIndex()
        );
    }

    static StubbedQuery<String> from(final String stub, final StubbedResponse[] responses) {
        return new StubbedQuery<>(stub, responses, STRING_CONVERTER, new IncrementalIndex());
    }


    /**
     * Returns the next response in the iterator. If the iterator has no more responses, a
     * {@link NoSuchElementException} will be thrown. After the next response is
     * fetched, its delay comes into play. After the amount of delay milliseconds passes,
     * the response is converted to the desired type (using the {@link StubbedQuery#converter}
     * function) and then returned. If the response contains a RuntimeException, it will be thrown
     * right after delay, without type conversion attempt.
     *
     * @return The next configured response.
     */
    public T next() {
        return responseUsing(this.index::getAndIncrement)
            .waitForDelay()
            .tryThrowing()
            .convertedUsing(this.converter);
    }

    /**
     * Returns the next configured response without "removing" it from responses queue.
     * If the queue is already exhausted, a {@link NoSuchElementException} will be thrown.
     *
     * @return The next configured response.
     */
    public StubbedResponse peek() {
        return responseUsing(this.index::currentValue);
    }

    public StubbedQuery<T> withDelayAt(int index, int delayMillis) {
        if (index < 0 || index >= this.responses.length) {
            throw new IndexOutOfBoundsException(
                String.format(
                    "Index %d is out of bounds for responses array of length %d",
                    index,
                    this.responses.length
                )
            );
        }
        this.responses[index] = this.responses[index].withDelay(delayMillis);
        return this;
    }

    public StubbedQuery<T> withDelayForAll(int delay) {
        for (int i = 0; i < this.responses.length; i++) {
            this.responses[i] = this.responses[i].withDelay(delay);
        }
        return this;
    }

    private StubbedResponse responseUsing(Supplier<Integer> counter) {
        if (this.responses == null) {
            throw new IllegalStateException("No response to send");
        }
        if (this.index.isTheLast(this.responses.length)) {
            throw new NoSuchElementException(
                String.format("No more configured responses for %s", this.id.query()));
        }
        return this.responses[counter.get()];
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
