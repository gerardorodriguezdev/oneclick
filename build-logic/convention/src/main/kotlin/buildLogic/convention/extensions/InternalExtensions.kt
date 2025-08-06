package buildLogic.convention.extensions

import org.gradle.api.JavaVersion
import org.gradle.api.provider.Provider
import org.gradle.jvm.toolchain.JavaLanguageVersion

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
