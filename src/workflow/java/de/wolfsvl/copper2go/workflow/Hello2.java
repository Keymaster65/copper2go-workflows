/*
 * Copyright 2021 Wolf Sluyterman van Langeweyde
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

import io.github.keymaster65.copper2go.workflowapi.ReplyChannelStore;
import io.github.keymaster65.copper2go.workflowapi.EventChannelStore;
import io.github.keymaster65.copper2go.workflowapi.RequestChannelStore;
import io.github.keymaster65.copper2go.workflowapi.WorkflowData;
import org.copperengine.core.AutoWire;
import org.copperengine.core.Interrupt;
import org.copperengine.core.Response;
import org.copperengine.core.WaitMode;
import org.copperengine.core.Workflow;
import org.copperengine.core.WorkflowDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WorkflowDescription(alias = "Hello", majorVersion = 2, minorVersion = 0, patchLevelVersion = 0)
public class Hello2 extends Workflow<WorkflowData> {
    private static final Logger logger = LoggerFactory.getLogger(Hello2.class);
    public static final int PRICING_HELLO_PERMINUTE_TIMEOUT_MSEC = 30000;
    private static final long serialVersionUID = 2;
    public static final String PRICING_CENT_PER_MINUTE = "Pricing.centPerMinute";

    @SuppressWarnings("FieldCanBeLocal") // need it as example anf starting point of technical discussion
    private String name;

    private transient ReplyChannelStore replyChannelStore;

    @AutoWire
    public void setReplyChannelStore(ReplyChannelStore replyChannelStore) {
        this.replyChannelStore = replyChannelStore;
    }

    private transient EventChannelStore eventChannelStore;

    @AutoWire
    public void setEventChannelStore(EventChannelStore eventChannelStore) {
        this.eventChannelStore = eventChannelStore;
    }

    private transient RequestChannelStore requestChannelStore;

    @AutoWire
    public void setRequestChannelStore(RequestChannelStore requestChannelStore) {
        this.requestChannelStore = requestChannelStore;
    }


    public String getRequest() {
        return getData().getPayload();
    }

    public void reply(final String message) {
        replyChannelStore.reply(getData().getUUID(), message);
    }

    public void replyError(final String message) {
        replyChannelStore.replyError(getData().getUUID(), message);
    }

    @Override
    public void main() throws Interrupt {
        try {
            logger.info("begin workflow 2.0");
            long startMillis = System.currentTimeMillis();
            name = Mapper.mapRequest(getRequest());
            String correlationId = getEngine().createUUID();
            callCentPerMinute(Mapper.mapPricingRequest(name), correlationId);

            final String response = Mapper.mapResponse(
                    this.name,
                    BusinessRules.calculatePrice(
                            startMillis,
                            System.currentTimeMillis(),
                            getPricePerMinute(getAndRemoveResponse(correlationId))
                    ));
            reply(response);
        } catch (RuntimeException e) {
            replyError(e.getClass().getSimpleName() +": " + e.getMessage());
            throw e;
        }
        logger.info("finish workflow 2.0");
    }

    private int getPricePerMinute(final Response<String> response) {
        if (response == null) {
            throw new WorkflowRuntimeException("Response is null, could not get price.");
        }
        if (response.isTimeout()) {
            throw new WorkflowRuntimeException("Timeout, could not get price.");
        } else if (null != response.getException()) {
            throw new WorkflowRuntimeException("Could not get price.", response.getException());
        }
        return Integer.parseInt(response.getResponse());
    }


    private void callCentPerMinute(final String pricingRequest, final String correlationId) throws Interrupt {
        requestChannelStore.request(PRICING_CENT_PER_MINUTE, pricingRequest, correlationId);
        wait(WaitMode.FIRST, PRICING_HELLO_PERMINUTE_TIMEOUT_MSEC, correlationId);
    }


}
