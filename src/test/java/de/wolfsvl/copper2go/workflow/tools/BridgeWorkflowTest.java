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

package de.wolfsvl.copper2go.workflow.tools;

import io.github.keymaster65.copper2go.util.Copper2goDependencyInjector;
import io.github.keymaster65.copper2go.util.WorkflowTestRunner;
import io.github.keymaster65.copper2go.workflowapi.EventChannelStore;
import io.github.keymaster65.copper2go.workflowapi.ReplyChannelStore;
import io.github.keymaster65.copper2go.workflowapi.RequestChannelStore;
import io.github.keymaster65.copper2go.workflowapi.WorkflowData;
import org.copperengine.core.Acknowledge;
import org.copperengine.core.CopperException;
import org.copperengine.core.Response;
import org.copperengine.core.tranzient.TransientScottyEngine;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class BridgeWorkflowTest {

    public static final String WORKFLOW_DIR = "src/workflow/java";
    public static final String WORKFLOW_NAME = "Bridge";
    public static final String UUID = "uuid";

    @Test
    void replyGoodCaseTest() throws Exception {
        final ReplyChannelStore replyChannelStoreMock = mock(ReplyChannelStore.class);
        final RequestChannelStore requestChannelStoreMock = mock(RequestChannelStore.class);
        final EventChannelStore eventChannelStoreMock = mock(EventChannelStore.class);

        runWorkflow(replyChannelStoreMock, requestChannelStoreMock, eventChannelStoreMock,
                "Hello", "0", null);

        verify(replyChannelStoreMock, times(1)).reply(any(), eq("Hello"));
        verify(requestChannelStoreMock, times(1)).request(any(),eq("Hello"), any());
    }

    @Test
    void replyExceptionTest() throws Exception {
        final ReplyChannelStore replyChannelStoreMock = mock(ReplyChannelStore.class);
        final RequestChannelStore requestChannelStoreMock = mock(RequestChannelStore.class);
        final EventChannelStore eventChannelStoreMock = mock(EventChannelStore.class);

        runWorkflow(replyChannelStoreMock, requestChannelStoreMock, eventChannelStoreMock,
                "Hello", null, new IllegalArgumentException("Test"));

        verify(replyChannelStoreMock, times(0)).reply(any(), any());
        verify(replyChannelStoreMock, times(1)).replyError(any(), eq("WorkflowRuntimeException: Could call RequestChannel."));
        verify(requestChannelStoreMock, times(1)).request(any(),eq("Hello"), any());
    }

    private void runWorkflow(
            final ReplyChannelStore replyChannelStoreMock,
            final RequestChannelStore requestChannelStoreMock,
            final EventChannelStore eventChannelStoreMock,
            final String payload,
            final String response,
            Exception exception
    ) throws CopperException {
        TransientScottyEngine engine = WorkflowTestRunner.createTestEngine(
                WORKFLOW_DIR,
                new Copper2goDependencyInjector(
                        replyChannelStoreMock,
                        eventChannelStoreMock,
                        requestChannelStoreMock
                )
        );

        doAnswer(invocation -> {
            String responseRorrelationId = invocation.getArgument(2);
            Response<String> copperResponse = new Response<>(responseRorrelationId, response, exception);
            engine.notify(copperResponse, new Acknowledge.BestEffortAcknowledge());
            return null;
        }).when(requestChannelStoreMock).request(eq("RequestChannel"), eq(payload), any());

        WorkflowTestRunner.runTest(
                new WorkflowData(UUID, payload),
                new WorkflowTestRunner.WorkflowDefinition(WORKFLOW_NAME, 1L, 0L),
                engine
        );
    }
}

