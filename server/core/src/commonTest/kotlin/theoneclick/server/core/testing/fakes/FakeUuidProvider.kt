@file:Suppress("NoVarsInConstructor")

package theoneclick.server.core.testing.fakes

import theoneclick.server.core.platform.UuidProvider
import theoneclick.server.core.testing.TestData
import theoneclick.shared.core.dataSources.models.entities.Uuid

class FakeUuidProvider(
    var fakeUuid: String = TestData.UUID
) : UuidProvider {
    override fun uuid(): Uuid =
        Uuid(fakeUuid)
}
