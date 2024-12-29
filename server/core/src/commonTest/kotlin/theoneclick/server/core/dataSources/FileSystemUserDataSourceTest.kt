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
    fun `GIVEN userData is not available WHEN userData requested THEN null is returned`() {
        val actualUserData = userDataRepository.userData()

        assertNull(actualUserData)
    }

    @Test
    fun `GIVEN userData available WHEN userData requested THEN userData is returned`() {
        fileSystem.writeBytes(
            pathProvider.path(UserDataSource.FILE_NAME),
            TestData.userDataByteArray
        )

        val actualUserData = userDataRepository.userData()

        assertEquals(expected = TestData.userData, actual = actualUserData)
    }

    @Test
    fun `GIVEN validUserData WHEN saveUserData THEN userData is saved`() {
        userDataRepository.saveUserData(TestData.userData)

        val userDataOnFile = fileSystem.readText(pathProvider.path(UserDataSource.FILE_NAME))
        assertEquals(expected = TestData.userDataString, actual = userDataOnFile)
    }

    @Test
    fun `GIVEN userData is saved WHEN removeUserData THEN userData is deleted`() {
        userDataRepository.saveUserData(TestData.userData)

        userDataRepository.removeUserData()

        pathProvider.path(UserDataSource.FILE_NAME)

        val userDataOnFile = fileSystem.readText(pathProvider.path(UserDataSource.FILE_NAME))
        assertEquals(expected = "", actual = userDataOnFile)
    }
}
