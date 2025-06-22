package theoneclick.client.features.home.models

data class HomesEntry(
    val lastModified: Long,
    val homes: List<Home>,
    val pageIndex: Int,
    val canRequestMore: Boolean,
)