package buildLogic.convention.extensions

import io.github.gerardorodriguezdev.chamaleon.gradle.plugin.extensions.ChamaleonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.jvm.toolchain.JavaLanguageVersion
import kotlin.collections.component1
import kotlin.collections.component2

internal fun Int.toJavaVersion(): JavaVersion =
    when (this) {
        17 -> JavaVersion.VERSION_17
        21 -> JavaVersion.VERSION_21
        else -> throw IllegalStateException("Version $this not supported")
    }

internal fun Provider<Int>.toJavaVersion(): Provider<JavaVersion> =
    map { jvmApi -> jvmApi.toJavaVersion() }

internal fun Provider<Int>.toJavaLanguageVersion(): Provider<JavaLanguageVersion> =
    map { jvmApi -> JavaLanguageVersion.of(jvmApi) }

internal fun ChamaleonExtension?.toMap(): Map<String, String> =
    if (this == null) {
        emptyMap()
    } else {
        buildMap {
            selectedEnvironmentOrNull()?.jvmPlatformOrNull?.properties?.forEach { (key, value) ->
                value.value?.toString()?.let { valueString ->
                    put(key, valueString)
                }
            }
        }
    }

internal fun Project.fullNameDockerImage(
    imageRegistryUrl: Provider<String>,
    imageName: Provider<String>,
    identifier: Provider<String>,
): Provider<String> =
    provider { "${imageRegistryUrl.get()}/${imageName.get()}:${identifier.get()}" }