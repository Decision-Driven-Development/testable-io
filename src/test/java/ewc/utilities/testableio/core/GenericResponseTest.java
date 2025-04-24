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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit-tests for the {@link GenericResponse} class.
 *
 * @since 0.1
 */
final class GenericResponseTest {
    @Test
    void shouldInstantiate() {
        final GenericResponse target = GenericResponseTest.getGenericResponse();
        Assertions.assertThat(target).isNotNull();
    }

    @Test
    void shouldBeConvertedToString() {
        final GenericResponse target = GenericResponseTest.getGenericResponse();
        final String result = target.contentsAs(r -> r.contents().toString());
        Assertions.assertThat(result).isEqualTo("test");
    }

    @Test
    void shouldBeConvertedToAnyClass() {
        final GenericResponse target = GenericResponseTest.getGenericResponse();
        final HttpResponseSample result = target.contentsAs(
            response -> new HttpResponseSample(
                response.contents().toString(),
                (Integer) response.metadata().get("http_response_code"),
                (String) response.metadata().get("x-header")
            )
        );
        Assertions.assertThat(result)
            .isEqualTo(new HttpResponseSample("test", 200, "12345"));
    }

    private static GenericResponse getGenericResponse() {
        return new GenericResponse(
            "test",
            1000,
            Map.of("http_response_code", 200, "x-header", "12345")
        );
    }

    record HttpResponseSample(String body, int statusCode, String header) {
    }
}
