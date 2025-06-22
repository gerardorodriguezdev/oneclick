package theoneclick.client.features.home.models

data class Homes(
    val lastModified: Long,
    val value: List<Home>,
    val pageIndex: Int,
    val canRequestMore: Boolean,
)