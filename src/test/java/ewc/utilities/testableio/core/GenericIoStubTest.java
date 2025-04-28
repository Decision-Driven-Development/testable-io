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

import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * The unit-tests for {@link GenericIoStub} class.
 *
 * @since 0.1
 */
final class GenericIoStubTest {
    /**
     * The generic query ID for the tests.
     */
    private static final String QUERY = "test_request";

    @Test
    void createDefaultStub() {
        final GenericIoStub target = new GenericIoStub();
        target.addCommonResponse(Response.TEST_RESPONSE);
        Assertions.assertThat(target.nextResponseFor(GenericRequest.ANY_CLIENT))
            .isEqualTo(GenericResponse.TEST_DEFAULT);
        Assertions.assertThat(target.nextResponseFor(GenericRequest.SPECIFIC_CLIENT))
            .isEqualTo(GenericResponse.TEST_DEFAULT);
    }

    @Test
    void createInfiniteStubForSpecificClient() {
        final GenericIoStub target = new GenericIoStub();
        target.addCommonResponse(Response.TEST_RESPONSE);
        target.addClientResponse(
            Response.EMPTY_RESPONSE,
            GenericRequest.SPECIFIC_CLIENT.clientId()
        );
        Assertions.assertThat(target.nextResponseFor(GenericRequest.ANY_CLIENT))
            .isEqualTo(GenericResponse.TEST_DEFAULT);
        Assertions.assertThat(target.nextResponseFor(GenericRequest.SPECIFIC_CLIENT))
            .isEqualTo(GenericResponse.EMPTY);
    }

    @Test
    void shouldAddResponseToStoredResponses() {
        final GenericIoStub target = new GenericIoStub();
        target.addCommonResponse(Response.TEST_RESPONSE);
        target.addClientResponse(
            Response.forQueryId(GenericIoStubTest.QUERY)
                .withContents(GenericResponse.EMPTY)
                .withResponseId("client-specific response")
                .build(),
            GenericRequest.SPECIFIC_CLIENT.clientId()
        );
        final Set<Response> responses = target.getResponsesFor(GenericIoStubTest.QUERY);
        Assertions.assertThat(responses.stream().map(Response::name).toList())
            .isNotEmpty()
            .containsExactlyInAnyOrder("test_response", "client-specific response");
    }
}
