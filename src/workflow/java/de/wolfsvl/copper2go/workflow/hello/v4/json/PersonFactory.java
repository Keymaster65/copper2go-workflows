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
package de.wolfsvl.copper2go.workflow.hello.v4.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.wolfsvl.copper2go.workflow.AdapterException;
import de.wolfsvl.copper2go.workflow.hello.v4.model.Person;

public class PersonFactory {


    public static Person fromJson(final String personString) {
        try {
            final JsonPerson jsonPerson = ObjectMapperFactory.getDefault().readValue(personString, JsonPerson.class);
            return new Person(jsonPerson.name);
        } catch (JsonProcessingException e) {
            throw new AdapterException("Unable to adapt Person '%s'.".formatted(personString), e);
        }
    }

    private PersonFactory() {
    }
}
