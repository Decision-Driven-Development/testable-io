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

import java.util.Objects;
import lombok.NonNull;

/**
 * A stub for a query.
 *
 * @since 0.2
 */
public final class Stub {
    private final QueryId query;
    private final ResponseId name;
    private final GenericResponse response;

    /**
     * @param query The ID of the query to be stubbed.
     * @param name The ID of the response.
     * @param response The response to be returned by the stub.
     */
    Stub(QueryId query, ResponseId name, GenericResponse response) {
        this.query = query;
        this.name = name;
        this.response = response;
    }

    @SuppressWarnings("PMD.ProhibitPublicStaticMethod")
    public static QueryStubBuilder forQueryId(final String query) {
        return new ConcreteQueryStubBuilder(query);
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (!(other instanceof Stub resp)) {
            result = false;
        } else {
            result = Objects.equals(this.name, resp.name) && Objects.equals(this.query, resp.query);
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.query, this.name);
    }

    public QueryId query() {
        return query;
    }

    public ResponseId name() {
        return name;
    }

    public GenericResponse response() {
        return response;
    }

    @Override
    public String toString() {
        return "Stub[" +
            "query=" + query + ", " +
            "name=" + name + ", " +
            "response=" + response + ']';
    }


    /**
     * Interface for building a stub for a specific query ID.
     *
     * @since 0.2
     */
    public interface QueryStubBuilder {
        StubBuilder withContents(GenericResponse response);
    }

    /**
     * Interface for building a stub for a specific client or a common stub.
     *
     * @since 0.2
     */
    public interface StubBuilder {
        Stub withResponseId(String name);

    }

    /**
     * The implementation of the {@link QueryStubBuilder} interface that creates the
     * stub for a specific query ID.
     *
     * @since 0.2
     */
    private static class ConcreteQueryStubBuilder implements QueryStubBuilder {
        /**
         * The ID of the query to be stubbed.
         */
        private final String query;

        ConcreteQueryStubBuilder(final String query) {
            this.query = query;
        }

        @Override
        public StubBuilder withContents(final GenericResponse response) {
            return new ConcreteStubBuilder(this.query, response);
        }
    }

    /**
     * The implementation of the {@link StubBuilder} interface that can create a common stub
     * (i.e. a stub that is valid for all clients) or a client-specific stub.
     *
     * @since 0.2
     */
    private static class ConcreteStubBuilder implements StubBuilder {
        /**
         * The ID of the query to be stubbed.
         */
        private final String query;

        /**
         * The response to be returned by the stub.
         */
        private final GenericResponse response;

        ConcreteStubBuilder(final String query, final GenericResponse response) {
            this.query = query;
            this.response = response;
        }

        @Override
        public Stub withResponseId(@NonNull final String name) {
            return new Stub(new QueryId(this.query), new ResponseId(name), this.response);
        }
    }
}
