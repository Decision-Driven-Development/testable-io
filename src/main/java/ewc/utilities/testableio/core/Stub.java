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
 * @param response The response to be returned by the stub.
 * @since 0.2
 */
public record Stub(String client, String query, String response) {

    @SuppressWarnings("PMD.ProhibitPublicStaticMethod")
    public static QueryStubBuilder forQuery(final String query) {
        return new ConcreteQueryStubBuilder(query);
    }

    public interface QueryStubBuilder {
        StubBuilder withContents(String response);
    }

    public interface StubBuilder {
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
        public StubBuilder withContents(final String response) {
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
        private final String response;

        ConcreteStubBuilder(final String query, final String response) {
            this.query = query;
            this.response = response;
        }

        @Override
        public Stub buildForSpecificClient(final String client) {
            return new Stub(client, this.query, this.response);
        }

        @Override
        public Stub buildForAllClients() {
            return new Stub(null, this.query, this.response);
        }
    }
}
