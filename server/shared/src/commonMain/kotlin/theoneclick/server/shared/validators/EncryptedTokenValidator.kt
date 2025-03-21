package theoneclick.server.shared.validators

import theoneclick.server.shared.validators.base.StringsValidator

val encryptedTokenValidator = StringsValidator(
    acceptDigits = true,
    acceptLetters = true,
    acceptLowercase = true,
    acceptUppercase = true,
    minLength = 50,
    maxLength = 150,
    acceptedSymbols = listOf('+', '=', '/'),
)
