package de.wolfsvl.copper2go.workflow;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessRulesTest {

    @Test
    void calculatePriceTest() {
        assertThat(BusinessRules.calculatePrice(1000L, 2000L)).isEqualTo(0.12);
    }

}