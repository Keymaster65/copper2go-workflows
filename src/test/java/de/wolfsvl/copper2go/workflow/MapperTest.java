package de.wolfsvl.copper2go.workflow;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MapperTest {

    @Test
    void mapRequestTest() {
        assertThat(Mapper.mapRequest("Wolf S.")).isEqualTo("Wolf");
    }

    @Test
    void mapResponseTest() {
        assertThat(Mapper.mapResponse("W", 0)).isEqualTo("Hello W! Please transfer 0 cent");
    }
}