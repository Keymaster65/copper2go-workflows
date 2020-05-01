package de.wolfsvl.copper2go.workflow;

import de.wolfsvl.copper2go.workflowapi.ContextStore;

class ResponseSender {
    public void sendResponse(final HelloContext context, final ContextStore contextStore) {
        contextStore.reply(context.uuid, context.response);
    }
}
