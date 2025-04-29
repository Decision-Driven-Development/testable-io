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
     * The client id for the test.
     */
    public static final ClientId SPEC_CLIENT = GenericRequest.FOR_SPEC_CLIENT.clientId();

    @Test
    void createDefaultStub() {
        final GenericIoStub target = new GenericIoStub();
        target.addCommonResponse(Response.TEST);
        Assertions.assertThat(target.nextResponseFor(GenericRequest.FOR_ANY_CLIENT))
            .isEqualTo(GenericResponse.TEST);
        Assertions.assertThat(target.nextResponseFor(GenericRequest.FOR_SPEC_CLIENT))
            .isEqualTo(GenericResponse.TEST);
    }

    @Test
    void createInfiniteStubForSpecificClient() {
        final GenericIoStub target = new GenericIoStub();
        target.addCommonResponse(Response.TEST);
        target.addClientResponse(Response.EMPTY, GenericIoStubTest.SPEC_CLIENT);
        Assertions.assertThat(target.nextResponseFor(GenericRequest.FOR_ANY_CLIENT))
            .isEqualTo(GenericResponse.TEST);
        Assertions.assertThat(target.nextResponseFor(GenericRequest.FOR_SPEC_CLIENT))
            .isEqualTo(GenericResponse.EMPTY);
    }

    @Test
    void shouldAddResponseToStoredResponses() {
        final GenericIoStub target = new GenericIoStub();
        target.addCommonResponse(Response.TEST);
        target.addClientResponse(Response.EMPTY, GenericIoStubTest.SPEC_CLIENT);
        final Set<Response> responses = target.getResponsesFor("test_request");
        Assertions.assertThat(responses.stream().map(Response::name).toList())
            .isNotEmpty()
            .containsExactlyInAnyOrder(
                new ResponseId("test_response"),
                new ResponseId("empty_response")
            );
    }

    @Test
    void shouldAddTheExceptionAsTheResponse() {
        final GenericIoStub target = new GenericIoStub();
        target.addClientResponse(Response.ERROR, GenericIoStubTest.SPEC_CLIENT);
        Assertions.assertThatThrownBy(() -> target.nextResponseFor(GenericRequest.FOR_SPEC_CLIENT))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("test error");
    }

    @Test
    void shouldActivateOneOfStoredResponses() {
        final GenericIoStub target = new GenericIoStub();
        target.addCommonResponse(Response.TEST);
        target.addClientResponse(Response.EMPTY, GenericIoStubTest.SPEC_CLIENT);
        Assertions.assertThat(target.nextResponseFor(GenericRequest.FOR_SPEC_CLIENT))
            .isEqualTo(GenericResponse.EMPTY);
        target.setActiveResponse(
            GenericIoStubTest.SPEC_CLIENT,
            Response.TEST.query(),
            Response.TEST.name()
        );
        Assertions.assertThat(target.nextResponseFor(GenericRequest.FOR_SPEC_CLIENT))
            .isEqualTo(GenericResponse.TEST);
    }

    @Test
    void shouldActivateStoredResponseForNewClient() {
        final GenericIoStub target = new GenericIoStub();
        target.addCommonResponse(Response.TEST);
        target.addClientResponse(Response.EMPTY, GenericIoStubTest.SPEC_CLIENT);
        Assertions.assertThat(target.nextResponseFor(GenericRequest.FOR_NEW_CLIENT))
            .isEqualTo(GenericResponse.TEST);
        target.setActiveResponse(
            GenericRequest.FOR_NEW_CLIENT.clientId(),
            Response.EMPTY.query(),
            Response.EMPTY.name()
        );
        Assertions.assertThat(target.nextResponseFor(GenericRequest.FOR_NEW_CLIENT))
            .isEqualTo(GenericResponse.EMPTY);
    }
}
