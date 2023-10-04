pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
        maven {
            url = uri("http://repo1.maven.org/maven2")
            isAllowInsecureProtocol = true
        }
    }
}
rootProject.name = "soap-web-client-helper-kotlin"
