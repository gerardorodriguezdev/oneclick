package theoneclick.server.shared.validators

import theoneclick.server.shared.validators.base.StringsValidator

val deviceIdValidator = StringsValidator(
    acceptLetters = true,
    acceptLowercase = true,
    acceptDigits = true,
    minLength = 36,
    maxLength = 36,
    acceptedSymbols = listOf('-'),
)
