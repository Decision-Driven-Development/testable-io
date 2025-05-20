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

import ewc.utilities.testableio.core.QueryId;
import ewc.utilities.testableio.core.SourceId;
import ewc.utilities.testableio.core.StubFacade;
import ewc.utilities.testableio.responses.ExceptionResponse;
import ewc.utilities.testableio.responses.RawResponse;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * End-to-end tests, like the real use cases for the library.
 *
 * @since 0.2
 */
final class EndToEndTest {
    private static final QueryId HOME_PAGE = new QueryId("home");
    private static final QueryId MISSING_PAGE = new QueryId("non-existing");
    private static final QueryId NUMBER_PAGE = new QueryId("number");

    private static final SourceId ANY_CLIENT = new SourceId("any client");
    private static final SourceId VIP_CLIENT = new SourceId("VIP client");
    private static final SourceId NEW_CLIENT = new SourceId("new client");
    private static final IllegalStateException NO_SUCH_PAGE_EXCEPTION = new IllegalStateException("no such page");
    private static final ExceptionResponse MISSING_PAGE_RESPONSE = new ExceptionResponse(NO_SUCH_PAGE_EXCEPTION);
    private static final String HOME_PAGE_CONTENTS = "html for the home page";
    private static final RawResponse HOME_PAGE_RESPONSE = new RawResponse(HOME_PAGE_CONTENTS);
    private static final long NUMBER_PAGE_CONTENTS = 1000L;
    private static final RawResponse NUMBER_PAGE_RESPONSE = new RawResponse(NUMBER_PAGE_CONTENTS);
    private static final String VIP_PAGE_CONTENTS = "VIP home page";
    private static final RawResponse VIP_PAGE_RESPONSE = new RawResponse(VIP_PAGE_CONTENTS);

    private StubFacade facade;

    @BeforeEach
    void setUp() {
        this.facade = StubFacade.basic();
        this.facade.setDefaultStubForQuery(HOME_PAGE, HOME_PAGE_RESPONSE);
        this.facade.setConverterForQuery(HOME_PAGE, (c, m) -> c.toString());
        this.facade.setDefaultStubForQuery(NUMBER_PAGE, NUMBER_PAGE_RESPONSE);
        this.facade.setConverterForQuery(NUMBER_PAGE, (c, m) -> c);
        this.facade.setDefaultStubForQuery(MISSING_PAGE, MISSING_PAGE_RESPONSE);
    }

    @Test
    void shouldHaveAPredefinedSetOfDefaultResponses() {
        when(VIP_CLIENT).requests(HOME_PAGE).responseIs(HOME_PAGE_CONTENTS);
        when(NEW_CLIENT).requests(HOME_PAGE).responseIs(HOME_PAGE_CONTENTS);
        when(ANY_CLIENT).requests(HOME_PAGE).responseIs(HOME_PAGE_CONTENTS);
        when(ANY_CLIENT).requests(NUMBER_PAGE).responseIs(NUMBER_PAGE_CONTENTS);
        when(NEW_CLIENT).requests(NUMBER_PAGE).responseIs(NUMBER_PAGE_CONTENTS);
        when(VIP_CLIENT).requests(NUMBER_PAGE).responseIs(NUMBER_PAGE_CONTENTS);
        when(ANY_CLIENT).requests(MISSING_PAGE).errorIs(NO_SUCH_PAGE_EXCEPTION);
        when(NEW_CLIENT).requests(MISSING_PAGE).errorIs(NO_SUCH_PAGE_EXCEPTION);
        when(VIP_CLIENT).requests(MISSING_PAGE).errorIs(NO_SUCH_PAGE_EXCEPTION);
    }

    @Test
    void shouldSetSpecificStubForASource() {
        this.facade.setStubForQuerySource(VIP_CLIENT, HOME_PAGE, VIP_PAGE_RESPONSE);

        when(VIP_CLIENT).requests(HOME_PAGE).responseIs(VIP_PAGE_CONTENTS);
        when(NEW_CLIENT).requests(HOME_PAGE).responseIs(HOME_PAGE_CONTENTS);
        when(ANY_CLIENT).requests(HOME_PAGE).responseIs(HOME_PAGE_CONTENTS);
    }

    @Test
    void shouldReturnCorrectCommonState() {
        Assertions.assertThat(this.facade.activeStubsForSource(ANY_CLIENT))
            .containsExactlyInAnyOrderEntriesOf(Map.of(
                HOME_PAGE, HOME_PAGE_RESPONSE,
                NUMBER_PAGE, NUMBER_PAGE_RESPONSE,
                MISSING_PAGE, MISSING_PAGE_RESPONSE
            ));
    }

    @Test
    void shouldCombineCommonAndSourceCurrentState() {
        this.facade.setStubForQuerySource(VIP_CLIENT, HOME_PAGE, VIP_PAGE_RESPONSE);
        Assertions.assertThat(this.facade.activeStubsForSource(VIP_CLIENT))
            .containsExactlyInAnyOrderEntriesOf(Map.of(
                HOME_PAGE, VIP_PAGE_RESPONSE,
                NUMBER_PAGE, NUMBER_PAGE_RESPONSE,
                MISSING_PAGE, MISSING_PAGE_RESPONSE
            ));
    }

    @Test
    void shouldResetStubsForSource() {
        this.facade.setStubForQuerySource(VIP_CLIENT, HOME_PAGE, VIP_PAGE_RESPONSE);
        this.facade.resetStubsForSource(VIP_CLIENT);
        Assertions.assertThat(this.facade.activeStubsForSource(VIP_CLIENT))
            .containsExactlyInAnyOrderEntriesOf(Map.of(
                HOME_PAGE, HOME_PAGE_RESPONSE,
                NUMBER_PAGE, NUMBER_PAGE_RESPONSE,
                MISSING_PAGE, MISSING_PAGE_RESPONSE
            ));
    }

    private When when(SourceId source) {
        return new When(source);
    }

    private class When {
        private final SourceId source;

        public When(SourceId source) {
            this.source = source;
        }

        public Then requests(QueryId query) {
            return new Then(source, query);
        }
    }

    private class Then {
        private final SourceId source;
        private final QueryId query;

        public Then(SourceId source, QueryId query) {
            this.source = source;
            this.query = query;
        }

        public <T> void responseIs(T expected) {
            Assertions.assertThat(facade.next(this.source, this.query, expected.getClass())).isEqualTo(expected);
        }

        public void errorIs(RuntimeException expected) {
            Assertions.assertThatThrownBy(() -> facade.next(this.source, this.query, expected.getClass()))
                .isInstanceOf(expected.getClass())
                .hasMessage(expected.getMessage());
        }
    }
}
