package theoneclick.shared.core.models.agents

enum class Agent(val value: String) {
    BROWSER("browser"),
    MOBILE("mobile");

    companion object {
        fun String?.toAgent(): Agent =
            when (this) {
                BROWSER.value -> BROWSER
                MOBILE.value -> MOBILE
                else -> BROWSER
            }
    }
}
