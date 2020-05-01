package de.wolfsvl.copper2go.workflow;

import de.wolfsvl.copper2go.workflowapi.Context;
import de.wolfsvl.copper2go.workflowapi.ContextStore;

/**
 * Not thread safe class for usage in one workflow thread only.
 */
public class HelloContext extends Context {
    final String uuid;
    final ContextStore contextStore;
    String name;
    String response;

    HelloContext(String uuid, ContextStore contextStore) {
        super(contextStore.getContext(uuid).getRequest());
        this.uuid = uuid;
        this.contextStore = contextStore;
    }
}
