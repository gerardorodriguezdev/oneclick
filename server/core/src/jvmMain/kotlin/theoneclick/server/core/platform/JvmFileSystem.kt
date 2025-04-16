package theoneclick.server.core.platform

import theoneclick.server.core.models.Path
import java.io.File

@Suppress("TooManyFunctions")
class JvmFileSystem : FileSystem {

    override fun readBytes(path: Path): ByteArray = path.toFile().readBytes()

    override fun writeBytes(path: Path, data: ByteArray) = path.toFile().writeBytes(data)

    override fun readText(path: Path): String = path.toFile().readText()

    override fun delete(path: Path): Boolean = path.toFile().delete()

    override fun exists(path: Path): Boolean = path.toFile().exists()

    override fun absolutePath(directory: Path, fileName: String): Path =
        Path(File(directory.value, fileName).absolutePath)

    override fun isFile(path: Path): Boolean = path.toFile().isFile

    override fun paths(directory: Path, filter: (String) -> Boolean): List<Path> =
        File(directory.value)
            .listFiles { file -> filter(file.name) }
            .map { file -> Path(file.absolutePath) }

    override fun createNewDirectory(path: Path): Boolean = path.toFile().mkdirs()

    override fun createNewFile(path: Path): Boolean = path.toFile().createNewFile()

    override fun createNewFile(directory: Path, fileName: String): Boolean =
        File(directory.value, fileName).createNewFile()

    private fun Path.toFile(): File = File(value)
}

actual fun fileSystem(): FileSystem = JvmFileSystem()
