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

/**
 * The class that keeps track of all the stubs for all the clients.
 *
 * @since 0.1
 */
public class GenericIoStub {
    /**
     * The default stubs for all clients.
     */
    private final SingleClientStubs common = new SingleClientStubs();

    /**
     * The stubs for each client.
     */
    private final Map<ClientId, SingleClientStubs> stubs = new HashMap<>();

    /**
     * Returns the next response for the given query.
     *
     * @param query The query for which to get the next response.
     * @return The next response object.
     */
    public GenericResponse nextResponseFor(final GenericRequest query) {
        return this.stubs
            .getOrDefault(query.clientId(), this.common)
            .nextResponseFor(query);
    }

    /**
     * Adds a default stub for all clients.
     *
     * @param query The query ID for which to add the stub.
     * @param response The stub response to be returned for the specified query.
     */
    public void addDefaultStub(final String query, final GenericResponse response) {
        this.common.setSingleResponseFor(query, response);
    }

    /**
     * Adds a stub for a specific client.
     *
     * @param client The client ID for which to add the stub.
     * @param query The query ID for which to add the stub.
     * @param response The stub response to be returned for the specified query.
     */
    public void addStubForClient(
        final String client, final String query, final GenericResponse response
    ) {
        final ClientId clientId = new ClientId(client);
        this.stubs.putIfAbsent(clientId, new SingleClientStubs());
        this.stubs.get(clientId).setSingleResponseFor(query, response);
    }
}
