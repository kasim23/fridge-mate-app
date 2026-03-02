plugins {
    id("java")
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.fridgemate"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    // Web — embeds Tomcat, enables REST controllers
    implementation("org.springframework.boot:spring-boot-starter-web")

    // JPA + Hibernate — ORM layer to talk to PostgreSQL
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Security — authentication/authorization framework
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Mail — send emails via SMTP (Gmail)
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // Validation — @NotBlank, @Email, @Size on DTOs
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // PostgreSQL JDBC driver
    runtimeOnly("org.postgresql:postgresql")

    // JWT — create and verify JSON Web Tokens
    implementation("io.jsonwebtoken:jjwt-api:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")

    // Lombok — reduces boilerplate (@Getter, @Builder, etc.)
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.test {
    useJUnitPlatform()
}
