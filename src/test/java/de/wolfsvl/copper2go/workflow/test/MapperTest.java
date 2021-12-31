/*
 * Copyright 2021 Wolf Sluyterman van Langeweyde
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

        Assertions.assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result.getProperty("a")).isEqualTo("A");
        Assertions.assertThat(result.getProperty(Mapper.REPLY_UUID)).isEqualTo("uuid");
    }

    @Test
    void mapSystemTestNull() {
        Properties input = new Properties();
        input.setProperty("a", "A");

        Properties result = Mapper.mapSystemTest(input, null);

        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.getProperty("a")).isEqualTo("A");
        Assertions.assertThat(result.getProperty(Mapper.REPLY_UUID)).isNull();
    }
}