package oneclick.shared.contracts.core.models

import kotlinx.serialization.Serializable

@Serializable
data class PaginationResult<T>(
    val value: T,
    val pageIndex: NonNegativeInt,
    val totalPages: NonNegativeInt,
)
