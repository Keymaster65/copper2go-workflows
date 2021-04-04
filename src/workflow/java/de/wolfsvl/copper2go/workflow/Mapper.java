package de.wolfsvl.copper2go.workflow;

public class Mapper {
    private Mapper() {
    }

    public static String mapRequest(final String request) {
        final int blankPosition = request.indexOf(' ');
        if (blankPosition > 1) {
            return request.substring(0, blankPosition);
        }
        return request;
    }


    public static String mapPricingRequest(final String name) {
        return name;
    }

    public static String mapResponse(final String name, final double price) {
        return "Hello " + name + "! Please transfer " + (long) (price * 100L) + " cent";
    }
}
