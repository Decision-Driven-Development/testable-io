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

package ewc.utilities.testableio.external;

import ewc.utilities.testableio.core.*;
import java.util.Map;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * End-to-end tests, like the real use cases for the library.
 *
 * @since 0.2
 */
final class EndToEndTest {
    public static final QueryId HOME_PAGE = new QueryId("home");
    public static final QueryId MISSING_PAGE = new QueryId("non-existing");
    public static final QueryId NUMBER_PAGE = new QueryId("number");

    public static final ClientId ANY_CLIENT = new ClientId("any client");
    public static final ClientId VIP_CLIENT = new ClientId("VIP client");
    public static final ClientId NEW_CLIENT = new ClientId("new client");
    public static final IllegalStateException NO_SUCH_PAGE_EXCEPTION = new IllegalStateException("no such page");
    public static final GenericResponse DEFAULT_HOME_PAGE = new GenericResponse("html for the home page");
    public static final GenericResponse DEFAULT_NUMBER_PAGE = new GenericResponse(1000L);
    public static final GenericResponse VIP_HOME_PAGE = new GenericResponse("VIP home page");
    public static final GenericResponse NO_SUCH_PAGE = new GenericResponse(NO_SUCH_PAGE_EXCEPTION);
    private GenericIoStub target;

    @BeforeEach
    void setUp() {
        this.target = new GenericIoStub();
        Stream.of(
            Stub.forQueryId("home")
                .withContents(DEFAULT_HOME_PAGE)
                .withResponseId("home"),
            Stub.forQueryId("non-existing")
                .withContents(NO_SUCH_PAGE)
                .withResponseId("non-existing"),
            Stub.forQueryId("number")
                .withContents(DEFAULT_NUMBER_PAGE)
                .withResponseId("number")
        ).forEach(stub -> this.target.addCommonStub(stub));
    }

    @Test
    void shouldHaveAPredefinedSetOfDefaultResponses() {
        when(VIP_CLIENT).requests(HOME_PAGE).then(DEFAULT_HOME_PAGE);
        when(NEW_CLIENT).requests(HOME_PAGE).then(DEFAULT_HOME_PAGE);
        when(ANY_CLIENT).requests(HOME_PAGE).then(DEFAULT_HOME_PAGE);
        when(ANY_CLIENT).requests(NUMBER_PAGE).then(DEFAULT_NUMBER_PAGE);
        when(NEW_CLIENT).requests(NUMBER_PAGE).then(DEFAULT_NUMBER_PAGE);
        when(VIP_CLIENT).requests(NUMBER_PAGE).then(DEFAULT_NUMBER_PAGE);
        when(ANY_CLIENT).requests(MISSING_PAGE).thenThrows(NO_SUCH_PAGE_EXCEPTION);
        when(NEW_CLIENT).requests(MISSING_PAGE).thenThrows(NO_SUCH_PAGE_EXCEPTION);
        when(VIP_CLIENT).requests(MISSING_PAGE).thenThrows(NO_SUCH_PAGE_EXCEPTION);
    }

    @Test
    void shouldSetSpecificStubForAClient() {
        final Stub newHome = Stub.forQueryId("home").withContents(VIP_HOME_PAGE).withResponseId("home");
        this.target.addClientStub(newHome, VIP_CLIENT);
        when(VIP_CLIENT).requests(HOME_PAGE).then(VIP_HOME_PAGE);
        when(NEW_CLIENT).requests(HOME_PAGE).then(DEFAULT_HOME_PAGE);
        when(ANY_CLIENT).requests(HOME_PAGE).then(DEFAULT_HOME_PAGE);
    }

    @Test
    void shouldChooseActiveStubFromStoredStubs() {
        final Stub newHome = Stub.forQueryId("home").withContents(DEFAULT_NUMBER_PAGE).withResponseId("number");
        this.target.addClientStub(newHome, VIP_CLIENT);
        when(VIP_CLIENT).requests(HOME_PAGE).then(DEFAULT_NUMBER_PAGE);
        when(NEW_CLIENT).requests(HOME_PAGE).then(DEFAULT_HOME_PAGE);
        when(ANY_CLIENT).requests(HOME_PAGE).then(DEFAULT_HOME_PAGE);

        this.target.setActiveResponse(VIP_CLIENT, HOME_PAGE, new ResponseId("home"));
        when(VIP_CLIENT).requests(HOME_PAGE).then(DEFAULT_HOME_PAGE);
        when(NEW_CLIENT).requests(HOME_PAGE).then(DEFAULT_HOME_PAGE);
        when(ANY_CLIENT).requests(HOME_PAGE).then(DEFAULT_HOME_PAGE);

        this.target.setActiveResponse(VIP_CLIENT, HOME_PAGE, new ResponseId("number"));
        when(VIP_CLIENT).requests(HOME_PAGE).then(DEFAULT_NUMBER_PAGE);
        when(NEW_CLIENT).requests(HOME_PAGE).then(DEFAULT_HOME_PAGE);
        when(ANY_CLIENT).requests(HOME_PAGE).then(DEFAULT_HOME_PAGE);
    }

    @Test
    void shouldReturnCorrectCommonState() {
        final Map<QueryId, GenericResponse> actual = this.target.getCurrentCommonStubs();
        Assertions.assertThat(actual)
            .containsEntry(HOME_PAGE, DEFAULT_HOME_PAGE)
            .containsEntry(NUMBER_PAGE, DEFAULT_NUMBER_PAGE)
            .containsEntry(MISSING_PAGE, NO_SUCH_PAGE);
    }

    @Test
    void shouldCombineCommonAndClientCurrentState() {
        final Stub newHome = Stub.forQueryId("home").withContents(VIP_HOME_PAGE).withResponseId("home");
        this.target.addClientStub(newHome, VIP_CLIENT);
        final Map<QueryId, GenericResponse> actual = this.target.getCurrentClientStubs(VIP_CLIENT);
        Assertions.assertThat(actual)
            .containsEntry(HOME_PAGE, VIP_HOME_PAGE)
            .containsEntry(NUMBER_PAGE, DEFAULT_NUMBER_PAGE)
            .containsEntry(MISSING_PAGE, NO_SUCH_PAGE);
    }

    private When when(ClientId client) {
        return new When(client);
    }

    private class When {
        private final ClientId client;

        public When(ClientId client) {
            this.client = client;
        }

        public Then requests(QueryId query) {
            return new Then(client, query);
        }
    }

    private class Then {
        private final ClientId client;
        private final QueryId query;

        public Then(ClientId client, QueryId query) {
            this.client = client;
            this.query = query;
        }

        public void then(GenericResponse response) {
            Assertions.assertThat(target.nextResponseFor(this.client, this.query)).isEqualTo(response);
        }

        public void thenThrows(RuntimeException exception) {
            Assertions.assertThatThrownBy(() -> target.nextResponseFor(this.client, this.query))
                .isInstanceOf(exception.getClass())
                .hasMessage(exception.getMessage());
        }
    }
}
