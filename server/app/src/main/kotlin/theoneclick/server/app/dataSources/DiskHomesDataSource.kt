package theoneclick.server.app.dataSources

import io.ktor.util.logging.Logger
import kotlinx.serialization.json.Json
import theoneclick.server.app.dataSources.base.HomesDataSource
import theoneclick.server.app.models.dtos.HomesEntryDto
import theoneclick.server.app.security.Encryptor
import theoneclick.shared.contracts.core.dtos.NonNegativeIntDto
import theoneclick.shared.contracts.core.dtos.PaginationResultDto
import theoneclick.shared.contracts.core.dtos.PositiveIntDto
import theoneclick.shared.contracts.core.dtos.UuidDto
import java.io.File
import kotlin.collections.forEach

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
                val homesEntry = Json.Default.decodeFromString<HomesEntryDto>(homesEntryString)
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