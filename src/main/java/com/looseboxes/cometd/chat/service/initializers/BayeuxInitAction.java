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
package com.looseboxes.cometd.chat.service.initializers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import org.cometd.bayeux.server.BayeuxServer;

/**
 * @author USER
 */
public interface BayeuxInitAction<T> extends BiFunction<BayeuxServer, List<T>, BayeuxServer>{

    default BayeuxServer apply(BayeuxServer bayeux, T... args) {
        return this.apply(bayeux, args == null || args.length == 0 ? 
                Collections.EMPTY_LIST : Arrays.asList(args));
    }

    @Override
    BayeuxServer apply(BayeuxServer bayeux, List<T> args);
}
