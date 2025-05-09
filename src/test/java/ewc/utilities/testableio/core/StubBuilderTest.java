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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit-tests for the {@link Stub.StubBuilder} class.
 *
 * @since 0.2
 */
final class StubBuilderTest {
    /**
     * The query ID for the stub tests.
     */
    private static final QueryId QUERY = new QueryId("recommendationIds");

    /**
     * The response ID for the stub tests.
     */
    private static final ResponseId RESPONSE = new ResponseId("recommendationIds");

    @Test
    void shouldBuildACommonStub() {
        final Stub common = Stub
            .forQueryId(StubBuilderTest.QUERY.query())
            .withContents(Mocks.defaultResponse())
            .withResponseId("recommendationIds");
        Assertions.assertThat(common)
            .isEqualTo(
                new Stub(
                    StubBuilderTest.QUERY,
                    StubBuilderTest.RESPONSE,
                    Mocks.defaultResponse()
                )
            );
    }

    @Test
    void shouldBuildWithSpecificName() {
        final Stub specific = Stub
            .forQueryId(StubBuilderTest.QUERY.query())
            .withContents(Mocks.defaultResponse())
            .withResponseId("myStub");
        Assertions.assertThat(specific)
            .isEqualTo(
                new Stub(
                    StubBuilderTest.QUERY,
                    new ResponseId("myStub"),
                    Mocks.defaultResponse()
                )
            );
    }
}
