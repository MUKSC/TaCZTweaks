import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException
import org.gradle.kotlin.dsl.getByType

fun Project.prop(name: String): String {
    val stonecutter = try {
        extensions.getByType<StonecutterBuildExtension>()
    } catch (_: UnknownDomainObjectException) {
        null
    }
    val currentProject = stonecutter?.let { rootProject.project(it.current.project) }
    return requireNotNull(findProperty(name) ?: currentProject?.findProperty(name)) {
        "No property named '$name'"
    } as String
}
fun Project.mod(name: String): String = prop("mod.$name")
fun Project.libs(name: String): String = prop("libs.$name")