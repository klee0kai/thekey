import com.android.build.gradle.internal.dsl.DynamicFeatureExtension
import org.gradle.api.Project


fun Project.pushDynamicFeature(android: DynamicFeatureExtension) {
    tasks.register("pushFeatureDebug") {
        dependsOn(tasks.getByName("assembleDebug"))
        notCompatibleWithConfigurationCache("todo")
        val adb = android.adbExecutable
        val apkFile = layout.buildDirectory.file("outputs/apk/debug").get().asFile
        doLast {
            exec { commandLine = listOf(adb.absolutePath, "shell", "rm", "-rf", "/data/local/tmp/tkey_features") }
        }
        doLast {
            exec { commandLine = listOf(adb.absolutePath, "shell", "mkdir", "-p", "/data/local/tmp/tkey_features") }
        }
        doLast {
            exec { commandLine = listOf(adb.absolutePath, "push", apkFile.absolutePath, "/data/local/tmp/tkey_features") }
        }
    }
}
