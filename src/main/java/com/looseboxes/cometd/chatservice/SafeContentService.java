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
 * @author USER
 */
public interface SafeContentService {

    /**
     * This call may invoke third party services.
     * The result may be cached, for use on subsequent calls.
     * @param text The content to flag
     * @return The flags, if the content is flagged as unsafe. E.g of flags = 
     * <code>adult,violence,racy,graphic,medical,spoof</code>; empty text if the 
     * content is flagged as safe or <code>null</code> if the safety or otherwise
     * of the content could not be ascertained.
     */
    String flag(String text);
}
