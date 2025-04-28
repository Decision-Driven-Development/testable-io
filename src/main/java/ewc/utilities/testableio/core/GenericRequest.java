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

import java.util.Map;
import java.util.function.Function;
import lombok.Builder;

/**
 * This class represents a generic request that can be made to any outside system.
 * The main reason to have the abstraction like that is to be able to use the same unified
 * data structure to get the data from the request (to find the corresponding response)
 * and to create the real query to the outside system.
 *
 * @since 0.1
 */
@Builder
public class GenericRequest {
    /**
     * The request for the unspecified client.
     */
    static final GenericRequest ANY_CLIENT =
        GenericRequest.builder()
            .client(req -> req.parameter("clientId").toString())
            .query(req -> req.parameter("url").toString())
            .parameters(
                Map.of(
                    "clientId", "unspecified",
                    "url", "test_request"
                )
            )
            .build();

    /**
     * The request for a specific client.
     */
    static final GenericRequest SPECIFIC_CLIENT =
        GenericRequest.builder()
            .client(req -> req.parameter("clientId").toString())
            .query(req -> req.parameter("url").toString())
            .parameters(
                Map.of(
                    "clientId", "12345",
                    "url", "test_request"
                )
            )
            .build();

    /**
     * The parameters of the request. These are used to modify the request data.
     */
    private final Map<String, Object> parameters;

    /**
     * The function that will be used to extract the client ID from the request data.
     */
    private final Function<GenericRequest, String> client;

    /**
     * The function that will be used to extract the query ID from the request data.
     */
    private final Function<GenericRequest, String> query;

    /**
     * Fetches the value of the specified parameter from the request.
     *
     * @param parameter The name of the parameter to fetch.
     * @return The value of the parameter.
     */
    public Object parameter(final String parameter) {
        return this.parameters.get(parameter);
    }

    /**
     * Returns the ID of the client performing the request. This ID will be used to
     * find the configured responses' collection for the specified client.
     *
     * @return The ID of the client performing the request.
     */
    public ClientId clientId() {
        return new ClientId(this.client.apply(this));
    }

    /**
     * Returns the query ID of the request. This ID will be used to find the right
     * response to return from the configured responses' collection.
     *
     * @return The query ID of the request.
     */
    public QueryId queryId() {
        return new QueryId(this.query.apply(this));
    }

    /**
     * Returns the request in the form that is required by the outside system and the real adapter.
     *
     * @param transformer Function that transform the generic request into the real request.
     * @param <R> The type of the real request.
     * @return The real request object, i.e. {@code HttpRequestEntity}.
     */
    public <R> R realRequest(final Function<GenericRequest, R> transformer) {
        return transformer.apply(this);
    }
}
