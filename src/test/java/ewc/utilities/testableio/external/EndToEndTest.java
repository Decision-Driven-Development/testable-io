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

package ewc.utilities.testableio.external;

import ewc.utilities.testableio.core.*;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * End-to-end tests, like the real use cases for the library.
 *
 * @since 0.2
 */
final class EndToEndTest {
    public static final QueryId HOME_PAGE = new QueryId("home");
    public static final QueryId MISSING_PAGE = new QueryId("non-existing");
    public static final QueryId NUMBER_PAGE = new QueryId("number");

    public static final ClientId ANY_CLIENT = new ClientId("any client");
    public static final ClientId VIP_CLIENT = new ClientId("VIP client");
    public static final ClientId NEW_CLIENT = new ClientId("new client");

    private GenericIoStub target;

    @BeforeEach
    void setUp() {
        this.target = new GenericIoStub();
        Stream.of(
            Stub.forQueryId("home")
                .withContents(new GenericResponse("html for the home page"))
                .build(),
            Stub.forQueryId("non-existing")
                .withContents(new GenericResponse(new IllegalStateException("no such page")))
                .build(),
            Stub.forQueryId("number")
                .withContents(new GenericResponse(1000L))
                .build()
        ).forEach(stub -> this.target.addCommonStub(stub));
    }

    @Test
    void shouldHaveAPredefinedSetOfDefaultResponses() {
        Assertions.assertThat(this.target.nextResponseFor(ANY_CLIENT, HOME_PAGE))
            .isEqualTo(new GenericResponse("html for the home page"));
        Assertions.assertThat(this.target.nextResponseFor(VIP_CLIENT, HOME_PAGE))
            .isEqualTo(new GenericResponse("html for the home page"));
        Assertions.assertThatThrownBy(() -> this.target.nextResponseFor(ANY_CLIENT, MISSING_PAGE))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("no such page");
        Assertions.assertThat(this.target.nextResponseFor(ANY_CLIENT, NUMBER_PAGE))
            .isEqualTo(new GenericResponse(1000L));
    }
}
