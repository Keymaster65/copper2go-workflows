/*
 * Copyright 2019 Wolf Sluyterman van Langeweyde
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.wolfsvl.copper2go.workflow;

import de.wolfsvl.copper2go.workflowapi.ReplyChannelStore;
import de.wolfsvl.copper2go.workflowapi.WorkflowData;
import org.copperengine.core.AutoWire;
import org.copperengine.core.Interrupt;
import org.copperengine.core.Workflow;
import org.copperengine.core.WorkflowDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WorkflowDescription(alias = "Pricing", majorVersion = 1, minorVersion = 0, patchLevelVersion = 0)
public class Pricing extends Workflow<WorkflowData> {
    private static final Logger logger = LoggerFactory.getLogger(Pricing.class);
    private static final long serialVersionUID = 1L;

    private transient ReplyChannelStore replyChannelStore;

    @AutoWire
    public void setReplyChannelStore(ReplyChannelStore replyChannelStore) {
        this.replyChannelStore = replyChannelStore;
    }

    @Override
    public void main() throws Interrupt {
        logger.info("begin workflow 1.0");
        replyChannelStore.reply(getData().getUUID(), "600");
        logger.info("finish workflow 1.0");
    }
}
