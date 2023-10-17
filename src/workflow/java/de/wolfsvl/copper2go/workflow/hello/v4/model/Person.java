package de.wolfsvl.copper2go.workflow.hello.v4.model;

import java.io.Serializable;
import java.util.Objects;

public record Person(String firstName, String lastName) implements Serializable {
    public Person {
        Objects.requireNonNull(firstName, "Person firstName must not be null.");
    }

    public Person(String firstName){
        this(firstName, null);
    }
}