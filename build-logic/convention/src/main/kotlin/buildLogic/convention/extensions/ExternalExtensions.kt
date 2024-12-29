package buildLogic.convention.extensions

import org.gradle.api.Project
import org.gradle.api.provider.Provider

@Suppress("UNCHECKED_CAST")
fun <T> Project.propProvider(name: String): Provider<T> = provider { findProperty(name) as T }

@Suppress("UNCHECKED_CAST")
fun <T> Project.prop(name: String): T = findProperty(name) as T

fun Project.isPropEnabled(propertyName: String): Boolean = findProperty(propertyName) == "true"
