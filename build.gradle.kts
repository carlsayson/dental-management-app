buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Uncomment when Firebase is configured
        // classpath("com.google.gms:google-services:4.4.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
