package theoneclick.server.core.testing.fakes

import theoneclick.server.core.dataSources.UserDataSource
import theoneclick.server.core.models.User
import theoneclick.server.core.testing.TestData

class FakeUserDataSource(
    var fakeUser: User? = TestData.user,
    var removeUserDataEvents: Int = 0,
) : UserDataSource {
    val saveUserEvents = mutableListOf<User>()

    override fun user(): User? = fakeUser

    override fun saveUser(user: User) {
        saveUserEvents.add(user)
    }

    override fun removeUser() {
        removeUserDataEvents += 1
    }
}
