package de.wolfsvl.copper2go.workflow;

import de.wolfsvl.copper2go.workflowapi.ContextStore;

class RequestReceiver {
    public HelloContext receiveMessage(final String uuid, final ContextStore contextStore) {
        return new HelloContext(uuid, contextStore);
    }
}
