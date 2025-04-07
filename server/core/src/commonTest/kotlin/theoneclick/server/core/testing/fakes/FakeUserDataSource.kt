package theoneclick.server.core.testing.fakes

import theoneclick.server.core.dataSources.UserDataSource
import theoneclick.server.core.models.UserData
import theoneclick.server.core.testing.TestData

class FakeUserDataSource(
    var fakeUserData: UserData? = TestData.userData,
    var removeUserDataEvents: Int = 0,
) : UserDataSource {
    val saveUserDataEvents = mutableListOf<UserData>()

    override fun userData(): UserData? = fakeUserData

    override fun saveUserData(userData: UserData) {
        saveUserDataEvents.add(userData)
    }

    override fun removeUserData() {
        removeUserDataEvents += 1
    }
}
