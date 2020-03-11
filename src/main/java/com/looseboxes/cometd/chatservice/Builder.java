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

/**
 * The parent specification for Builder types.
 * @author USER
 * @param <T> The type of the instances this Builder builds and returns via the
 * {@link #build()} method.
 */
public interface Builder<T> {

    /**
     * This method may only be called once per Builder, otherwise it will throw
     * an exception. So if in doubt use {@link #isBuildAttempted()} to confirm 
     * if this method has already been called.
     * @return The newly created instance.
     * @see #isBuildAttempted() 
     */
    T build();
    
    /**
     * @return <code>true</code> if the {@link #build()} method has already been
     * called, otherwise returns <code>false</code>
     */
    boolean isBuildAttempted();
}
