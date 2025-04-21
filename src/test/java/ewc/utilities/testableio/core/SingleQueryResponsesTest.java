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
final class SingleQueryResponsesTest {
    @Test
    void shouldReturnTheSameResponseForeverIfItIsASingleResponse() {
        final SingleQueryResponses response = new SingleQueryResponses(
            "single response",
            SingleQueryResponsesTest.getGenericResponse()
        );
        Assertions.assertThat(response.next())
            .isEqualTo(response.next())
            .isEqualTo(response.next())
            .extracting("contents").isEqualTo("sample response");
    }

    @Test
    void shouldReturnJustOneResponseIfItIsAnArrayWithOneElement() {
        final SingleQueryResponses response = new SingleQueryResponses(
            "array with one element",
            new GenericResponse<?>[] {SingleQueryResponsesTest.getGenericResponse()}
        );
        Assertions.assertThat(response.next())
            .extracting("contents").isEqualTo("sample response");
        Assertions.assertThatThrownBy(response::next)
            .isInstanceOf(NoSuchElementException.class)
            .hasMessageContaining("No more configured responses for");
    }

    @Test
    void shouldThrowAnExceptionThatIsSetAsAResponse() {
        final SingleQueryResponses response = new SingleQueryResponses(
            "exception response",
            new GenericResponse<>(
                new ArithmeticException("test exception"),
                1000,
                Map.of("http_response_code", 200, "x-header", "12345")
            )
        );
        Assertions.assertThatThrownBy(response::next)
            .isInstanceOf(ArithmeticException.class)
            .hasMessageContaining("test exception");
    }

    @Test
    void shouldReturnAllTheSpecifiedResponsesInOrder() {
        final SingleQueryResponses response = new SingleQueryResponses(
            "multiple responses",
            new GenericResponse<>("response 1", 0, Map.of()),
            new GenericResponse<>(new ArithmeticException("test exception"), 0, Map.of())
        );
        Assertions.assertThat(response.next())
            .extracting("contents").isEqualTo("response 1");
        Assertions.assertThatThrownBy(response::next)
            .isInstanceOf(ArithmeticException.class)
            .hasMessageContaining("test exception");
        Assertions.assertThatThrownBy(response::next)
            .isInstanceOf(NoSuchElementException.class)
            .hasMessageContaining("No more configured responses for multiple responses");
    }

    private static GenericResponse<String> getGenericResponse() {
        return new GenericResponse<>(
            "sample response",
            1000,
            Map.of("http_response_code", 200, "x-header", "12345")
        );
    }
}
