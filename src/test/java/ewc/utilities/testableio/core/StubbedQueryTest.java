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

import java.util.Map;
import java.util.NoSuchElementException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit-tests for the {@link SingleQueryResponses} class.
 *
 * @since 0.1
 */
final class StubbedQueryTest {
    @Test
    void shouldReturnTheSameResponseForeverIfItIsASingleResponse() {
        final StubbedQuery<String> responses = StubbedQuery.from(
            "single response",
            StubbedQueryTest.getGenericResponse()
        );
        Assertions.assertThat(responses.next())
            .isEqualTo(responses.next())
            .isEqualTo(responses.next());
        Assertions.assertThat(responses.next())
            .isEqualTo("sample response");
    }

    @Test
    void shouldReturnJustOneResponseIfItIsAnArrayWithOneElement() {
        final StubbedQuery<String> responses = StubbedQuery.from(
            "array with one element",
            new StubbedResponse[]{StubbedQueryTest.getGenericResponse()}
        );
        Assertions.assertThat(responses.next())
            .isEqualTo("sample response");
        Assertions.assertThatThrownBy(responses::next)
            .isInstanceOf(NoSuchElementException.class)
            .hasMessageContaining("No more configured responses for");
    }

    @Test
    void shouldThrowAnExceptionThatIsSetAsAResponse() {
        final StubbedQuery<String> responses = StubbedQuery.from(
            "exception response",
            StubbedResponse.from(new ArithmeticException("test exception"))
        );
        Assertions.assertThatThrownBy(responses::next)
            .isInstanceOf(ArithmeticException.class)
            .hasMessageContaining("test exception");
    }

    @Test
    void shouldReturnAllTheSpecifiedResponsesInOrder() {
        final StubbedQuery<String> responses = StubbedQuery.from(
            "multiple responses",
            new StubbedResponse[]{
                StubbedResponse.from("response 1"),
                StubbedResponse.from(new ArithmeticException("test exception"))
            }
        );
        Assertions.assertThat(responses.next())
            .isEqualTo("response 1");
        Assertions.assertThatThrownBy(responses::next)
            .isInstanceOf(ArithmeticException.class)
            .hasMessageContaining("test exception");
        Assertions.assertThatThrownBy(responses::next)
            .isInstanceOf(NoSuchElementException.class)
            .hasMessageContaining("No more configured responses for multiple responses");
    }

    private static StubbedResponse getGenericResponse() {
        return new StubbedResponse(
            new RawResponse(
                ResponseId.random(),
                "sample response",
                Map.of("http_response_code", 200, "x-header", "12345")
            ),
            0
        );
    }
}
