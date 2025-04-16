package theoneclick.server.core.testing.fakes

import theoneclick.server.core.dataSources.UsersDataSource
import theoneclick.server.core.models.User
import theoneclick.server.core.models.Username
import theoneclick.server.core.testing.TestData

class FakeUsersDataSource(
    var fakeUser: User? = TestData.user,
) : UsersDataSource {
    val saveUserEvents = mutableListOf<User>()

    override fun user(sessionToken: String): User? = fakeUser

    override fun user(username: Username): User? = fakeUser

    override fun saveUser(user: User) {
        saveUserEvents.add(user)
    }
}
