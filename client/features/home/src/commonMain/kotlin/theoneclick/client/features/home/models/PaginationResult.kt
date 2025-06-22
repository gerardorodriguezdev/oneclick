package theoneclick.client.features.home.models

data class PaginationResult<T>(
    val lastModified: Long,
    val value: T,
    val pageIndex: Int,
    val canRequestMore: Boolean,
)