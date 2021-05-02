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
package de.wolfsvl.copper2go.workflow.test;

import de.wolfsvl.copper2go.workflow.WorkflowRuntimeException;
import io.github.keymaster65.copper2go.workflowapi.EventChannelStore;
import io.github.keymaster65.copper2go.workflowapi.ReplyChannelStore;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.io.ByteArrayOutputStream;

@WorkflowDescription(alias = "SystemTest", majorVersion = 1, minorVersion = 0, patchLevelVersion = 0)
public class SystemTest extends Workflow<WorkflowData> {
    private static final Logger logger = LoggerFactory.getLogger(SystemTest.class);

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

    public void reply(final String uuid, final String message) {
        replyChannelStore.reply(uuid, message);
    }

    public void replyError(final String message) {
        replyChannelStore.replyError(getData().getUUID(), message);
    }

    @Override
    public void main() throws Interrupt {
        try {
            logger.info("Begin workflow {} 1.0.", this.getClass().getSimpleName());

            final Properties payloadProperties = Mapper.mapRequest(getRequest());
            if (payloadProperties.getProperty(Mapper.REPLY_UUID) != null) {
                reply(payloadProperties.getProperty(Mapper.REPLY_UUID), payloadProperties.toString());
            } else {
                callSystemTestRequestChannel(Mapper.mapSystemTest(payloadProperties, getData().getUUID()));
            }
        } catch (Exception e) {
            replyError(e.getClass().getSimpleName() + ": " + e.getMessage());
            throw new WorkflowRuntimeException("Could not process request: " + getRequest(), e);
        }
        logger.info("Finish workflow {} 1.0.", this.getClass().getSimpleName());
    }

    private void callSystemTestRequestChannel(final Properties inputProperties) throws IOException, Interrupt {
        String correlationId = getEngine().createUUID();
        var propertiesStream = new ByteArrayOutputStream();
        inputProperties.store(propertiesStream, "generated in workflow");
        requestChannelStore.request("SystemTestRequestChannel", propertiesStream.toString(StandardCharsets.ISO_8859_1), correlationId);
        wait(WaitMode.FIRST, 3000, correlationId);
        Response<String> response = getAndRemoveResponse(correlationId);
        if (response == null) {
            throw new WorkflowRuntimeException("Response is null, could not call SystemTest.");
        }
        if (response.isTimeout()) {
            throw new WorkflowRuntimeException("Timeout, could call SystemTest.");
        } else if (null != response.getException()) {
            throw new WorkflowRuntimeException("Could call SystemTest.", response.getException());
        }
    }
}
