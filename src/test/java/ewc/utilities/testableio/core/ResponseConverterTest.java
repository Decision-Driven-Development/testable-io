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

import ewc.utilities.testableio.responses.RawResponse;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class ResponseConverterTest {

    public static final RawResponse FIRST_RESPONSE = new RawResponse(
        new ResponseId("first"),
        "some test response",
        Map.of("http_response_code", 200, "x-header", "12345")
    );
    public static final RawResponse SECOND_RESPONSE = new RawResponse(
        new ResponseId("second"),
        "the second response",
        Map.of("http_response_code", 404, "x-header", "00000")
    );

    private StubbedQuery stub;
    private ResponseConverter target;

    @BeforeEach
    void setUp() {
        this.stub = StubbedQuery.from(
            "test endpoint",
            new StubbedResponse[]{
                new StubbedResponse(FIRST_RESPONSE),
                new StubbedResponse(SECOND_RESPONSE)
            }
        );
        target = new ResponseConverter();
    }

    @Test
    void shouldHaveTheDefaultStringConverter() {
        Assertions.assertThat(target.asString(this.stub.next()))
            .startsWith("some test response {");
        Assertions.assertThat(target.asString(this.stub.next()))
            .startsWith("the second response {");
    }

    @Test
    void shouldConvertUsingProvidedConverter() {
        Assertions.assertThat(target.convert(this.stub.next(), SampleHttpResponse.RESPONSE_CONVERTER))
            .isEqualTo(new SampleHttpResponse("some test response", 200, "12345"));
        Assertions.assertThat(target.convert(this.stub.next(), SampleHttpResponse.RESPONSE_CODE))
            .isEqualTo(404);
    }

    @Test
    void shouldChooseAConverterBasedOnQueryId() {
        final QueryId firstQueryId = new QueryId("test endpoint");
        this.target.addConverter(
            firstQueryId,
            SampleHttpResponse.RESPONSE_CONVERTER,
            SampleHttpResponse.class
        );
        final QueryId secondQueryId = new QueryId("error endpoint");
        final StubbedQuery anotherStub = StubbedQuery.from(
            "error endpoint",
            new StubbedResponse[]{
                new StubbedResponse(SECOND_RESPONSE)
            }
        );
        this.target.addConverter(
            secondQueryId,
            SampleHttpResponse.RESPONSE_CODE,
            Integer.class
        );
        Assertions.assertThat(target.next(this.stub, SampleHttpResponse.class))
            .isEqualTo(new SampleHttpResponse("some test response", 200, "12345"));
        Assertions.assertThat(target.next(anotherStub, Integer.class))
            .isEqualTo(404);
    }
}
