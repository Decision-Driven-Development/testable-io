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
import java.util.function.Function;
import lombok.NonNull;

/**
 * GenericResponse is a class that represents a response object with generic contents.
 *
 * @param <T> The type of the contents of the response. This can be any type of object, such as a
 * string, JSON object, or any other data structure.
 * @param contents The contents of the response. This can be any type of object, such as a string,
 * JSON object, or any other data structure.
 * @param delay The delay in milliseconds before the response is sent. This can be used to
 * simulate network latency or processing time. Is expressed in milliseconds and
 * expected to be positive or zero.
 * @param metadata The metadata associated with the response. This can include any additional
 * information that is relevant to the response but not necessarily part of the
 * response data itself.
 * @since 0.1
 */
public record GenericResponse<T>(@NonNull T contents, int delay, Map<String, Object> metadata) {
    /**
     * This method allows you to transform the contents of the response to any class using a
     * provided transformer function.
     *
     * @param transformer The function that will be used to transform the contents of the response.
     * @param <R> The type of the transformed contents.
     * @return The transformed contents of the response.
     */
    public <R> R contentsAs(final Function<GenericResponse<T>, R> transformer) {
        if (transformer == null) {
            throw new IllegalArgumentException("Transformer function cannot be null");
        }
        return transformer.apply(this);
    }
}
