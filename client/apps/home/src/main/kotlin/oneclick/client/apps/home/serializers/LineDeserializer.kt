package oneclick.client.apps.home.serializers

import oneclick.client.apps.home.serializers.LineDeserializer.Entry

interface LineDeserializer {
    fun deserialize(data: String): Map<String, Entry>

    data class Entry(
        val key: String,
        val value: String,
    )
}

class DefaultLineDeserializer : LineDeserializer {
    override fun deserialize(data: String): Map<String, Entry> = data.toEntries()

    private fun String.toEntries(): Map<String, Entry> {
        val entriesStrings = split(ENTRY_SEPARATOR)

        val entries = entriesStrings
            .map { entryString ->
                val (key, value) = entryString.split(ENTRY_KEY_VALUE_SEPARATOR)
                Entry(key = key, value = value)
            }

        return entries.associateBy { entry -> entry.key }
    }

    private companion object {
        const val ENTRY_SEPARATOR = ";"
        const val ENTRY_KEY_VALUE_SEPARATOR = "="
    }
}