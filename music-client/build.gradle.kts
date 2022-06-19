plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "me.tropicalfan344"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    jcenter()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
    implementation(project(":music-common"))

    implementation("com.google.code.gson:gson:2.8.8")
    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("com.kohlschutter.junixsocket:junixsocket-core:2.5.0")

}

tasks {
    register<JavaExec>("runClient") {
        dependsOn("classes")
        mainClass.set("Main")
        args = arrayListOf(System.getProperty("host"))
        classpath = files(*ArrayList<File>().also {
            it.addAll(project.extensions.getByType(JavaPluginExtension::class.java).sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).runtimeClasspath)
            it.add(File(project.buildDir, "classes"))
        }.toTypedArray())
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    shadowJar {
        manifest {
            attributes["Main-Class"] = "Main"
        }
    }
}