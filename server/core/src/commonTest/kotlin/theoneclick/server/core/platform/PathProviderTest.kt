package theoneclick.server.core.platform

import theoneclick.server.core.testing.base.IntegrationTest
import kotlin.test.Test
import kotlin.test.assertTrue

class PathProviderTest : IntegrationTest() {
    private val pathProvider = PathProvider(tempDirectory, fileSystem)
    private val fileName = "test.txt"

    @Test
    fun `GIVEN valid path WHEN path requested THEN returns path`() {
        val path = pathProvider.path(fileName)

        assertTrue(fileSystem.exists(path))
        assertTrue(fileSystem.isFile(path))
    }

    @Test
    fun `GIVEN existing directory WHEN path requested THEN returns path`() {
        fileSystem.createNewDirectory(tempDirectory)

        val path = pathProvider.path(fileName)

        assertTrue(fileSystem.exists(path))
        assertTrue(fileSystem.isFile(path))
    }

    @Test
    fun `GIVEN existing file WHEN path requested THEN returns path`() {
        fileSystem.createNewDirectory(tempDirectory)
        fileSystem.createNewFile(tempDirectory, fileName)

        val path = pathProvider.path(fileName)

        assertTrue(fileSystem.exists(path))
        assertTrue(fileSystem.isFile(path))
    }
}
