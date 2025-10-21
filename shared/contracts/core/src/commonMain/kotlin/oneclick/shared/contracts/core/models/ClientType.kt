package oneclick.shared.contracts.core.models

enum class ClientType(val value: String) {
    BROWSER("browser"),
    MOBILE("mobile"),
    DESKTOP("desktop");

    companion object Companion {
        fun String?.toClientType(): ClientType =
            when (this) {
                BROWSER.value -> BROWSER
                MOBILE.value -> MOBILE
                DESKTOP.value -> DESKTOP
                else -> BROWSER
            }
    }
}