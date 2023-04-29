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

import io.github.keymaster65.copper2go.api.workflow.ReplyChannelStore;
import io.github.keymaster65.copper2go.api.workflow.RequestChannelStore;
import io.github.keymaster65.copper2go.api.workflow.WorkflowData;
import org.copperengine.core.AutoWire;
import org.copperengine.core.Interrupt;
import org.copperengine.core.Response;
import org.copperengine.core.WaitMode;
import org.copperengine.core.Workflow;
import org.copperengine.core.WorkflowDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.concurrent.atomic.AtomicReference;

@WorkflowDescription(alias = "Hello", majorVersion = 3, minorVersion = 0, patchLevelVersion = 0)
public class Hello3 extends Workflow<WorkflowData> {

    private static final String PRICING_CENT_PER_MINUTE_CHANNEL = "Pricing.centPerMinute";

    private static final int PRICING_HELLO_PERMINUTE_TIMEOUT_MSEC = 30000;

    @Serial
    private static final long serialVersionUID = 1;

    private static final Logger logger = LoggerFactory.getLogger(Hello3.class);

    private final AtomicReference<String> nameRef = new AtomicReference<>();

    private transient ReplyChannelStore replyChannelStore;

    private transient RequestChannelStore requestChannelStore;

    @Override
    public void main() throws Interrupt {
        try {
            logger.info("Begin workflow 3.0.");
            final long startNanos = System.nanoTime();
            final String correlationIdPricing = getEngine().createUUID();

            logger.info("Map workflow request to workflow instance.");
            nameRef.set(Mapper.mapRequest(getRequest()));

            logger.info("Call pricing service");
            callCentPerMinute(Mapper.mapPricingRequest(nameRef.get()), correlationIdPricing);

            logger.info("Mapping pricing service response to workflow reply.");
            final String workflowResponse = Mapper.mapResponse(
                    nameRef.get(),
                    BusinessRules.calculatePrice(
                            startNanos,
                            System.nanoTime(),
                            getPricePerMinute(getAndRemoveResponse(correlationIdPricing))
                    ));

            logger.info("Sending reply of workflow.");
            reply(workflowResponse);

        } catch (RuntimeException e) {

            logger.info("Exceptional finish of workflow.");
            replyError(e.getClass().getSimpleName() + ": " + e.getMessage());
            throw e;
        } finally {
            logger.info("Finish workflow.");
        }
    }

    private long getPricePerMinute(final Response<String> response) {
        logger.info("Getting response of pricing service.");
        if (response == null) {
            throw new WorkflowRuntimeException("Response is null, could not get price.");
        }
        if (response.isTimeout()) {
            throw new WorkflowRuntimeException("Timeout, could not get price.");
        } else if (null != response.getException()) {
            throw new WorkflowRuntimeException("Could not get price.", response.getException());
        }
        return Long.parseLong(response.getResponse());
    }


    private void callCentPerMinute(final String pricingRequest, final String correlationId) throws Interrupt {
        requestChannelStore.request(PRICING_CENT_PER_MINUTE_CHANNEL, pricingRequest, correlationId);
        wait(WaitMode.FIRST, PRICING_HELLO_PERMINUTE_TIMEOUT_MSEC, correlationId);
    }

    private String getRequest() {
        return getData().getPayload();
    }

    private void reply(final String message) {
        replyChannelStore.reply(getData().getUUID(), message);
    }

    private void replyError(final String message) {
        replyChannelStore.replyError(getData().getUUID(), message);
    }

    @SuppressWarnings("unused")
    @AutoWire
    public void setReplyChannelStore(ReplyChannelStore replyChannelStore) {
        this.replyChannelStore = replyChannelStore;
    }

    @SuppressWarnings("unused")
    @AutoWire
    public void setRequestChannelStore(RequestChannelStore requestChannelStore) {
        this.requestChannelStore = requestChannelStore;
    }
}
