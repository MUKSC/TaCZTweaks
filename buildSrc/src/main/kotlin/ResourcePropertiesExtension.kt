import org.gradle.api.provider.MapProperty

interface ResourcePropertiesExtension {
    val properties: MapProperty<String, String>
}