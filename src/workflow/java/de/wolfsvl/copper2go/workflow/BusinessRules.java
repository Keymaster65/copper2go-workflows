package de.wolfsvl.copper2go.workflow;

public class BusinessRules {
    void calculatePrice(final HelloContext context, final long startMillis, final long now) {
        double pricePerSecond = 0.12;
        long durarionMillis = now - startMillis;
        context.price = pricePerSecond * ((double) durarionMillis / 1000L);
    }
}
