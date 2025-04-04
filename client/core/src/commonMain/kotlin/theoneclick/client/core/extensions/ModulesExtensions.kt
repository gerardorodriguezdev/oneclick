package theoneclick.client.core.extensions

import org.koin.core.module.Module
import theoneclick.client.core.di.base.ModuleProvider

fun List<ModuleProvider>.modules(): List<Module> =
    map { moduleProvider -> moduleProvider.module }

fun Module.includes(moduleProvider: ModuleProvider) {
    includes(moduleProvider.module)
}