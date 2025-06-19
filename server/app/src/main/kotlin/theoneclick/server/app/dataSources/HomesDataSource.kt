package theoneclick.server.app.dataSources

import io.ktor.util.logging.*
import kotlinx.serialization.json.Json
import theoneclick.server.app.security.Encryptor
import theoneclick.shared.contracts.core.dtos.*
import java.io.File

abstract class HomesDataSource {
    abstract fun homesEntry(
        userId: UuidDto,
        pageSize: PositiveIntDto,
        currentPageIndex: NonNegativeIntDto
    ): PaginationResultDto<HomesEntryDto>?

    protected fun paginateHomesEntry(
        homesEntry: HomesEntryDto,
        pageSize: PositiveIntDto,
        currentPageIndex: NonNegativeIntDto
    ): PaginationResultDto<HomesEntryDto>? {
        val firstPageIndex = currentPageIndex.value + 1
        val lastPageIndex = firstPageIndex + pageSize.value

        var newPageIndex = 0
        val newHomes = buildList {
            for (index in firstPageIndex until lastPageIndex) {
                val home = homesEntry.homes.getOrNull(index)
                if (home != null) {
                    newPageIndex = index
                    add(home)
                } else {
                    break
                }
            }
        }
        if (newHomes.isEmpty()) return null

        val newHomesEntry = homesEntry.copy(homes = newHomes)

        return PaginationResultDto(
            value = newHomesEntry,
            pageIndex = NonNegativeIntDto.unsafe(newPageIndex),
            totalPages = NonNegativeIntDto.unsafe(homesEntry.homes.size),
        )
    }
}

class MemoryHomesDataSource : HomesDataSource() {
    private val homesEntries = linkedMapOf<UuidDto, HomesEntryDto>()

    override fun homesEntry(
        userId: UuidDto,
        pageSize: PositiveIntDto,
        currentPageIndex: NonNegativeIntDto
    ): PaginationResultDto<HomesEntryDto>? {
        val homesEntry = homesEntries[userId] ?: return null

        return paginateHomesEntry(
            homesEntry = homesEntry,
            pageSize = pageSize,
            currentPageIndex = currentPageIndex
        )
    }

    private companion object {
        const val CLEAN_UP_LIMIT = 10_000
    }
}

class DiskHomesDataSource(
    private val homesEntriesDirectory: File,
    private val encryptor: Encryptor,
    private val logger: Logger,
) : HomesDataSource() {

    override fun homesEntry(
        userId: UuidDto,
        pageSize: PositiveIntDto,
        currentPageIndex: NonNegativeIntDto
    ): PaginationResultDto<HomesEntryDto>? {
        val homesEntry = findHomesEntry { homes -> homes.userId == userId } ?: return null
        return paginateHomesEntry(
            homesEntry = homesEntry,
            pageSize = pageSize,
            currentPageIndex = currentPageIndex
        )
    }

    private fun findHomesEntry(predicate: (homesEntry: HomesEntryDto) -> Boolean): HomesEntryDto? =
        try {
            val homesEntriesFiles = homesEntriesFiles()
            homesEntriesFiles.forEach { homesEntryFile ->
                val encryptedHomesEntryBytes = homesEntryFile.readBytes()
                val homesEntryString = encryptor.decrypt(input = encryptedHomesEntryBytes).getOrThrow()
                val homesEntry = Json.decodeFromString<HomesEntryDto>(homesEntryString)
                if (predicate(homesEntry)) return homesEntry
            }
            null
        } catch (e: Exception) {
            logger.error("Error trying to find homes entry", e)
            null
        }

    private fun homesEntryFile(userId: UuidDto): File = File(homesEntriesDirectory, homesEntryFileName(userId))

    private fun homesEntriesFiles(): Array<File> =
        homesEntriesDirectory.listFiles { file ->
            file.name.endsWith(HOMES_ENTRY_FILE_NAME_SUFFIX)
        }

    companion object {
        private const val HOMES_ENTRIES_DIRECTORY_NAME = "homes"
        private const val HOMES_ENTRY_FILE_NAME_SUFFIX = "homes.txt"
        private fun homesEntryFileName(userId: UuidDto): String = "${userId.value}.$HOMES_ENTRY_FILE_NAME_SUFFIX"
        fun homesEntriesDirectory(storageDirectory: String): File =
            File(storageDirectory, HOMES_ENTRIES_DIRECTORY_NAME).apply {
                if (!exists()) {
                    mkdirs()
                }
            }
    }
}
