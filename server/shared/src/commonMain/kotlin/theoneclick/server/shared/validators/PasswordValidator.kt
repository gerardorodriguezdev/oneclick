package theoneclick.server.shared.validators

import theoneclick.server.shared.validators.base.StringsValidator

val passwordValidator = StringsValidator(
    acceptLetters = true,
    acceptDigits = true,
    acceptLowercase = true,
    acceptUppercase = true,
    minLength = 10,
    maxLength = 25,
    acceptedSymbols = listOf('!', '-', '_', '+'),
)
