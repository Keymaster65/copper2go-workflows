package de.wolfsvl.copper2go.workflow;

public class Mapper {
    public void mapRequest(final HelloContext context) {
        String request = context.getRequest();
        final int blankPosition = request.indexOf(' ');
        if (blankPosition > 1) {
            context.name = request.substring(0, blankPosition);
        } else {
            context.name = request;
        }
    }

    public void mapResponse(final HelloContext context) {
        context.response = "Hello " + context.name + "! Please transfer " + (long) (context.price * 100L) + " cent";
    }
}
