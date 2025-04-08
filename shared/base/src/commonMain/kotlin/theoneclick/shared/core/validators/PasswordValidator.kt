package theoneclick.shared.core.validators

import theoneclick.shared.core.validators.base.StringsValidator

val passwordValidator = StringsValidator(
    acceptLetters = true,
    acceptDigits = true,
    acceptLowercase = true,
    acceptUppercase = true,
    minLength = 10,
    maxLength = 25,
    acceptedSymbols = listOf('!', '-', '_', '+'),
)
