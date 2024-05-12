import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun Project.brooklynTaskOrdering() {
    afterEvaluate {
        val kotlinCompileTasks = tasks.filter {
            it is JavaCompile || it is KotlinCompile
        }
        val cmakeTasks = tasks.filter {
            it is com.android.build.gradle.tasks.ExternalNativeBuildJsonTask ||
                    it is com.android.build.gradle.tasks.ExternalNativeBuildTask
        }

        cmakeTasks.forEach { cmakeTask ->
            kotlinCompileTasks.forEach { kotlinTask ->
                cmakeTask.mustRunAfter(kotlinTask)
            }
        }
    }
}