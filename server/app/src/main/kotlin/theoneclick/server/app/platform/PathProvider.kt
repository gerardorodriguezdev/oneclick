package theoneclick.server.app.platform

import theoneclick.server.app.models.Path

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

    fun paths(filter: (fileName: String) -> Boolean): List<Path> =
        fileSystem.paths(directory = directory, filter = filter)

    companion object {
        const val DIRECTORY_NAME = "local"
    }
}
