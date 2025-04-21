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

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.SneakyThrows;

/**
 * This class provides configurable responses for all the queries made by a single 'client'.
 *
 * @since 0.1
 */
class SingleClientStubs {
    /**
     * The storage for all the configured responses.
     */
    private final Map<String, SingleQueryResponses> responses = new HashMap<>();

    /**
     * Returns the next response for the given query.
     *
     * @param query The query for which to get the next response.
     * @return The next response object.
     */
    @SneakyThrows
    public GenericResponse<?> nextResponseFor(final GenericRequest<?> query) {
        final String key;
        if (this.responses.containsKey(SingleClientStubs.responseKeyFor(query))) {
            key = SingleClientStubs.responseKeyFor(query);
        } else {
            key = query.queryId();
        }
        if (!this.responses.containsKey(key)) {
            throw new NoSuchElementException("No responses configured");
        }
        final GenericResponse<?> response = this.responses.get(key).next();
        if (response.delay() > 0) {
            Thread.sleep(response.delay());
        }
        return response;
    }

    public void setDefaultResponsesFor(final String query, final SingleQueryResponses response) {
        this.responses.put(query, response);
    }

    public void setResponsesFor(
        final String client, final String query, final SingleQueryResponses response
    ) {
        this.responses.put(SingleClientStubs.responseKeyFor(client, query), response);
    }

    private static String responseKeyFor(final GenericRequest<?> request) {
        return SingleClientStubs.responseKeyFor(request.clientId(), request.queryId());
    }

    private static String responseKeyFor(final String client, final String query) {
        return "%s::%s".formatted(client, query);
    }
}
