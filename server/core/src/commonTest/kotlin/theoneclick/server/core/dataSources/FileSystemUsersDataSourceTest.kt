package theoneclick.server.core.dataSources

import theoneclick.server.core.platform.PathProvider
import theoneclick.server.core.testing.TestData
import theoneclick.server.core.testing.base.IntegrationTest
import theoneclick.server.core.testing.fakes.FakeSecurityUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FileSystemUsersDataSourceTest : IntegrationTest() {
    private val pathProvider = PathProvider(tempDirectory, fileSystem)
    private val securityUtils = FakeSecurityUtils()
    private val userDataRepository = FileSystemUsersDataSource(pathProvider, securityUtils, fileSystem)

    @Test
    fun `GIVEN user is not available WHEN user requested THEN null is returned`() {
        val actualUser = userDataRepository.user(TestData.username)

        assertNull(actualUser)
    }

    @Test
    fun `GIVEN user available WHEN user requested THEN user is returned`() {
        fileSystem.writeBytes(
            pathProvider.path(FileSystemUsersDataSource.userFileName(TestData.userId)),
            TestData.userByteArray
        )

        val actualUser = userDataRepository.user(TestData.username)

        assertEquals(expected = TestData.user, actual = actualUser)
    }

    @Test
    fun `GIVEN validUser WHEN saveUser THEN user is saved`() {
        userDataRepository.saveUser(TestData.user)

        val userOnFile = fileSystem.readText(pathProvider.path(FileSystemUsersDataSource.userFileName(TestData.userId)))
        assertEquals(expected = TestData.userString, actual = userOnFile)
    }
}
