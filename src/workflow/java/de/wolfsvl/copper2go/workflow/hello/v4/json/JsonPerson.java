package de.wolfsvl.copper2go.workflow.hello.v4.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class JsonPerson implements Serializable {
    @JsonProperty
    String name;
}
