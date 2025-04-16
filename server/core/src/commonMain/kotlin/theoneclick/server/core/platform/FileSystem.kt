package theoneclick.server.core.platform

import theoneclick.server.core.models.Path

interface FileSystem {
    fun readBytes(path: Path): ByteArray
    fun writeBytes(path: Path, data: ByteArray)

    fun readText(path: Path): String

    fun exists(path: Path): Boolean
    fun absolutePath(directory: Path, fileName: String): Path
    fun isFile(path: Path): Boolean
    fun paths(directory: Path, filter: (fileName: String) -> Boolean): List<Path>

    fun createNewDirectory(path: Path): Boolean
    fun createNewFile(path: Path): Boolean
    fun createNewFile(directory: Path, fileName: String): Boolean
    fun delete(path: Path): Boolean
}

expect fun fileSystem(): FileSystem
