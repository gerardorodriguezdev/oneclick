package theoneclick.client.features.home.mappers

import theoneclick.client.features.home.models.Home
import theoneclick.client.features.home.models.Home.Companion.toHome
import theoneclick.shared.contracts.core.dtos.HomeDto

internal fun List<HomeDto>.toHomes(): List<Home> = map { it.toHome() }