package de.wolfsvl.copper2go.workflow.hello.v4.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperFactory {

    private static final ObjectMapper DEFAULT_OBJECT_MAPPER
            = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    static ObjectMapper getDefault() {
        return DEFAULT_OBJECT_MAPPER;
    }

    private ObjectMapperFactory() {
    }
}
