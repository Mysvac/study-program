plugins {
    id("java")
}

group = "com.mythovac"
version = "1.0-SNAPSHOT"

repositories {
    maven { url = uri("https://maven.aliyun.com/nexus/content/repositories/google") }
    maven { url = uri("https://maven.aliyun.com/nexus/content/groups/public") }
    google()
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}


tasks.test {
    useJUnitPlatform()
}