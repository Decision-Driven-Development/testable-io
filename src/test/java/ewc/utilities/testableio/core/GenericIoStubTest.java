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
    public static final ClientId SPEC_CLIENT = Mocks.testRequestFromVipClient().clientId();

    @Test
    void createDefaultStub() {
        final GenericIoStub target = new GenericIoStub();
        target.addCommonStub(Mocks.defaultStub());
        Assertions.assertThat(target.nextResponseFor(Mocks.testRequestFromAnyClient()))
            .isEqualTo(Mocks.defaultResponse());
        Assertions.assertThat(target.nextResponseFor(Mocks.testRequestFromVipClient()))
            .isEqualTo(Mocks.defaultResponse());
    }

    @Test
    void createInfiniteStubForSpecificClient() {
        final GenericIoStub target = new GenericIoStub();
        target.addCommonStub(Mocks.defaultStub());
        target.addClientStub(Mocks.emptyStub(), GenericIoStubTest.SPEC_CLIENT);
        Assertions.assertThat(target.nextResponseFor(Mocks.testRequestFromAnyClient()))
            .isEqualTo(Mocks.defaultResponse());
        Assertions.assertThat(target.nextResponseFor(Mocks.testRequestFromVipClient()))
            .isEqualTo(Mocks.emptyResponse());
    }

    @Test
    void shouldAddResponseToStoredResponses() {
        final GenericIoStub target = new GenericIoStub();
        target.addCommonStub(Mocks.defaultStub());
        target.addClientStub(Mocks.emptyStub(), GenericIoStubTest.SPEC_CLIENT);
        final Set<Stub> stubs = target.getResponsesFor("test_request");
        Assertions.assertThat(stubs.stream().map(Stub::name).toList())
            .isNotEmpty()
            .containsExactlyInAnyOrder(
                new ResponseId("default_response"),
                new ResponseId("empty_response")
            );
    }

    @Test
    void shouldAddTheExceptionAsTheResponse() {
        final GenericIoStub target = new GenericIoStub();
        target.addClientStub(Mocks.errorStub(), GenericIoStubTest.SPEC_CLIENT);
        Assertions
            .assertThatThrownBy(() -> target.nextResponseFor(Mocks.testRequestFromVipClient()))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("test error");
    }

    @Test
    void shouldActivateOneOfStoredResponses() {
        final GenericIoStub target = new GenericIoStub();
        target.addCommonStub(Mocks.defaultStub());
        target.addClientStub(Mocks.emptyStub(), GenericIoStubTest.SPEC_CLIENT);
        Assertions.assertThat(target.nextResponseFor(Mocks.testRequestFromVipClient()))
            .isEqualTo(Mocks.emptyResponse());
        target.setActiveResponse(
            GenericIoStubTest.SPEC_CLIENT,
            Mocks.defaultStub().query(),
            Mocks.defaultStub().name()
        );
        Assertions.assertThat(target.nextResponseFor(Mocks.testRequestFromVipClient()))
            .isEqualTo(Mocks.defaultResponse());
    }

    @Test
    void shouldActivateStoredResponseForNewClient() {
        final GenericIoStub target = new GenericIoStub();
        target.addCommonStub(Mocks.defaultStub());
        target.addClientStub(Mocks.emptyStub(), GenericIoStubTest.SPEC_CLIENT);
        Assertions.assertThat(target.nextResponseFor(Mocks.testRequestFromNewClient()))
            .isEqualTo(Mocks.defaultResponse());
        target.setActiveResponse(
            Mocks.testRequestFromNewClient().clientId(),
            Mocks.emptyStub().query(),
            Mocks.emptyStub().name()
        );
        Assertions.assertThat(target.nextResponseFor(Mocks.testRequestFromNewClient()))
            .isEqualTo(Mocks.emptyResponse());
    }
}
