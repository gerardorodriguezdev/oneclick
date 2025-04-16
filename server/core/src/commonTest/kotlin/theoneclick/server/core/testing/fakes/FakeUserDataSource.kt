package theoneclick.server.core.testing.fakes

import theoneclick.server.core.dataSources.UserDataSource
import theoneclick.server.core.models.User
import theoneclick.server.core.models.Username
import theoneclick.server.core.testing.TestData

class FakeUserDataSource(
    var fakeUser: User? = TestData.user,
) : UserDataSource {
    val saveUserEvents = mutableListOf<User>()

    override fun user(sessionToken: String): User? = fakeUser

    override fun user(username: Username): User? = fakeUser

    override fun saveUser(user: User) {
        saveUserEvents.add(user)
    }
}
