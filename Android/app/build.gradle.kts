plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // Apply the Google services plugin
}

android {
    namespace = "com.example.pmg302_project"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pmg302_project"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.google.code.gson:gson:2.8.9")

    implementation(platform("com.google.firebase:firebase-bom:31.0.2"))
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-auth")
    implementation ("org.osmdroid:osmdroid-android:6.1.10")
    implementation ("androidx.cardview:cardview:1.0.0")

}

apply(plugin = "com.google.gms.google-services")