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

import ewc.utilities.testableio.exceptions.UnconfiguredStubException;
import ewc.utilities.testableio.responses.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Stubs {
    private final Map<ResponseId, Response> stubs = new HashMap<>();
    private final Map<QueryId, BiFunction<Object, Map<String, Object>, ?>> converters = new HashMap<>();

    public <T> T next(SourceId source, QueryId query, Class<T> type) {
        final ResponseId key = new ResponseId(source, query);
        if (noResponseFor(key)) {
            throw new UnconfiguredStubException("No stubs configured for query: " + query.id());
        }
        return this.stubFor(key).next(this.converterFor(key.query));
    }

    @SuppressWarnings("unchecked")
    private <T> BiFunction<Object, Map<String, Object>, T> converterFor(QueryId query) {
        var converter = (BiFunction<Object, Map<String, Object>, T>) this.converters.get(query);
        if (converter != null) {
            return converter;
        }
        return (Object o, Map<String, Object> map) -> (T) "%s %s".formatted(o.toString(), map.toString());
    }

    private boolean noResponseFor(ResponseId key) {
        return !stubs.containsKey(key) && !stubs.containsKey(defaultResponseIdFor(key.query));
    }

    public void setDefaultStubFor(QueryId query, Response response) {
        this.stubs.put(defaultResponseIdFor(query), response);
    }

    private Response stubFor(ResponseId key) {
        return this.stubs.getOrDefault(key, this.stubs.get(defaultResponseIdFor(key.query)));
    }

    private static ResponseId defaultResponseIdFor(QueryId query) {
        return new ResponseId(SourceId.DEFAULT_SOURCE, query);
    }

    public void setSourceStubFor(SourceId source, QueryId query, Response response) {
        this.stubs.put(new ResponseId(source, query), response);
    }

    public void setConverterFor(QueryId query, BiFunction<Object, Map<String, Object>, ?> converter) {
        this.converters.put(query, converter);
    }

    record ResponseId(SourceId source, QueryId query) {
    }
}
