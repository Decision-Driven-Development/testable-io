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

/**
 * A stub for a query.
 *
 * @param client The optional client ID for which the stub is valid.
 * @param query The ID of the query to be stubbed.
 * @param name The ID of the response.
 * @param response The response to be returned by the stub.
 *
 * @since 0.2
 */
public record Stub(String client, String query, String name, GenericResponse response) {
    /**
     * The testing instance of the stub.
     */
    static final Stub COMMON_TEST = forQuery("getItemRecommendations")
        .withContents(GenericResponse.TEST_RESPONSE)
        .buildForAllClients();

    @SuppressWarnings("PMD.ProhibitPublicStaticMethod")
    public static QueryStubBuilder forQuery(final String query) {
        return new ConcreteQueryStubBuilder(query);
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
        StubBuilder withName(String name);

        Stub buildForSpecificClient(String client);

        Stub buildForAllClients();

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
        public StubBuilder withName(final String identifier) {
            this.name = identifier;
            return this;
        }

        @Override
        public Stub buildForSpecificClient(final String client) {
            if (this.name == null) {
                this.name = "%s::%s".formatted(client, this.query);
            }
            return new Stub(client, this.query, this.name, this.response);
        }

        @Override
        public Stub buildForAllClients() {
            if (this.name == null) {
                this.name = "common::%s".formatted(this.query);
            }
            return new Stub("common", this.query, this.name, this.response);
        }
    }
}
