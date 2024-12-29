package theoneclick.shared.testing.extensions

import theoneclick.shared.testing.models.TestScenario
import kotlin.test.assertContains
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun generateLongString(length: Int): String =
    buildString {
        repeat(length) {
            append('a')
        }
    }

fun <Input : Any?, Expected : Any?> parameterizedTest(
    vararg testScenario: TestScenario<Input, Expected>,
    block: (index: Int, input: Input) -> Expected
) {
    listOf(*testScenario).forEachIndexed { index, (input: Input, expected: Expected) ->
        val actual = block(index, input)
        assertEquals(expected, actual, "Index: $index | Input: $input | Actual: $actual | Expected: $expected")
    }
}

fun <Input : Any?> runOnlyParameterizedTest(
    vararg input: Input,
    block: (index: Int, input: Input) -> Unit
) {
    listOf(*input).forEachIndexed { index, input: Input ->
        block(index, input)
    }
}

fun <T> List<T>.assertContains(value: T) {
    assertContains(this, value)
}

fun <T> List<T>.assertContentsEquals(value: List<T>) {
    assertContentEquals(this, value)
}

fun <T> List<T>.assertIsEmpty() {
    assertTrue(message = "List was not empty") { isEmpty() }
}
