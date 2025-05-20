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
import ewc.utilities.testableio.exceptions.NoMoreResponsesException;
import ewc.utilities.testableio.responses.ExceptionResponse;
import ewc.utilities.testableio.responses.RawResponse;
import ewc.utilities.testableio.responses.SequencedResponse;
import java.util.Map;
import java.util.function.BiFunction;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * End-to-end tests, like the real use cases for the library.
 *
 * @since 0.2
 */
final class EndToEndTest {
    private static final QueryId HOME_PAGE = new QueryId("home");
    private static final String HOME_PAGE_CONTENTS = "html for the home page";
    private static final RawResponse HOME_PAGE_RESPONSE = new RawResponse(HOME_PAGE_CONTENTS);
    private static final String VIP_PAGE_CONTENTS = "VIP home page";
    private static final RawResponse VIP_PAGE_RESPONSE = new RawResponse(VIP_PAGE_CONTENTS);
    private static final QueryId MISSING_PAGE = new QueryId("non-existing");
    private static final IllegalStateException NO_SUCH_PAGE_EXCEPTION = new IllegalStateException("no such page");
    private static final ExceptionResponse MISSING_PAGE_RESPONSE = new ExceptionResponse(NO_SUCH_PAGE_EXCEPTION);
    private static final QueryId NUMBER_PAGE = new QueryId("number");
    private static final Long NUMBER_PAGE_CONTENTS = 1000L;
    private static final RawResponse NUMBER_PAGE_RESPONSE = new RawResponse(NUMBER_PAGE_CONTENTS);
    private static final QueryId COUNTER_PAGE = new QueryId("counter");
    private static final RawResponse FIRST = new RawResponse("first");
    private static final RawResponse SECOND = new RawResponse("second");
    private static final RawResponse THIRD = new RawResponse("third");
    public static final BiFunction<Object, Map<String, Object>, Object> STRING_CONVERTER = (c, m) -> c.toString();
    public static final BiFunction<Object, Map<String, Object>, Object> IDENTITY_CONVERTER = (c, m) -> c;

    private static final SourceId ANY_CLIENT = new SourceId("any client");
    private static final SourceId VIP_CLIENT = new SourceId("VIP client");
    private static final SourceId NEW_CLIENT = new SourceId("new client");

    private StubFacade facade;

    @BeforeEach
    void setUp() {
        this.facade = StubFacade.basic();
        this.facade.setDefaultStubForQuery(HOME_PAGE, HOME_PAGE_RESPONSE);
        this.facade.setConverterForQuery(HOME_PAGE, STRING_CONVERTER);
        this.facade.setDefaultStubForQuery(NUMBER_PAGE, NUMBER_PAGE_RESPONSE);
        this.facade.setConverterForQuery(NUMBER_PAGE, IDENTITY_CONVERTER);
        this.facade.setDefaultStubForQuery(MISSING_PAGE, MISSING_PAGE_RESPONSE);
        this.facade.setConverterForQuery(COUNTER_PAGE, STRING_CONVERTER);
        this.facade.setDefaultStubForQuery(COUNTER_PAGE, new SequencedResponse(FIRST, SECOND, THIRD));
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
        when(ANY_CLIENT).requests(COUNTER_PAGE).responseIs(FIRST.convertedUsing(STRING_CONVERTER));
        when(NEW_CLIENT).requests(COUNTER_PAGE).responseIs(SECOND.convertedUsing(STRING_CONVERTER));
        when(VIP_CLIENT).requests(COUNTER_PAGE).responseIs(THIRD.convertedUsing(STRING_CONVERTER));
    }

    @Test
    void shouldSetSpecificStubForASource() {
        this.facade.setStubForQuerySource(VIP_CLIENT, HOME_PAGE, VIP_PAGE_RESPONSE);

        when(VIP_CLIENT).requests(HOME_PAGE).responseIs(VIP_PAGE_CONTENTS);
        when(NEW_CLIENT).requests(HOME_PAGE).responseIs(HOME_PAGE_CONTENTS);
        when(ANY_CLIENT).requests(HOME_PAGE).responseIs(HOME_PAGE_CONTENTS);
    }

    @Test
    void shouldThrowAfterTheResponseSequenceIsExhausted() {
        this.facade.setStubForQuerySource(
            VIP_CLIENT,
            HOME_PAGE,
            new SequencedResponse(
                VIP_PAGE_RESPONSE,
                MISSING_PAGE_RESPONSE,
                NUMBER_PAGE_RESPONSE
            )
        );

        when(VIP_CLIENT).requests(HOME_PAGE).responseIs(VIP_PAGE_CONTENTS);
        when(VIP_CLIENT).requests(HOME_PAGE).errorIs(NO_SUCH_PAGE_EXCEPTION);
        when(VIP_CLIENT).requests(HOME_PAGE).responseIs(NUMBER_PAGE_CONTENTS.toString());
        when(VIP_CLIENT).requests(HOME_PAGE)
            .errorIs(new NoMoreResponsesException("home"));
    }

    @Test
    void shouldReturnCorrectCommonState() {
        assertThat(this.facade.activeStubsForSource(ANY_CLIENT))
            .containsExactlyInAnyOrderEntriesOf(Map.of(
                HOME_PAGE, HOME_PAGE_RESPONSE,
                NUMBER_PAGE, NUMBER_PAGE_RESPONSE,
                MISSING_PAGE, MISSING_PAGE_RESPONSE,
                COUNTER_PAGE, FIRST
            ));
    }

    @Test
    void shouldCombineCommonAndSourceCurrentState() {
        this.facade.setStubForQuerySource(VIP_CLIENT, HOME_PAGE, VIP_PAGE_RESPONSE);
        assertThat(this.facade.activeStubsForSource(VIP_CLIENT))
            .containsExactlyInAnyOrderEntriesOf(Map.of(
                HOME_PAGE, VIP_PAGE_RESPONSE,
                NUMBER_PAGE, NUMBER_PAGE_RESPONSE,
                MISSING_PAGE, MISSING_PAGE_RESPONSE,
                COUNTER_PAGE, FIRST
            ));
    }

    @Test
    void shouldNotAdvanceSequenceWhileGettingCurrentState() {
        assertThat(this.facade.activeStubsForSource(ANY_CLIENT))
            .containsEntry(COUNTER_PAGE, FIRST);
        when(ANY_CLIENT).requests(COUNTER_PAGE).responseIs(FIRST.convertedUsing(STRING_CONVERTER));
        assertThat(this.facade.activeStubsForSource(ANY_CLIENT))
            .containsEntry(COUNTER_PAGE, SECOND);
        when(ANY_CLIENT).requests(COUNTER_PAGE).responseIs(SECOND.convertedUsing(STRING_CONVERTER));
        when(ANY_CLIENT).requests(COUNTER_PAGE).responseIs(THIRD.convertedUsing(STRING_CONVERTER));
    }

    @Test
    void shouldRepresentExceptionsInTheCurrentState() {
        for (int i = 0; i < 3; i++) {
            this.facade.next(ANY_CLIENT, COUNTER_PAGE, String.class);
        }
        assertThatThrownBy(() -> this.facade.activeStubsForSource(ANY_CLIENT).get(COUNTER_PAGE).next(STRING_CONVERTER))
            .isInstanceOf(NoMoreResponsesException.class)
            .hasMessageContaining("No more responses available for query: counter");

    }

    @Test
    void shouldResetStubsForSource() {
        this.facade.setStubForQuerySource(VIP_CLIENT, HOME_PAGE, VIP_PAGE_RESPONSE);
        this.facade.resetStubsForSource(VIP_CLIENT);
        assertThat(this.facade.activeStubsForSource(VIP_CLIENT))
            .containsExactlyInAnyOrderEntriesOf(Map.of(
                HOME_PAGE, HOME_PAGE_RESPONSE,
                NUMBER_PAGE, NUMBER_PAGE_RESPONSE,
                MISSING_PAGE, MISSING_PAGE_RESPONSE,
                COUNTER_PAGE, FIRST
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
            assertThat(facade.next(this.source, this.query, expected.getClass())).isEqualTo(expected);
        }

        public void errorIs(RuntimeException expected) {
            assertThatThrownBy(() -> facade.next(this.source, this.query, expected.getClass()))
                .isInstanceOf(expected.getClass())
                .hasMessage(expected.getMessage());
        }
    }
}
