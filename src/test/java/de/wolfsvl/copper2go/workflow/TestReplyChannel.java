package de.wolfsvl.copper2go.workflow;

import de.wolfsvl.copper2go.workflowapi.ReplyChannel;

public class TestReplyChannel {
    static ReplyChannel createDefaultContext() {
        return new ReplyChannel() {
            
            @Override
            public void reply(final String message) {

            }
        };
    }
}
