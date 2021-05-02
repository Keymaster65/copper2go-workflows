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

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

public class Mapper {

    public static final String REPLY_UUID = "replyUUID";

    private Mapper() {
    }

    public static Properties mapRequest(final String request) throws IOException {
        final var properties = new Properties();
        properties.load(new StringReader(request));
        return properties;
    }

    public static Properties mapSystemTest(final Properties payloadProperties, final String initialUuid) {
        var systemTestProperties = new Properties();
        systemTestProperties.putAll(payloadProperties);
        systemTestProperties.put(REPLY_UUID, initialUuid);
        return systemTestProperties;
    }
}
