plugins {
    id("org.javastro.build.module.quarkus-app")
}

dependencies {
    implementation(project(":common"))
    implementation("io.quarkus:quarkus-picocli")
    implementation("io.quarkus:quarkus-rest-client-jackson")
    implementation("io.quarkus:quarkus-oidc-client")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
}
