package theoneclick.shared.core.models.entities

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class Uuid(val value: String)
