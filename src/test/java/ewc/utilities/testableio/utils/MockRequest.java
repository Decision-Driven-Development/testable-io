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

package ewc.utilities.testableio.utils;

import ewc.utilities.testableio.core.GenericRequest;
import java.util.Map;
import java.util.function.Function;

/**
 * The utility class that provides a mock request for testing purposes.
 *
 * @since 0.1
 */
public final class MockRequest {
    /**
     * An instance of a testable GenericRequest.
     */
    private final GenericRequest assigned;

    /**
     * An instance of a testable GenericRequest with an unknown client.
     */
    private final GenericRequest unknown;

    public MockRequest() {
        this.assigned = GenericRequest.<String>builder()
            .parameters(
                Map.of(
                    "clientId", "12345",
                    "url", "http://localhost:8080/recommendations"
                )
            )
            .client(MockRequest.clientDiscriminator())
            .query(MockRequest.queryDiscriminator())
            .build();
        this.unknown = GenericRequest.<String>builder()
            .parameters(
                Map.of(
                    "clientId", "67890",
                    "url", "http://localhost:8080/recommendations"
                )
            )
            .client(MockRequest.clientDiscriminator())
            .query(MockRequest.queryDiscriminator())
            .build();
    }

    public GenericRequest assignedToClient() {
        return this.assigned;
    }

    public GenericRequest clientUnknown() {
        return this.unknown;
    }

    private static Function<GenericRequest, String> queryDiscriminator() {
        return request -> {
            final String result;
            if (request.parameter("url").toString().contains("recommendations")) {
                result = "getItemRecommendations";
            } else {
                result = "home";
            }
            return result;
        };
    }

    private static Function<GenericRequest, String> clientDiscriminator() {
        return request ->
            request.parameter("clientId").toString();
    }
}
