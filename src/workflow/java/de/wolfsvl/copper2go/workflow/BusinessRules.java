package de.wolfsvl.copper2go.workflow;

public class BusinessRules {
    private BusinessRules() {}

    static double calculatePrice(final long startMillis, final long now, final int pricePerMinute) {
        long durarionMillis = now - startMillis;
        return pricePerMinute * ((double) durarionMillis / (60L * 1000L));
    }
}
