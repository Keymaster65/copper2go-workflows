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
package de.wolfsvl.copper2go.workflow;

public class Mapper {
    private Mapper() {
    }

    public static String mapRequest(final String request) {
        if (null == request || "".equals(request)) {
            throw new IllegalArgumentException("A name must be specified.");
        }

        final int blankPosition = request.indexOf(' ');
        if (blankPosition > 1) {
            return request.substring(0, blankPosition);
        }
        return request;
    }


    public static String mapPricingRequest(final String name) {
        return name;
    }

    public static String mapResponse(final String name, final double price) {
        return "Hello " + name + "! Please transfer " + (long) (price) + " cent";
    }
}
