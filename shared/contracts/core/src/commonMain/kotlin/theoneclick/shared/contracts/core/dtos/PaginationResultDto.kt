package theoneclick.shared.contracts.core.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PaginationResultDto<T>(
    val lastModified: PositiveLongDto,
    val value: T,
    val pageIndex: NonNegativeIntDto,
    val totalPages: NonNegativeIntDto,
)