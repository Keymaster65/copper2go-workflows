package de.wolfsvl.copper2go.workflow;

import de.wolfsvl.copper2go.workflowapi.Context;

public class TestContext {
    static Context createDefaultContext() {
        return new Context() {

            @Override
            public String getRequest() {
                return "Wolf S.";
            }

            @Override
            public void reply(final String message) {

            }
        };
    }
}
