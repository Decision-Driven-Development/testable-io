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

package ewc.utilities.testableio.responses;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

public class SequencedResponse implements Response {
    /**
     * The counter providing the index of the next response to be returned.
     */
    private final IncrementalIndex index;

    /**
     * The array of responses to be returned sequentially.
     */
    private final Response[] responses;

    public SequencedResponse(Response... responses) {
        this.responses = responses;
        this.index = new IncrementalIndex();
    }

    @Override
    public <R> R next(BiFunction<Object, Map<String, Object>, R> transformer) {
        return this.responses[this.index.getAndIncrement()].next(transformer);
    }

    @Override
    public Response peek() {
        return this.responses[this.index.currentValue()].peek();
    }

    private static final class IncrementalIndex {
        /**
         * The thread-safe counter providing the index of the next response to be returned.
         */
        private final AtomicInteger index = new AtomicInteger(0);

        public int currentValue() {
            return this.index.intValue();
        }

        public int getAndIncrement() {
            return this.index.getAndIncrement();
        }

    }
}
