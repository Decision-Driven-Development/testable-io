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

import ewc.utilities.testableio.exceptions.NoMoreResponsesException;
import ewc.utilities.testableio.exceptions.UnconfiguredStubException;
import ewc.utilities.testableio.responses.DelayedResponse;
import ewc.utilities.testableio.responses.ExceptionResponse;
import ewc.utilities.testableio.responses.RawResponse;
import ewc.utilities.testableio.responses.ResponseSequence;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BasicStubFacadeTest {
    private static final QueryId TEST_URL = new QueryId("test url");
    private static final QueryId SPECIFIC_URL = new QueryId("specific url");
    private static final SourceId SPECIFIC_SOURCE = new SourceId("specific");
    private StubFacade target;
    private QueryId anyQuery;
    private SourceId anySource;

    @BeforeEach
    void setUp() {
        this.target = StubFacade.basic();
        this.anyQuery = QueryId.random();
        this.anySource = SourceId.random();
    }

    @Test
    void shouldThrowWhenQueryStubHasNoDefault() {
        assertThatThrownBy(() -> target.next(anySource, anyQuery, String.class))
            .isInstanceOf(UnconfiguredStubException.class)
            .hasMessageContaining("No stubs configured for query: %s".formatted(anyQuery.id()));
    }

    @Test
    void shouldReturnDefaultNextResponseForever_whenSetAsSingleObject() {
        target.setDefaultStubForQuery(TEST_URL, new RawResponse("test response"));
        for (int i = 0; i < 100; i++) {
            target.next(anySource, TEST_URL, String.class);
        }
        assertThat(target.next(anySource, TEST_URL, String.class))
            .isEqualTo("test response {}");
    }

    @Test
    void shouldThrow_whenSetAsAnException() {
        target.setDefaultStubForQuery(TEST_URL, new ExceptionResponse(new RuntimeException("test exception")));
        assertThatThrownBy(() -> target.next(anySource, TEST_URL, String.class))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("test exception");
    }

    @Test
    void shouldReturnThePredefinedResponseSequence_whenSetAsSequence() {
        target.setDefaultStubForQuery(TEST_URL, new ResponseSequence(
            new RawResponse("test response 1"),
            new RawResponse("test response 2"),
            new RawResponse("test response 3")
        ));
        assertThat(target.next(anySource, TEST_URL, String.class))
            .isEqualTo("test response 1 {}");
        assertThat(target.next(anySource, TEST_URL, String.class))
            .isEqualTo("test response 2 {}");
        assertThat(target.next(anySource, TEST_URL, String.class))
            .isEqualTo("test response 3 {}");
    }

    @Test
    void shouldThrow_whenThePredefinedSequenceIsExhausted() {
        target.setDefaultStubForQuery(TEST_URL, new ResponseSequence(
            new RawResponse("the only test response")
        ));
        assertThat(target.next(anySource, TEST_URL, String.class))
            .isEqualTo("the only test response {}");
        assertThatExceptionOfType(NoMoreResponsesException.class)
            .isThrownBy(() -> target.next(anySource, TEST_URL, String.class))
            .withMessageContaining("No more responses available for query: %s".formatted(TEST_URL.id()));
    }

    @Test
    void shouldWaitForDelayBeforeReturning() {
        target.setDefaultStubForQuery(TEST_URL, new ResponseSequence(
            new DelayedResponse(new RawResponse("test response 1"), 1000),
            new DelayedResponse(new ExceptionResponse(new RuntimeException("test exception")), 2000))
        );
        final long firstDelay = System.currentTimeMillis();
        assertThat(target.next(anySource, TEST_URL, String.class))
            .isEqualTo("test response 1 {}");
        assertThat(System.currentTimeMillis() - firstDelay)
            .isCloseTo(1000, withinPercentage(1));

        final long secondDelay = System.currentTimeMillis();
        assertThatThrownBy(() -> target.next(anySource, TEST_URL, String.class))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("test exception");
        assertThat(System.currentTimeMillis() - secondDelay)
            .isCloseTo(2000, withinPercentage(1));
    }

    @Test
    void shouldSetUpSpecificResponsesForSpecificSource() {
        target.setDefaultStubForQuery(TEST_URL, new RawResponse("test response"));
        target.setDefaultStubForQuery(SPECIFIC_URL, new RawResponse("default response"));
        target.setStubForQuerySource(SPECIFIC_SOURCE, SPECIFIC_URL, new RawResponse("specific response"));

        assertThat(target.next(SPECIFIC_SOURCE, TEST_URL, String.class))
            .isEqualTo("test response {}");
        assertThat(target.next(SPECIFIC_SOURCE, SPECIFIC_URL, String.class))
            .isEqualTo("specific response {}");
        assertThat(target.next(anySource, SPECIFIC_URL, String.class))
            .isEqualTo("default response {}");
    }

    @Test
    void shouldUseDifferentConvertersForDifferentQueries() {
        target.setDefaultStubForQuery(TEST_URL, new RawResponse("test response"));
        target.setDefaultStubForQuery(SPECIFIC_URL, new RawResponse("default response"));
        target.setConverterForQuery(
            SPECIFIC_URL,
            (content, metadata) -> new ResponseId(content.toString())
        );
        assertThat(target.next(anySource, TEST_URL, String.class))
            .isEqualTo("test response {}");
        assertThat(target.next(anySource, SPECIFIC_URL, ResponseId.class))
            .isEqualTo(new ResponseId("default response"));
    }
}