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

import ewc.utilities.testableio.utils.MockRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * The unit-tests for {@link GenericIoStub} class.
 *
 * @since 0.1
 */
final class GenericIoStubTest {
    /**
     * The request for the default (unspecified) client.
     */
    private static final GenericRequest ANY_CLIENT =
        new MockRequest().clientUnknown();

    /**
     * The request for a specific client.
     */
    private static final GenericRequest SPECIFIC_CLIENT =
        new MockRequest().assignedToClient();

    /**
     * The response for the default (unspecified) client.
     */
    private static final GenericResponse DEFAULT_RESPONSE =
        new GenericResponse("test default");

    /**
     * The response for the specific client.
     */
    private static final GenericResponse SPECIFIC_RESPONSE =
        new GenericResponse("client-specific response");

    @Test
    void createDefaultStub() {
        final GenericIoStub target = new GenericIoStub();
        target.addDefaultStub("getItemRecommendations", GenericIoStubTest.DEFAULT_RESPONSE);
        Assertions.assertThat(target.nextResponseFor(GenericIoStubTest.ANY_CLIENT))
            .isEqualTo(GenericIoStubTest.DEFAULT_RESPONSE);
        Assertions.assertThat(target.nextResponseFor(GenericIoStubTest.SPECIFIC_CLIENT))
            .isEqualTo(GenericIoStubTest.DEFAULT_RESPONSE);
    }

    @Test
    void createInfiniteStubForSpecificClient() {
        final GenericIoStub target = new GenericIoStub();
        target.addDefaultStub("getItemRecommendations", GenericIoStubTest.DEFAULT_RESPONSE);
        target.addStubForClient(
            "12345",
            "getItemRecommendations",
            GenericIoStubTest.SPECIFIC_RESPONSE
        );
        Assertions.assertThat(target.nextResponseFor(GenericIoStubTest.ANY_CLIENT))
            .isEqualTo(GenericIoStubTest.DEFAULT_RESPONSE);
        Assertions.assertThat(target.nextResponseFor(GenericIoStubTest.SPECIFIC_CLIENT))
            .isEqualTo(GenericIoStubTest.SPECIFIC_RESPONSE);
    }
}
