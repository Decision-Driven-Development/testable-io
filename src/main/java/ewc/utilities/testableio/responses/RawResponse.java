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

package ewc.utilities.testableio.responses;


import java.util.Map;
import java.util.function.BiFunction;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

/**
 * I am a stubbed response, that is, some content and metadata associated with identity information.
 *
 * @since 0.3
 */
@EqualsAndHashCode
public class RawResponse implements Response {
    /**
     * Main content of the stubbed response. Cannot be null.
     */
    private final Object content;

    /**
     * Metadata associated with the stubbed response. Cannot be null.
     */
    private final Map<String, Object> metadata;

    /**
     * Primary constructor.
     *
     * @param content The main content of the stubbed response. Cannot be null.
     * @param metadata The metadata associated with the stubbed response. Cannot be null.
     */
    public RawResponse(
        @NonNull final Object content,
        @NonNull final Map<String, Object> metadata
    ) {
        this.content = content;
        this.metadata = metadata;
    }

    public RawResponse(@NonNull final Object content) {
        this(content, Map.of());
    }

    public <R> R convertedUsing(BiFunction<Object, Map<String, Object>, R> converter) {
        return converter.apply(this.content, this.metadata);
    }

    @Override
    public <R> R next(BiFunction<Object, Map<String, Object>, R> transformer) {
        return this.convertedUsing(transformer);
    }
}
