package theoneclick.shared.core.validators

import theoneclick.shared.core.validators.base.StringsValidator

val encryptedTokenValidator = StringsValidator(
    acceptDigits = true,
    acceptLetters = true,
    acceptLowercase = true,
    acceptUppercase = true,
    minLength = 50,
    maxLength = 150,
    acceptedSymbols = listOf('+', '=', '/'),
)
