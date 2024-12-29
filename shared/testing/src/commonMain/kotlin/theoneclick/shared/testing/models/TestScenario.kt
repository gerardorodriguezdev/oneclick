package theoneclick.shared.testing.models

data class TestScenario<Input, Expected>(
    val input: Input,
    val expected: Expected,
)

fun <Input, Expected> testScenario(input: Input, expected: Expected): TestScenario<Input, Expected> =
    TestScenario(input, expected)
