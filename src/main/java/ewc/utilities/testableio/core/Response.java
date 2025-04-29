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

/**
 * A stub for a query.
 *
 * @param query The ID of the query to be stubbed.
 * @param name The ID of the response.
 * @param response The response to be returned by the stub.
 * @since 0.2
 */
public record Response(QueryId query, ResponseId name, GenericResponse response) {
    /**
     * The starting point for building a stub.
     */
    public static final QueryStubBuilder SLUG = Response
        .forQueryId("test_request");

    /**
     * The testing instance of the stub.
     */
    static final Response TEST = SLUG
        .withContents(GenericResponse.TEST)
        .withResponseId("test_response")
        .build();

    /**
     * Another testing instance of the response.
     */
    static final Response EMPTY = SLUG
        .withContents(GenericResponse.EMPTY)
        .withResponseId("empty_response")
        .build();

    /**
     * The error instance of the response.
     */
    static final Response ERROR = SLUG
        .withContents(GenericResponse.ERROR)
        .withResponseId("error_response")
        .build();

    @SuppressWarnings("PMD.ProhibitPublicStaticMethod")
    public static QueryStubBuilder forQueryId(final String query) {
        return new ConcreteQueryStubBuilder(query);
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (!(other instanceof Response resp)) {
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
        StubBuilder withResponseId(String name);

        Response build();

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

        /**
         * The name of the response.
         */
        private String name;

        ConcreteStubBuilder(final String query, final GenericResponse response) {
            this.query = query;
            this.response = response;
        }

        @Override
        public StubBuilder withResponseId(final String identifier) {
            this.name = identifier;
            return this;
        }

        @Override
        public Response build() {
            if (this.name == null) {
                this.name = this.query;
            }
            return new Response(new QueryId(this.query), new ResponseId(this.name), this.response);
        }
    }
}
