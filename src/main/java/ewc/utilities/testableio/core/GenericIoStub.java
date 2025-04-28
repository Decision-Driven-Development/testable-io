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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The class that keeps track of all the stubs for all the clients.
 *
 * @since 0.1
 */
public class GenericIoStub {
    /**
     * Client ID for all the clients (i.e. when no specific client is set for the stub).
     */
    private static final ClientId COMMON_CLIENT = new ClientId("common");

    /**
     * The stubs for each client.
     */
    private final Map<ClientId, SingleClientStubs> stubs = new HashMap<>();

    /**
     * The set of all the responses.
     */
    private final Set<Response> responses = new HashSet<>();

    /**
     * Returns the next response for the given query.
     *
     * @param query The query for which to get the next response.
     * @return The next response object.
     */
    public GenericResponse nextResponseFor(final GenericRequest query) {
        return this.stubs
            .getOrDefault(query.clientId(), this.stubs.get(GenericIoStub.COMMON_CLIENT))
            .nextResponseFor(query);
    }

    /**
     * Adds a common response for all clients.
     *
     * @param response The response to be added.
     */
    public void addCommonResponse(final Response response) {
        this.addStub(response, GenericIoStub.COMMON_CLIENT);
    }

    /**
     * Adds a response for a specific client.
     *
     * @param response The response to be added.
     * @param client The client ID for which the response is to be added.
     */
    public void addClientResponse(final Response response, final ClientId client) {
        this.addStub(response, client);
    }

    /**
     * Returns the set of all responses for the given query.
     *
     * @param query The query for which to get the responses.
     * @return The set of responses for the given query.
     */
    public Set<Response> getResponsesFor(final String query) {
        return this.responses
            .stream()
            .filter(response -> response.query().equals(query))
            .collect(Collectors.toSet());
    }

    /**
     * Sets the active response for a specific client.
     *
     * @param client The client ID for which the response is to be set.
     * @param response The response to be set as active.
     */
    public void setActiveResponse(final ClientId client, final Response response) {
        this.getStubsFor(client).setSingleResponseFor(response.query(), response.response());
    }

    /**
     * Adds a stub to the internal stub storage.
     *
     * @param response The stub to be added.
     * @param client The client ID for which the stub is to be added.
     */
    private void addStub(final Response response, final ClientId client) {
        this.responses.add(response);
        this.getStubsFor(client).setSingleResponseFor(response.query(), response.response());
    }

    private SingleClientStubs getStubsFor(final ClientId client) {
        this.stubs.putIfAbsent(client, new SingleClientStubs());
        return this.stubs.get(client);
    }
}
