package theoneclick.shared.contracts.core.models

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class Uuid(val value: String)
