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
import java.util.NoSuchElementException;
import lombok.SneakyThrows;

/**
 * This class provides configurable responses for HTTP-related utilities.
 * It is designed to be extended or modified to suit specific use cases.
 *
 * @since 0.1
 */
class ConfigurableResponses {
    /**
     * The storage for all the configured responses.
     */
    private final Map<String, ConfiguredResponse> responses = new HashMap<>();

    /**
     * Returns the next response for the given request.
     *
     * @param request The request for which to get the next response.
     * @return The next response object.
     */
    @SneakyThrows
    public GenericResponse<?> nextResponseFor(final GenericRequest<?> request) {
        final String key;
        if (this.responses.containsKey(ConfigurableResponses.responseKeyFor(request))) {
            key = ConfigurableResponses.responseKeyFor(request);
        } else {
            key = request.queryId();
        }
        if (!this.responses.containsKey(key)) {
            throw new NoSuchElementException("No responses configured");
        }
        final GenericResponse<?> response = this.responses.get(key).next();
        if (response.delay() > 0) {
            Thread.sleep(response.delay());
        }
        return response;
    }

    public void setDefaultResponsesFor(final String query, final ConfiguredResponse response) {
        this.responses.put(query, response);
    }

    public void setResponsesFor(
        final String client, final String query, final ConfiguredResponse response
    ) {
        this.responses.put(ConfigurableResponses.responseKeyFor(client, query), response);
    }

    private static String responseKeyFor(final GenericRequest<?> request) {
        return ConfigurableResponses.responseKeyFor(request.clientId(), request.queryId());
    }

    private static String responseKeyFor(final String client, final String query) {
        return "%s::%s".formatted(client, query);
    }
}
