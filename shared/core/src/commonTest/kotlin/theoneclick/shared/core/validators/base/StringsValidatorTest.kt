package theoneclick.shared.core.validators.base

import theoneclick.shared.core.validators.base.StringsValidator
import theoneclick.shared.testing.extensions.generateLongString
import theoneclick.shared.testing.extensions.parameterizedTest
import theoneclick.shared.testing.models.testScenario
import kotlin.test.Test

class StringsValidatorTest {

    @Test
    fun `GIVEN stringValidator with acceptDigits WHEN isValid called THEN returns expected`() {
        parameterizedTest(
            testScenario("a", false),
            testScenario("1", true),
            block = { index, input ->
                StringsValidator(acceptDigits = true).isValid(input)
            }
        )
    }

    @Test
    fun `GIVEN stringValidator with acceptLetters WHEN isValid called THEN returns expected`() {
        parameterizedTest(
            testScenario("1", false),
            testScenario("a", true),
            block = { index, input ->
                StringsValidator(acceptLowercase = true, acceptLetters = true).isValid(input)
            }
        )
    }

    @Test
    fun `GIVEN stringValidator with acceptUppercase WHEN isValid called THEN returns expected`() {
        parameterizedTest(
            testScenario("a", false),
            testScenario("A", true),
            block = { index, input ->
                StringsValidator(acceptLetters = true, acceptUppercase = true).isValid(input)
            }
        )
    }

    @Test
    fun `GIVEN stringValidator with acceptLowercase WHEN isValid called THEN returns expected`() {
        parameterizedTest(
            testScenario("A", false),
            testScenario("a", true),
            block = { index, input ->
                StringsValidator(acceptLetters = true, acceptLowercase = true).isValid(input)
            }
        )
    }

    @Test
    fun `GIVEN stringValidator with minLength WHEN isValid called THEN returns expected`() {
        parameterizedTest(
            testScenario(generateLongString(9), false),
            testScenario(generateLongString(10), true),
            block = { index, input ->
                StringsValidator(
                    acceptLetters = true,
                    acceptLowercase = true,
                    minLength = 10
                ).isValid(input)
            }
        )
    }

    @Test
    fun `GIVEN stringValidator with maxLength WHEN isValid called THEN returns expected`() {
        parameterizedTest(
            testScenario(generateLongString(11), false),
            testScenario(generateLongString(10), true),
            block = { index, input ->
                StringsValidator(
                    acceptLetters = true,
                    acceptLowercase = true,
                    maxLength = 10
                ).isValid(input)
            }
        )
    }

    @Test
    fun `GIVEN stringValidator with acceptedSymbols WHEN isValid called THEN returns expected`() {
        parameterizedTest(
            testScenario("a", false),
            testScenario("!", true),
            block = { index, input ->
                StringsValidator(acceptedSymbols = listOf('!')).isValid(input)
            }
        )
    }
}
