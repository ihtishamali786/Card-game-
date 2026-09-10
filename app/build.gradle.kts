import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy
import java.security.KeyStore

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
  alias(libs.plugins.google.services)
}

android {
  namespace = "com.solitaire.hyper.card.games"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.solitaire.hyper.card.games"
    minSdk = 24
    targetSdk = 36
    versionCode = 7
    versionName = "7.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    create("release") {
      val releaseKeystore = file("${rootDir}/release-keystore.jks")
      val envKeystorePath = System.getenv("KEYSTORE_PATH")
      val keystoreFile = when {
        releaseKeystore.exists() -> releaseKeystore
        !envKeystorePath.isNullOrBlank() && file(envKeystorePath).exists() -> file(envKeystorePath)
        else -> file("${rootDir}/debug.keystore")
      }
      storeFile = keystoreFile

      val pass = when {
        keystoreFile == releaseKeystore -> "release123"
        keystoreFile.name == "debug.keystore" -> "android"
        else -> System.getenv("STORE_PASSWORD") ?: "android"
      }
      storePassword = pass
      keyPassword = when {
        keystoreFile == releaseKeystore -> "release123"
        else -> System.getenv("KEY_PASSWORD") ?: pass
      }

      val envAlias = System.getenv("KEY_ALIAS")
      if (keystoreFile == releaseKeystore) {
        keyAlias = "release"
      } else if (!envAlias.isNullOrBlank()) {
        keyAlias = envAlias
      } else if (keystoreFile.name == "debug.keystore") {
        keyAlias = "androiddebugkey"
      } else if (keystoreFile.exists()) {
        try {
          val ks = KeyStore.getInstance(KeyStore.getDefaultType())
          keystoreFile.inputStream().use { stream ->
            ks.load(stream, pass.toCharArray())
            val aliases = ks.aliases().toList()
            keyAlias = when {
              aliases.contains("upload") -> "upload"
              aliases.contains("androiddebugkey") -> "androiddebugkey"
              aliases.isNotEmpty() -> aliases.first()
              else -> "androiddebugkey"
            }
          }
        } catch (_: Exception) {
          keyAlias = "androiddebugkey"
        }
      } else {
        keyAlias = "androiddebugkey"
      }
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug { signingConfig = signingConfigs.getByName("debugConfig") }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
  ignoreList.add("FIREBASE_APPCHECK_DEBUG_TOKEN")
}

googleServices { missingGoogleServicesStrategy = MissingGoogleServicesStrategy.WARN }

// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.firebase.analytics)
  implementation(libs.play.services.ads)
  implementation(libs.play.app.update)
  // implementation(libs.coil.compose)
  implementation(libs.converter.moshi)
  implementation(libs.firebase.ai)
  // Uncomment to use Firestore:
  // implementation(libs.firebase.firestore)

  // Uncomment ALL FOUR of the following dependencies together to use Firebase Auth and Google
  // Sign-In via Credential Manager:
  // implementation(libs.firebase.auth)
  // implementation(libs.androidx.credentials)
  // implementation(libs.androidx.credentials.play.services)
  // implementation(libs.googleid)
  implementation(libs.firebase.appcheck.recaptcha)
  implementation(libs.firebase.appcheck.debug)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  // implementation(libs.play.services.location)
  implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}

tasks.register<Zip>("createObb") {
  archiveFileName.set("main.${android.defaultConfig.versionCode}.${android.defaultConfig.applicationId}.obb")
  destinationDirectory.set(layout.buildDirectory.dir("outputs/obb"))
  from("src/main/assets")
}

val rootDirPath = rootDir.absolutePath
tasks.matching { it.name == "signReleaseBundle" || it.name == "packageReleaseBundle" }.configureEach {
  doLast {
    val buildDirFile = layout.buildDirectory.get().asFile
    val candidates = listOf(
      File(buildDirFile, "outputs/bundle/release/app-release.aab"),
      File(buildDirFile, "intermediates/final_app_bundle/release/packageReleaseBundle/base.aab")
    )
    for (src in candidates) {
      if (src.exists() && src.length() > 0) {
        val rootRelease = File(rootDirPath, "release.aab")
        val pubRelease = File("$rootDirPath/public/downloads/release.aab")
        val pubAppRelease = File("$rootDirPath/public/downloads/app-release.aab")
        src.copyTo(rootRelease, overwrite = true)
        src.copyTo(pubRelease, overwrite = true)
        src.copyTo(pubAppRelease, overwrite = true)
        println(">>> Successfully copied ${src.name} (${src.length()} bytes) to release.aab and public/downloads!")
        break
      }
    }
  }
}


