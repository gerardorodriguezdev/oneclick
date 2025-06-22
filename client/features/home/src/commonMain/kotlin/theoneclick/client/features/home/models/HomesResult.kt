package theoneclick.client.features.home.models

sealed interface HomesResult {
    data class Success(val paginationResult: PaginationResult<List<Home>>?) : HomesResult
    data object Error : HomesResult
}