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

/**
 * The utility class providing several predefined test objects.
 *
 * @since 0.2
 */
public final class Mocks {
    private Mocks() {
        // Prevent instantiation of the utility class
    }

    /**
     * A test response object that can be used for testing purposes. It contains an empty map of
     * metadata, a delay of 0 milliseconds and a simple string as the contents.
     *
     * @return A new instance of the {@link GenericResponse} with the default values.
     */
    static GenericResponse defaultResponse() {
        return new GenericResponse("test default");
    }

    /**
     * A simple test response that contains empty string as the contents.
     *
     * @return A new instance of the {@link GenericResponse} with empty string as the contents.
     */
    static GenericResponse emptyResponse() {
        return new GenericResponse("");
    }

    /**
     * A simple test response that contains an exception as the contents.
     *
     * @return A new instance of the {@link GenericResponse} with an exception as the contents.
     */
    static GenericResponse errorResponse() {
        return new GenericResponse(new RuntimeException("test error"));
    }

    /**
     * A test stub that can be used for testing purposes. It returns an empty response for all the
     * requests made to "test_request" query ID.
     *
     * @return An instance of the {@link Stub} with empty response.
     */
    static Stub emptyStub() {
        return Stub
            .forQueryId("test_request")
            .withContents(Mocks.emptyResponse())
            .withResponseId("empty_response");
    }

    static Stub errorStub() {
        return Stub
            .forQueryId("test_request")
            .withContents(Mocks.errorResponse())
            .withResponseId("error_response");
    }

    static Stub defaultStub() {
        return Stub
            .forQueryId("test_request")
            .withContents(Mocks.defaultResponse())
            .withResponseId("default_response");
    }

    static Stub anotherQueryStub() {
        return Stub
            .forQueryId("another_request")
            .withContents(Mocks.defaultResponse())
            .withResponseId("another_response");
    }
}
