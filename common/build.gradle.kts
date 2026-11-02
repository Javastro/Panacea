plugins {
    id("org.javastro.build.module.quarkus-lib")
}

dependencies {
    api("net.ivoa.dm:ExecutionDM:0.1-SNAPSHOT")
    api("org.javastro:jaxbjpa-utils:0.2.3")
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-rest-client-jackson")
}
