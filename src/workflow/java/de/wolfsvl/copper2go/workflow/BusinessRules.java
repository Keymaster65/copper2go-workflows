package de.wolfsvl.copper2go.workflow;

public class BusinessRules {
    private BusinessRules() {}

    static double calculatePrice(final long startMillis, final long now) {
        double pricePerSecond = 0.12;
        long durarionMillis = now - startMillis;
        return pricePerSecond * ((double) durarionMillis / 1000L);
    }
}
