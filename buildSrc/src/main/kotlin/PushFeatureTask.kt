import com.android.build.gradle.internal.dsl.DynamicFeatureExtension
import org.gradle.api.Project


fun Project.pushDynamicFeature(android: DynamicFeatureExtension) {
    tasks.register("pushFeatureDebug") {
        dependsOn(tasks.getByName("assembleDebug"))
        notCompatibleWithConfigurationCache("todo")
        val adb = android.adbExecutable
        val apkFile = layout.buildDirectory.file("outputs/apk/debug")
            .get()
            .asFile
            .walk()
            .filter { file -> file.name.endsWith(".apk") }
            .firstOrNull()
            ?: return@register

        doLast {
            exec { commandLine = listOf(adb.absolutePath, "shell", "mkdir", "-p", "/data/local/tmp/tkey_features") }
        }
        doLast {
            exec { commandLine = listOf(adb.absolutePath, "shell", "rm", "-f", "/data/local/tmp/tkey_features/${apkFile.name}") }
        }
        doLast {
            exec { commandLine = listOf(adb.absolutePath, "push", apkFile.absolutePath, "/data/local/tmp/tkey_features") }
        }
    }
}

