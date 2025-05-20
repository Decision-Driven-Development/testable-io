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

import ewc.utilities.testableio.exceptions.NoMoreResponsesException;
import ewc.utilities.testableio.responses.Response;
import java.util.Map;
import java.util.function.BiFunction;

class BasicStubFacade implements StubFacade {

    private final Stubs stubs;

    BasicStubFacade() {
        this.stubs = new Stubs();
    }

    @Override
    public void setDefaultStubForQuery(QueryId query, Response response) {
        this.stubs.setDefaultStubFor(query, response);
    }

    @Override
    public void setStubForQuerySource(SourceId source, QueryId query, Response response) {
        this.stubs.setSourceStubFor(source, query, response);
    }

    @Override
    public void setConverterForQuery(QueryId query, BiFunction<Object, Map<String, Object>, ?> converter) {
        this.stubs.setConverterFor(query, converter);
    }

    @Override
    public <T> T next(SourceId source, QueryId query, Class<T> type) {
        try {
            return this.stubs.next(source, query, type);
        } catch (ArrayIndexOutOfBoundsException exception) {
            throw new NoMoreResponsesException("No more responses available for query: %s".formatted(query.id()));
        }
    }

    @Override
    public Map<QueryId, Response> activeStubsForSource(SourceId source) {
        return this.stubs.activeStubsForSource(source);
    }

    @Override
    public void resetStubsForSource(SourceId source) {
        this.stubs.resetStubsForSource(source);
    }
}
