package de.wolfsvl.copper2go.workflow;

import de.wolfsvl.copper2go.impl.ContextStoreImpl;
import de.wolfsvl.copper2go.workflowapi.Context;
import de.wolfsvl.copper2go.workflowapi.ContextStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BusinessRulesTest {

    private BusinessRules businessRules;

    @BeforeEach
    public void beforeEach() {
        this.businessRules = new BusinessRules();
    }

    @Test
    public void calculatePriceTest() {
        final ContextStore contextStore = new ContextStoreImpl();
        Context context = new Context("Wolf S");
        contextStore.store("uuid", context);
        var context2 = new HelloContext("uuid", contextStore);
        businessRules.calculatePrice(context2,1000L, 2000L);
        assertThat(context2.price).isEqualTo(0.12);
    }

}