package theoneclick.server.shared.validators.base

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@Suppress("ReturnCount", "LongParameterList")
class StringsValidator(
    private val acceptDigits: Boolean = false,
    private val acceptLetters: Boolean = false,
    private val acceptUppercase: Boolean = false,
    private val acceptLowercase: Boolean = false,
    private val minLength: Int = 0,
    private val maxLength: Int = Int.MAX_VALUE,
    acceptedSymbols: List<Char> = listOf(),
) {
    private val acceptedSymbols = buildList {
        if (acceptLetters && acceptLowercase) {
            addAll(VALID_LOWERCASE_LETTERS.toList())
        }

        if (acceptLetters && acceptUppercase) {
            addAll(VALID_UPPERCASE_LETTERS.toList())
        }

        addAll(acceptedSymbols)
    }

    @OptIn(ExperimentalContracts::class)
    fun isValid(value: String?): Boolean {
        contract { returns(true) implies (value != null) }

        if (value == null) return false

        val isValidLength = value.length in IntRange(start = minLength, endInclusive = maxLength)
        if (!isValidLength) return false

        value.onEach { char ->
            if (!acceptDigits && char.isDigit()) return false
            if (!acceptLetters && char.isLetter()) return false
            if (!acceptLowercase && char.isLowerCase()) return false
            if (!acceptUppercase && char.isUpperCase()) return false
            if (!char.isDigit() && char !in acceptedSymbols) return false
        }

        return true
    }

    @OptIn(ExperimentalContracts::class)
    fun isNotValid(value: String?): Boolean {
        contract { returns(false) implies (value != null) }

        return !isValid(value)
    }

    companion object {
        const val VALID_LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz"
        const val VALID_UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    }
}
