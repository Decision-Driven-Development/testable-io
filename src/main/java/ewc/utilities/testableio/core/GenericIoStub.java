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
    private final Set<Stub> stored = new HashSet<>();

    public GenericResponse nextResponseFor(final ClientId client, final QueryId query) {
        return this.stubsActiveFor(client).nextResponseFor(query);
    }

    /**
     * Adds a common response for all clients.
     *
     * @param stub The response to be added.
     */
    public void addCommonStub(final Stub stub) {
        this.addStub(stub, GenericIoStub.COMMON_CLIENT);
    }

    /**
     * Adds a response for a specific client.
     *
     * @param stub The response to be added.
     * @param client The client ID for which the response is to be added.
     */
    public void addClientStub(final Stub stub, final ClientId client) {
        this.addStub(stub, client);
    }

    /**
     * Returns the set of all responses for the given query.
     *
     * @param query The query for which to get the responses.
     * @return The set of responses for the given query.
     */
    public Set<Stub> getResponsesFor(final String query) {
        return this.stored
            .stream()
            .filter(response -> response.query().equals(new QueryId(query)))
            .collect(Collectors.toSet());
    }

    /**
     * Sets the active response for a specific client.
     *
     * @param client Client ID for which the response is to be set.
     * @param query Query ID for which the response is to be set.
     * @param response Response ID for the response to be set as active.
     */
    public void setActiveResponse(
        final ClientId client, final QueryId query, final ResponseId response
    ) {
        this.stored.stream().filter(resp -> resp.query().equals(query))
            .filter(resp -> resp.name().equals(response))
            .findFirst()
            .ifPresentOrElse(
                resp -> this.setActiveResponse(client, resp),
                () -> {
                    throw new IllegalArgumentException("No such response");
                });
    }

    public Map<QueryId, GenericResponse> getCurrentCommonStubs() {
        return this.stubs.get(GenericIoStub.COMMON_CLIENT).currentActive();
    }

    public Map<QueryId, GenericResponse> getCurrentClientStubs(final ClientId client) {
        final Map<QueryId, GenericResponse> result = new HashMap<>(
            this.stubs.getOrDefault(GenericIoStub.COMMON_CLIENT, new SingleClientStubs()).currentActive()
        );
        result.putAll(this.stubsExplicitlyConfiguredFor(client).currentActive());
        return result;
    }

    public void resetStubsFor(ClientId client) {
        this.stubs.remove(client);
    }

    /**
     * Sets the active response for a specific client.
     *
     * @param client The client ID for which the response is to be set.
     * @param stub The response to be set as active.
     */
    private void setActiveResponse(final ClientId client, final Stub stub) {
        this.stubs.putIfAbsent(client, new SingleClientStubs());
        this.stubs.get(client).setSingleResponseFor(stub.query(), stub.response());
    }

    /**
     * Adds a stub to the internal stub storage.
     *
     * @param stub The stub to be added.
     * @param client The client ID for which the stub is to be added.
     */
    private void addStub(final Stub stub, final ClientId client) {
        this.stored.add(stub);
        this.setActiveResponse(client, stub);
    }

    private SingleClientStubs stubsExplicitlyConfiguredFor(ClientId client) {
        return this.stubs.getOrDefault(client, this.stubs.get(GenericIoStub.COMMON_CLIENT));
    }

    private SingleClientStubs stubsActiveFor(ClientId client) {
        final Map<QueryId, GenericResponse> clientStubs = this.getCurrentClientStubs(client);
        Map<QueryId, SingleQueryResponses> result = new HashMap<>();
        clientStubs.forEach((query, response) -> {
            result.put(query, new SingleQueryResponses(query.query(), response));
        });
        return new SingleClientStubs(result);
    }
}
