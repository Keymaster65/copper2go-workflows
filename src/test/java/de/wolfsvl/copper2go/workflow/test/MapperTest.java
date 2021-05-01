package de.wolfsvl.copper2go.workflow.test;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class MapperTest {

    @Test
    void mapRequest() throws IOException {
        Properties result = Mapper.mapRequest("a=A");
        Assertions.assertThat(result.size()).isOne();
        Assertions.assertThat(result.getProperty("a")).isEqualTo("A");
    }

    @Test
    void mapRequestEmpty() throws IOException {
        Properties result = Mapper.mapRequest("");
        Assertions.assertThat(result.size()).isZero();
    }


    @Test
    void mapRequestEmptyProperty() throws IOException {
        Properties result = Mapper.mapRequest("a");
        Assertions.assertThat(result.size()).isOne();
        Assertions.assertThat(result.getProperty("a")).isEmpty();
        Assertions.assertThat(result.getProperty("b")).isNull();
    }

    @Test
    void mapSystemTest() {
        Properties input = new Properties();
        input.setProperty("a", "A");
        Properties result = Mapper.mapSystemTest(input, "uuid");
        Assertions.assertThat(result.getProperty("a")).isEqualTo("A");
        Assertions.assertThat(result.getProperty(Mapper.REPLY_UUID)).isEqualTo("uuid");
    }
}