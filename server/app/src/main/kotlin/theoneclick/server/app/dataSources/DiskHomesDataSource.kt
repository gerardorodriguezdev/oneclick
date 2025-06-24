package theoneclick.server.app.dataSources

import io.ktor.util.logging.Logger
import kotlinx.serialization.json.Json
import theoneclick.server.app.dataSources.base.HomesDataSource
import theoneclick.server.shared.models.HomesEntry
import theoneclick.server.shared.security.Encryptor
import theoneclick.shared.contracts.core.models.NonNegativeInt
import theoneclick.shared.contracts.core.models.PaginationResult
import theoneclick.shared.contracts.core.models.PositiveInt
import theoneclick.shared.contracts.core.models.Uuid
import java.io.File
import kotlin.collections.forEach

class DiskHomesDataSource(
    private val homesEntriesDirectory: File,
    private val encryptor: Encryptor,
    private val logger: Logger,
) : HomesDataSource() {

    override fun homesEntry(
        userId: Uuid,
        pageSize: PositiveInt,
        currentPageIndex: NonNegativeInt
    ): PaginationResult<HomesEntry>? {
        val homesEntry = findHomesEntry { homes -> homes.userId == userId } ?: return null
        return paginateHomesEntry(
            homesEntry = homesEntry,
            pageSize = pageSize,
            currentPageIndex = currentPageIndex
        )
    }

    private fun findHomesEntry(predicate: (homesEntry: HomesEntry) -> Boolean): HomesEntry? =
        try {
            val homesEntriesFiles = homesEntriesFiles()
            homesEntriesFiles.forEach { homesEntryFile ->
                val encryptedHomesEntryBytes = homesEntryFile.readBytes()
                val homesEntryString = encryptor.decrypt(input = encryptedHomesEntryBytes).getOrThrow()
                val homesEntry = Json.Default.decodeFromString<HomesEntry>(homesEntryString)
                if (predicate(homesEntry)) return homesEntry
            }
            null
        } catch (e: Exception) {
            logger.error("Error trying to find homes entry", e)
            null
        }

    private fun homesEntryFile(userId: Uuid): File = File(homesEntriesDirectory, homesEntryFileName(userId))

    private fun homesEntriesFiles(): Array<File> =
        homesEntriesDirectory.listFiles { file ->
            file.name.endsWith(HOMES_ENTRY_FILE_NAME_SUFFIX)
        }

    companion object {
        private const val HOMES_ENTRIES_DIRECTORY_NAME = "homes"
        private const val HOMES_ENTRY_FILE_NAME_SUFFIX = "homes.txt"
        private fun homesEntryFileName(userId: Uuid): String = "${userId.value}.$HOMES_ENTRY_FILE_NAME_SUFFIX"
        fun homesEntriesDirectory(storageDirectory: String): File =
            File(storageDirectory, HOMES_ENTRIES_DIRECTORY_NAME).apply {
                if (!exists()) {
                    mkdirs()
                }
            }
    }
}