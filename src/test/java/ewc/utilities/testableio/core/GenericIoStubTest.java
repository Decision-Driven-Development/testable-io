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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The unit-tests for {@link GenericIoStub} class.
 *
 * @since 0.1
 */
final class GenericIoStubTest {
    public static final SourceId ANY_CLIENT = new SourceId("any client");

    public static final SourceId VIP_CLIENT = new SourceId("12345");

    public static final SourceId NEW_CLIENT = new SourceId("new client");

    public static final QueryId TEST_REQUEST = new QueryId("test_request");

    private GenericIoStub target;

    @BeforeEach
    void setUp() {
        this.target = new GenericIoStub();
    }

    @Test
    void createDefaultStub() {
        this.target.addCommonStub(Mocks.defaultStub());
        Assertions.assertThat(this.target.nextResponseFor(ANY_CLIENT, TEST_REQUEST))
            .isEqualTo(Mocks.defaultResponse());
        Assertions.assertThat(this.target.nextResponseFor(VIP_CLIENT, TEST_REQUEST))
            .isEqualTo(Mocks.defaultResponse());
    }

    @Test
    void createInfiniteStubForSpecificClient() {
        this.target.addCommonStub(Mocks.defaultStub());
        this.target.addClientStub(Mocks.emptyStub(), GenericIoStubTest.VIP_CLIENT);
        Assertions.assertThat(this.target.nextResponseFor(ANY_CLIENT, TEST_REQUEST))
            .isEqualTo(Mocks.defaultResponse());
        Assertions.assertThat(this.target.nextResponseFor(VIP_CLIENT, TEST_REQUEST))
            .isEqualTo(Mocks.emptyResponse());
    }

    @Test
    void shouldAddResponseToStoredResponses() {
        this.target.addCommonStub(Mocks.defaultStub());
        this.target.addClientStub(Mocks.emptyStub(), GenericIoStubTest.VIP_CLIENT);
        final Set<Stub> stubs = this.target.getResponsesFor("test_request");
        Assertions.assertThat(stubs.stream().map(Stub::name).toList())
            .isNotEmpty()
            .containsExactlyInAnyOrder(
                new ResponseId("default_response"),
                new ResponseId("empty_response")
            );
    }

    @Test
    void shouldAddTheExceptionAsTheResponse() {
        this.target.addCommonStub(Mocks.errorStub());
        Assertions
            .assertThatThrownBy(() -> this.target.nextResponseFor(VIP_CLIENT, TEST_REQUEST))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("test error");
    }

    @Test
    void shouldReturnSpecificClientStubsWhenNoDefaultStubsConfigured() {
        this.target.addClientStub(Mocks.emptyStub(), GenericIoStubTest.VIP_CLIENT);
        Assertions.assertThatNoException().isThrownBy(() -> this.target.nextResponseFor(VIP_CLIENT, TEST_REQUEST));
    }

    @Test
    void shouldActivateOneOfStoredResponses() {
        this.target.addCommonStub(Mocks.defaultStub());
        this.target.addClientStub(Mocks.emptyStub(), GenericIoStubTest.VIP_CLIENT);
        Assertions.assertThat(this.target.nextResponseFor(VIP_CLIENT, TEST_REQUEST))
            .isEqualTo(Mocks.emptyResponse());
        this.target.setActiveResponse(
            GenericIoStubTest.VIP_CLIENT,
            Mocks.defaultStub().query(),
            Mocks.defaultStub().name()
        );
        Assertions.assertThat(this.target.nextResponseFor(VIP_CLIENT, TEST_REQUEST))
            .isEqualTo(Mocks.defaultResponse());
    }

    @Test
    void shouldActivateStoredResponseForNewClient() {
        this.target.addCommonStub(Mocks.defaultStub());
        this.target.addClientStub(Mocks.emptyStub(), GenericIoStubTest.VIP_CLIENT);
        Assertions.assertThat(this.target.nextResponseFor(NEW_CLIENT, TEST_REQUEST))
            .isEqualTo(Mocks.defaultResponse());
        this.target.setActiveResponse(
            NEW_CLIENT,
            Mocks.emptyStub().query(),
            Mocks.emptyStub().name()
        );
        Assertions.assertThat(this.target.nextResponseFor(NEW_CLIENT, TEST_REQUEST))
            .isEqualTo(Mocks.emptyResponse());
    }

    @Test
    void shouldReturnDefaultResponsesForUnconfiguredClientQueries() {
        this.target.addCommonStub(Mocks.defaultStub());
        this.target.addCommonStub(Mocks.anotherQueryStub());
        this.target.addClientStub(Mocks.emptyStub(), GenericIoStubTest.VIP_CLIENT);
        Assertions.assertThat(this.target.nextResponseFor(VIP_CLIENT, new QueryId("another_request")))
            .isEqualTo(Mocks.defaultResponse());
    }

    @Test
    void shouldResetStubsForClient() {
        this.target.addCommonStub(Mocks.defaultStub());
        this.target.addClientStub(Mocks.emptyStub(), GenericIoStubTest.VIP_CLIENT);
        Assertions.assertThat(this.target.nextResponseFor(VIP_CLIENT, TEST_REQUEST))
            .isEqualTo(Mocks.emptyResponse());
        this.target.resetStubsFor(GenericIoStubTest.VIP_CLIENT);
        Assertions.assertThat(this.target.nextResponseFor(VIP_CLIENT, TEST_REQUEST))
            .isEqualTo(Mocks.defaultResponse());
    }
}
