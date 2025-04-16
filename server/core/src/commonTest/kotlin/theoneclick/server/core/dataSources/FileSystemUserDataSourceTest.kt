package theoneclick.server.core.dataSources

import theoneclick.server.core.platform.PathProvider
import theoneclick.server.core.testing.TestData
import theoneclick.server.core.testing.base.IntegrationTest
import theoneclick.server.core.testing.fakes.FakeSecurityUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FileSystemUserDataSourceTest : IntegrationTest() {
    private val pathProvider = PathProvider(tempDirectory, fileSystem)
    private val userDataRepository =
        FileSystemUserDataSource(pathProvider, FakeSecurityUtils(), fileSystem)

    @Test
    fun `GIVEN user is not available WHEN user requested THEN null is returned`() {
        val actualUser = userDataRepository.user()

        assertNull(actualUser)
    }

    @Test
    fun `GIVEN user available WHEN user requested THEN user is returned`() {
        fileSystem.writeBytes(
            pathProvider.path(UserDataSource.FILE_NAME),
            TestData.userByteArray
        )

        val actualUser = userDataRepository.user()

        assertEquals(expected = TestData.user, actual = actualUser)
    }

    @Test
    fun `GIVEN validUser WHEN saveUser THEN user is saved`() {
        userDataRepository.saveUser(TestData.user)

        val userOnFile = fileSystem.readText(pathProvider.path(UserDataSource.FILE_NAME))
        assertEquals(expected = TestData.userString, actual = userOnFile)
    }

    @Test
    fun `GIVEN user is saved WHEN removeUser THEN user is deleted`() {
        userDataRepository.saveUser(TestData.user)

        userDataRepository.removeUser()

        pathProvider.path(UserDataSource.FILE_NAME)

        val userOnFile = fileSystem.readText(pathProvider.path(UserDataSource.FILE_NAME))
        assertEquals(expected = "", actual = userOnFile)
    }
}
