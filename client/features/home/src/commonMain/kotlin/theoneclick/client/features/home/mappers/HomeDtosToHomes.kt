package theoneclick.client.features.home.mappers

import theoneclick.client.features.home.models.Home
import theoneclick.client.features.home.models.Home.Companion.toHome
import theoneclick.shared.contracts.core.models.Home

internal fun List<theoneclick.shared.contracts.core.models.Home>.toHomes(): List<Home> = map { it.toHome() }