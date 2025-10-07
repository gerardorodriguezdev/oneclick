package oneclick.shared.contracts.core.models

enum class ClientType(val value: String) {
    BROWSER("browser"),
    MOBILE("mobile");

    companion object Companion {
        fun String?.toClientType(): ClientType =
            when (this) {
                BROWSER.value -> BROWSER
                MOBILE.value -> MOBILE
                else -> BROWSER
            }
    }
}