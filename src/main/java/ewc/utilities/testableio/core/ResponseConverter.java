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

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * I am a converter for response objects.
 *
 * @since 0.3
 */
public class ResponseConverter {
    /**
     * The default function that converts the {@link RawResponse} to a string.
     */
    private static final BiFunction<Object, Map<String, Object>, String> STRING_CONVERTER =
        (content, metadata) ->
            "%s %s".formatted(content.toString(), metadata.toString());

    private final Map<Pair<?>, BiFunction<Object, Map<String, Object>, ?>> converters;

    /**
     * Primary constructor.
     */
    public ResponseConverter() {
        this.converters = new HashMap<>();
    }

    public String asString(RawResponse response) {
        return response.convertedUsing(STRING_CONVERTER);
    }

    public <T> T convert(RawResponse next,
                         BiFunction<Object, Map<String, Object>, T> converter) {
        return next.convertedUsing(converter);
    }

    @SuppressWarnings("unchecked")
    public <T> T convert(QueryId queryId, RawResponse next, Class<T> clazz) {
        BiFunction<Object, Map<String, Object>, T> converter =
            (BiFunction<Object, Map<String, Object>, T>) this.converters.get(new Pair<>(queryId, clazz));
        if (converter == null) {
            throw new IllegalArgumentException("No converter found for queryId: " + queryId + " and class: " + clazz);
        }
        return next.convertedUsing(converter);
    }

    public <T> void addConverter(QueryId queryId, BiFunction<Object, Map<String, Object>, T> converter, Class<T> clazz) {
        this.converters.put(new Pair<>(queryId, clazz), converter);
    }

    public <T> T next(StubbedQuery stub, Class<T> clazz) {
        return this.convert(stub.queryId(), stub.next(), clazz);
    }

    record Pair<T>(QueryId queryId, T clazz) {
    }
}
