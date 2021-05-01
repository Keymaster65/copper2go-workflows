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

import io.github.keymaster65.copper2go.util.Copper2goDependencyInjector;
import io.github.keymaster65.copper2go.util.WorkflowTestRunner;
import io.github.keymaster65.copper2go.workflowapi.EventChannelStore;
import io.github.keymaster65.copper2go.workflowapi.ReplyChannelStore;
import io.github.keymaster65.copper2go.workflowapi.RequestChannelStore;
import io.github.keymaster65.copper2go.workflowapi.WorkflowData;
import org.copperengine.core.CopperException;
import org.copperengine.core.tranzient.TransientScottyEngine;
import org.junit.jupiter.api.Test;

import static de.wolfsvl.copper2go.workflow.test.Mapper.REPLY_UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SystemTestWorkflowTest {

    public static final String WORKFLOW_DIR = "src/workflow/java";
    public static final String WORKFLOW_NAME = "SystemTest";
    public static final String UUID = "uuid";

    @Test
    void noReplyTest() throws Exception {
        final ReplyChannelStore replyChannelStoreMock = mock(ReplyChannelStore.class);
        final RequestChannelStore requestChannelStoreMock = mock(RequestChannelStore.class);
        final EventChannelStore eventChannelStoreMock = mock(EventChannelStore.class);

        runWorkflow(replyChannelStoreMock, requestChannelStoreMock, eventChannelStoreMock, "");

        verify(replyChannelStoreMock, times(0)).reply(any(), any());
        verify(requestChannelStoreMock, times(1)).request(any(), any(), any());
    }

    @Test
    void replyTest() throws Exception {
        final ReplyChannelStore replyChannelStoreMock = mock(ReplyChannelStore.class);
        final RequestChannelStore requestChannelStoreMock = mock(RequestChannelStore.class);
        final EventChannelStore eventChannelStoreMock = mock(EventChannelStore.class);

        runWorkflow(replyChannelStoreMock, requestChannelStoreMock, eventChannelStoreMock, REPLY_UUID + "=uuid0");

        verify(replyChannelStoreMock, times(1)).reply(any(), any());
        verify(requestChannelStoreMock, times(0)).request(any(), any(), any());
    }
    private void runWorkflow(
            final ReplyChannelStore replyChannelStoreMock,
            final RequestChannelStore requestChannelStoreMock,
            final EventChannelStore eventChannelStoreMock, final String payload
    ) throws CopperException {
        TransientScottyEngine engine = WorkflowTestRunner.createTestEngine(
                WORKFLOW_DIR,
                new Copper2goDependencyInjector(
                        replyChannelStoreMock,
                        eventChannelStoreMock,
                        requestChannelStoreMock
                )
        );
        WorkflowTestRunner.runTest(
                new WorkflowData(UUID, payload),
                new WorkflowTestRunner.WorkflowDefinition(WORKFLOW_NAME, 1L, 0L),
                engine
        );
    }
}

