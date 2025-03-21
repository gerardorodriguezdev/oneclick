package theoneclick.server.core.platform

import theoneclick.server.core.models.Path

class PathProvider(
    private val directory: Path,
    private val fileSystem: FileSystem,
) {

    fun path(fileName: String): Path {
        if (!fileSystem.exists(directory)) {
            fileSystem.createNewDirectory(directory)
        }

        val filePath = fileSystem.absolutePath(directory, fileName)

        if (!fileSystem.exists(filePath)) {
            fileSystem.createNewFile(filePath)
        }

        return filePath
    }

    companion object {
        const val DIRECTORY_NAME = "local"
    }
}
