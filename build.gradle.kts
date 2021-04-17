plugins {
    java
}

sourceSets {
    main {
        java.srcDir("src/workflow/java")
    }
}
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.keymaster65:copper2go-api:2+")
    implementation("org.copper-engine:copper-coreengine:5+")

    testImplementation("org.assertj:assertj-assertions-generator:2+")
    testImplementation("org.junit.jupiter:junit-jupiter:5+")
    testImplementation("org.mockito:mockito-core:3+")
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines.add("junit-jupiter")
    }
}