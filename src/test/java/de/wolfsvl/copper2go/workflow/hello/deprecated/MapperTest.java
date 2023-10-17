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
package de.wolfsvl.copper2go.workflow.hello.deprecated;

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