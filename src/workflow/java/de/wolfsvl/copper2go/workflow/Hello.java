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

import de.wolfsvl.copper2go.workflowapi.ContextStore;
import de.wolfsvl.copper2go.workflowapi.HelloData;
import org.copperengine.core.AutoWire;
import org.copperengine.core.Interrupt;
import org.copperengine.core.WaitMode;
import org.copperengine.core.Workflow;
import org.copperengine.core.WorkflowDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WorkflowDescription(alias = "Hello", majorVersion = 1, minorVersion = 0, patchLevelVersion = 0)
public class Hello extends Workflow<HelloData> {
    private static final Logger logger = LoggerFactory.getLogger(Hello.class);

    @SuppressWarnings("FieldCanBeLocal") // need it as example anf starting point of technical discussion
    private String name;

    private transient ContextStore contextStore;

    @AutoWire
    public void setContextStore(ContextStore contextStore) {
        this.contextStore = contextStore;
    }


    public String getRequest() {
        return contextStore.getContext(getData().getUUID()).getRequest();
    }

    public void reply(final String message) {
        contextStore.reply(getData().getUUID(), message);
    }

    @Override
    public void main() throws Interrupt {
        logger.info("begin workflow 1.0");
        long startMillis = System.currentTimeMillis();
        name = Mapper.mapRequest(getRequest());
        wait(WaitMode.FIRST, getRequest().length() + 1, "dummy");
        reply(Mapper.mapResponse(this.name, BusinessRules.calculatePrice(startMillis, System.currentTimeMillis())));
        logger.info("finish workflow 1.0");
    }


}
