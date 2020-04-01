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
package com.looseboxes.cometd.chatservice;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author USER
 */
public abstract class AbstractBuilderTest {

    public abstract Builder getBuilder();
    
    public <T> void whenMethodCalled_shouldUpdateBuilderWithCallArg(
            String methodName, T parameter) {
        this.whenMethodCalled_shouldUpdateBuilderWithCallArg(
                methodName, (Class<T>)parameter.getClass(), parameter);
    }
    
    public <T> void whenMethodCalled_shouldUpdateBuilderWithCallArg(
            String methodName, Class<T> parameterType, T parameter) {
        System.out.println(methodName + "_whenCalled_shouldUpdateBuilderWithCallArg");
        this.whenCalled_shouldReturnValidResult(
                getBuilder(), methodName, parameterType, parameter);
    }

    public <T> void whenCalled_shouldReturnValidResult(
            Builder builder, String methodName, 
            Class<T> parameterType, T parameter) {
    
        final T result = this.whenCalled_shouldReturn(
                builder, methodName, parameterType, parameter);
        
        assertThat(result, is(parameter));
    }
    
    public <T> T whenCalled_shouldReturn(String methodName, 
            Class<T> parameterType, T parameter) {
    
        return this.whenCalled_shouldReturn(this.getBuilder(), 
                methodName, parameterType, parameter);
    }
    
    public <T> T whenCalled_shouldReturn(
            Builder builder, String methodName, 
            Class<T> parameterType, T parameter) {

        try{
        
            builder.getClass().getMethod(methodName, parameterType)
                    .invoke(builder, parameter);
        
            final Object response = builder.build();
            
            final String getterMethodName = 
                    toGetterMethodName(methodName, parameterType);
            
            return (T)response.getClass().getMethod(getterMethodName).invoke(response);
        
        }catch(Exception e) {
        
            throw new RuntimeException(e);
        }
    }
    
    public String toGetterMethodName(String name, Class parameterType) {
        
        final String prefix = parameterType.equals(boolean.class) || 
                parameterType.equals(Boolean.class)
                ? "is" : "get";
        
        return prefix + 
                Character.toTitleCase(name.charAt(0)) + 
                name.substring(1);
    }
}
