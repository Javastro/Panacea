plugins {
    id("org.javastro.build.module.quarkus-app")
}

dependencies {
    implementation(project(":common"))
    implementation("io.quarkus:quarkus-rest-client-jackson")
    implementation("io.quarkus:quarkus-oidc")
    implementation("io.quarkus:quarkus-scheduler")
}
