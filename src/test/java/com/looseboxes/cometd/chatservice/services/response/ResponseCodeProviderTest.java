/*
 * Copyright 2020 looseBoxes.com
 *
 * Licensed under the looseBoxes Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.looseboxes.cometd.chatservice.services.response;

import com.looseboxes.cometd.chatservice.test.TestConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author USER
 */
public class ResponseCodeProviderTest {

    private final boolean logStackTrace = TestConfig.LOG_STACKTRACE;
    
    @Test
    public void from_whenNullArg_shouldThrowRuntimeException() {
        System.out.println("from_whenNullArg_shouldThrowRuntimeException");
        final Object candidate = null;
        final ResponseCodeProvider resCodeProvider = this.getResponseCodeProvider();
        final int resultIfNone = 500;
        final RuntimeException thrown = Assertions.assertThrows(
                RuntimeException.class, 
                () -> resCodeProvider.from(candidate, resultIfNone));
        if(logStackTrace) {
            thrown.printStackTrace();
        }
    }

    @Test
    public void from_whenInvalidDefaultValue_shouldThrowRuntimeException() {
        System.out.println("from_whenInvalidDefaultValue_shouldThrowRuntimeException");
        final Object candidate = new ShouldReturnProvidedDefault();
        final ResponseCodeProvider resCodeProvider = this.getResponseCodeProvider();
        final int resultIfNone = 1000;
        final RuntimeException thrown = Assertions.assertThrows(
                RuntimeException.class, 
                () -> resCodeProvider.from(candidate, resultIfNone));
        if(logStackTrace) {
            thrown.printStackTrace();
        }
    }

    @Test
    public void from_whenArgWithoutResponseStatus_shouldReturnProvidedDefault() {
        System.out.println("from_whenArgWithoutResponseStatus_shouldReturnProvidedDefault");
        final Object shouldReturnProvidedDefault = new ShouldReturnProvidedDefault();
        final ResponseCodeProvider resCodeProvider = this.getResponseCodeProvider();
        final int resultIfNone = 100;
        final int result = resCodeProvider.from(
                shouldReturnProvidedDefault, resultIfNone);
        assertThat(result, is(resultIfNone));
    }
    
    @Test
    public void from_whenArgWithResponseStatus_shouldReturnValidResult() {
        System.out.println("from_whenArgWithResponseStatus_shouldReturnValidResult");
        final Object shouldReturnStatusOk = new ShouldReturnStatusOk();
        final ResponseCodeProvider resCodeProvider = this.getResponseCodeProvider();
        final int resultIfNone = 500;
        final int result = resCodeProvider.from(shouldReturnStatusOk, resultIfNone);
        assertThat(result, is(HttpStatus.OK.value()));
    }

    public ResponseCodeProvider getResponseCodeProvider() {
        return new ResponseCodeFromSpringAnnotationProvider();
    }
    
    @ResponseStatus(code=HttpStatus.OK, value=HttpStatus.OK)
    private static class ShouldReturnStatusOk{ }
    
    private static class ShouldReturnProvidedDefault{ }
}
