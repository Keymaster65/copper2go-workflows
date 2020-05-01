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
import org.copperengine.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WorkflowDescription(alias = "Hello", majorVersion = 1, minorVersion = 0, patchLevelVersion = 0)
public class Hello extends Workflow<HelloData> {
    private static final Logger logger = LoggerFactory.getLogger(Hello.class);

    private transient ContextStore contextStore;

    private final transient RequestReceiver requestReceiver = new RequestReceiver();
    private final transient ResponseSender responseSender = new ResponseSender();
    private final transient Mapper mapper = new Mapper();
    private final transient BusinessRules businessRules = new BusinessRules();

    @AutoWire
    public void setContextStore(ContextStore contextStore) {
        this.contextStore = contextStore;
    }

    @Override
    public void main() throws Interrupt {
        logger.info("begin workflow 1.0");
        long startMillis = System.currentTimeMillis();
        HelloContext context = requestReceiver.receiveMessage(getData().getUUID(), contextStore);
        mapper.mapRequest(context);
        wait(WaitMode.FIRST, context.name.length() * 100 + 1, "dummy");
        businessRules.calculatePrice(context, startMillis, System.currentTimeMillis());;
        mapper.mapResponse(context);
        responseSender.sendResponse(context, contextStore);
        logger.info("finish workflow 1.0");
    }



}
