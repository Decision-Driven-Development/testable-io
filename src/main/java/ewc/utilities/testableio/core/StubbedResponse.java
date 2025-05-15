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

import lombok.SneakyThrows;

/**
 * I am a wrapper around the {@link RawResponse} class that adds a delay to the response.
 *
 * @since 0.3
 */
public class StubbedResponse {
    private final RawResponse response;

    private final int delay;

    public static StubbedResponse from(final Object content) {
        return new StubbedResponse(new RawResponse(content), 0);
    }

    /**
     * Primary constructor.
     * @param response The value of the response to be returned.
     * @param delay The delay in milliseconds before the response is returned.
     */
    public StubbedResponse (final RawResponse response, final int delay) {
        this.response = response;
        this.delay = delay;
    }

    public StubbedResponse(final RawResponse response) {
        this(response, 0);
    }

    /**
     * Creates a new StubbedResponse with the same response but with a different delay.
     *
     * @param delayMillis The new delay in milliseconds
     * @return A new StubbedResponse instance with the specified delay
     */
    public StubbedResponse withDelay(int delayMillis) {
        if (this.delay == delayMillis) {
            return this;
        }
        return new StubbedResponse(this.response, delayMillis);
    }

    @SneakyThrows
    public StubbedResponse waitForDelay() {
        if (this.delay > 0) {
            Thread.sleep(this.delay);
        }
        return this;
    }

    public RawResponse tryThrowing() {
        return this.response.tryThrowing();
    }
}
