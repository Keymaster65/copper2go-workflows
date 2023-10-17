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
package de.wolfsvl.copper2go.workflow.hello.v3;

import de.wolfsvl.copper2go.workflow.hello.Constants;
import io.github.keymaster65.copper2go.api.util.Copper2goDependencyInjector;
import io.github.keymaster65.copper2go.api.util.WorkflowTestRunner;
import io.github.keymaster65.copper2go.api.workflow.ReplyChannelStore;
import io.github.keymaster65.copper2go.api.workflow.RequestChannelStore;
import io.github.keymaster65.copper2go.api.workflow.WorkflowData;
import org.copperengine.core.Acknowledge;
import org.copperengine.core.Response;
import org.copperengine.core.tranzient.TransientScottyEngine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class HelloWorkflowTest {

    @Test
    void helloTest() throws Exception {
        final ReplyChannelStore replyChannelStoreMock = mock(ReplyChannelStore.class);

        TransientScottyEngine engine = WorkflowTestRunner.createTestEngine(
                Constants.WORKFLOW_DIR,
                new Copper2goDependencyInjector(
                        replyChannelStoreMock,
                        null,
                        null
                )
        );
        WorkflowTestRunner.runTest(
                new WorkflowData(Constants.UUID, Constants.TEST_NAME),
                new WorkflowTestRunner.WorkflowDefinition(Constants.WORKFLOW_NAME, 1L, 0L),
                engine
        );

        verify(replyChannelStoreMock).reply(Constants.UUID, "HEllo " + Constants.TEST_NAME + "! (Fix the bug;-)");
    }

    @ParameterizedTest
    @ValueSource(longs = {2, 3})
    void helloTest(final long majorVersion) throws Exception {
        final ReplyChannelStore replyChannelStoreMock = mock(ReplyChannelStore.class);
        final RequestChannelStore requestChannelStoreMock = mock(RequestChannelStore.class);

        TransientScottyEngine engine = WorkflowTestRunner.createTestEngine(
                Constants.WORKFLOW_DIR,
                new Copper2goDependencyInjector(
                        replyChannelStoreMock,
                        null,
                        requestChannelStoreMock
                )
        );
        doAnswer(invocation -> {
            String responseCorrelationId = invocation.getArgument(2);
            Response<String> copperResponse = new Response<>(responseCorrelationId, "0", null);
            engine.notify(copperResponse, new Acknowledge.BestEffortAcknowledge());
            return null;
        }).when(requestChannelStoreMock).request(eq(Constants.PRICING_CENT_PER_MINUTE_CHANNEL), eq(Constants.TEST_NAME), any());

        WorkflowTestRunner.runTest(
                new WorkflowData(Constants.UUID, Constants.TEST_NAME),
                new WorkflowTestRunner.WorkflowDefinition(Constants.WORKFLOW_NAME, majorVersion, 0L),
                engine
        );

        verify(replyChannelStoreMock).reply(Constants.UUID, "Hello " + Constants.TEST_NAME + "! Please transfer 0 cent");
    }
}

