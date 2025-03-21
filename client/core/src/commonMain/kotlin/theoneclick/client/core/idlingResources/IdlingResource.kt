package theoneclick.client.core.idlingResources

interface IdlingResource {
    fun increment()
    fun decrement()
}

class EmptyIdlingResource : IdlingResource {
    @Suppress("EmptyFunctionBlock")
    override fun increment() {}

    @Suppress("EmptyFunctionBlock")
    override fun decrement() {}
}
