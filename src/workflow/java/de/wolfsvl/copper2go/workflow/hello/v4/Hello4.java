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
package de.wolfsvl.copper2go.workflow.hello.v4;

import de.wolfsvl.copper2go.workflow.hello.deprecated.Mapper;
import de.wolfsvl.copper2go.workflow.WorkflowRuntimeException;
import de.wolfsvl.copper2go.workflow.hello.BusinessRules;
import de.wolfsvl.copper2go.workflow.hello.v4.json.PersonFactory;
import de.wolfsvl.copper2go.workflow.hello.v4.model.Person;
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

@WorkflowDescription(alias = "Hello", majorVersion = 4, minorVersion = 0, patchLevelVersion = 0)
public class Hello4 extends Workflow<WorkflowData> {
    /**
     * Use this serializable member to have an example to store workflow object state
     */
    private volatile Person person; //NOSONAR record and member are immutable

    private static final String PRICING_CENT_PER_MINUTE_CHANNEL = "Pricing.centPerMinute";

    private static final int PRICING_HELLO_PERMINUTE_TIMEOUT_MSEC = 30000;

    @Serial
    private static final long serialVersionUID = 1;

    private static final Logger logger = LoggerFactory.getLogger(Hello4.class);

    private transient ReplyChannelStore replyChannelStore;

    private transient RequestChannelStore requestChannelStore;

    @Override
    public void main() throws Interrupt {
        try {
            logger.info("Begin workflow.");
            final long startNanos = System.nanoTime();

            logger.info("Map workflow request to workflow instance model.");
            person = PersonFactory.fromJson(getRequest());

            logger.info("Call pricing service.");
            final String correlationIdPricing = getEngine().createUUID();
            final long pricePerMinute = getPricePerMinute(Mapper.mapPricingRequest(person.firstName()), correlationIdPricing);

            logger.info("Mapping pricing service response to workflow reply.");
            final String workflowResponse = Mapper.mapResponse(
                    person.firstName(),
                    BusinessRules.calculatePrice(
                            startNanos,
                            System.nanoTime(),
                            pricePerMinute
                    )
            );

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

    private long getPricePerMinute(final String pricingRequest, final String correlationIdPricing) throws Interrupt {
        requestChannelStore.request(PRICING_CENT_PER_MINUTE_CHANNEL, pricingRequest, correlationIdPricing);
        wait(WaitMode.FIRST, PRICING_HELLO_PERMINUTE_TIMEOUT_MSEC, correlationIdPricing);
        return getPricePerMinute(getAndRemoveResponse(correlationIdPricing));
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
