package de.wolfsvl.copper2go.workflow;

import io.github.keymaster65.copper2go.util.Copper2goDependencyInjector;
import io.github.keymaster65.copper2go.util.WorkflowTestRunner;
import io.github.keymaster65.copper2go.workflowapi.ReplyChannelStore;
import io.github.keymaster65.copper2go.workflowapi.RequestChannelStore;
import io.github.keymaster65.copper2go.workflowapi.WorkflowData;
import org.copperengine.core.Acknowledge;
import org.copperengine.core.Response;
import org.copperengine.core.tranzient.TransientScottyEngine;
import org.junit.jupiter.api.Test;

import static de.wolfsvl.copper2go.workflow.Hello2.PRICING_CENT_PER_MINUTE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class HelloWorkflowTest {

    public static final String WORKFLOW_DIR = "src/workflow/java";
    public static final String TEST_NAME = "Wolf";
    public static final String WORKFLOW_NAME = "Hello";
    public static final String UUID = "uuid";

    @Test
    void helloTest() throws Exception {
        final ReplyChannelStore replyChannelStoreMock = mock(ReplyChannelStore.class);

        TransientScottyEngine engine = WorkflowTestRunner.createTestEngine(
                WORKFLOW_DIR,
                new Copper2goDependencyInjector(
                        replyChannelStoreMock,
                        null,
                        null
                )
        );
        WorkflowTestRunner.runTest(
                new WorkflowData(UUID, TEST_NAME),
                new WorkflowTestRunner.WorkflowDefinition(WORKFLOW_NAME, 1L, 0L),
                engine
        );

        verify(replyChannelStoreMock).reply(UUID, "HEllo " + TEST_NAME + "! (Fix the bug;-)");
    }

    @Test
    void hello2Test() throws Exception {
        final ReplyChannelStore replyChannelStoreMock = mock(ReplyChannelStore.class);
        final RequestChannelStore requestChannelStoreMock = mock(RequestChannelStore.class);

        TransientScottyEngine engine = WorkflowTestRunner.createTestEngine(
                WORKFLOW_DIR,
                new Copper2goDependencyInjector(
                        replyChannelStoreMock,
                        null,
                        requestChannelStoreMock
                )
        );
        doAnswer(invocation -> {
            String responseRorrelationId = invocation.getArgument(2);
            Response<String> copperResponse = new Response<>(responseRorrelationId, "0", null);
            engine.notify(copperResponse, new Acknowledge.BestEffortAcknowledge());
            return null;
        }).when(requestChannelStoreMock).request(eq(PRICING_CENT_PER_MINUTE), eq(TEST_NAME), any());

        WorkflowTestRunner.runTest(
                new WorkflowData(UUID, TEST_NAME),
                new WorkflowTestRunner.WorkflowDefinition(WORKFLOW_NAME, 2L, 0L),
                engine
        );

        verify(replyChannelStoreMock).reply(UUID, "Hello " + TEST_NAME +"! Please transfer 0 cent");
    }
}

