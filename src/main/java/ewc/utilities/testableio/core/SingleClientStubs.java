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
    private final Map<QueryId, SingleQueryResponses> stubs = new HashMap<>();

    public void setSingleResponseFor(final QueryId query, final GenericResponse response) {
        this.stubs.put(query, new SingleQueryResponses(query.query(), response));
    }

    public void setMultipleResponsesFor(final String query, final GenericResponse... responses) {
        this.stubs.put(new QueryId(query), new SingleQueryResponses(query, responses));
    }

    @SneakyThrows
    public GenericResponse nextResponseFor(QueryId query) {
        if (!this.stubs.containsKey(query)) {
            throw new NoSuchElementException("No responses configured");
        }
        final GenericResponse response = this.stubs.get(query).next();
        if (response.delay() > 0) {
            Thread.sleep(response.delay());
        }
        return response;
    }

    public Map<QueryId, GenericResponse> currentActive() {
        final Map<QueryId, GenericResponse> active = new HashMap<>(this.stubs.size());
        this.stubs.keySet().forEach(query -> {
            active.put(query, this.stubs.get(query).peek());
        });
        return active;
    }
}
