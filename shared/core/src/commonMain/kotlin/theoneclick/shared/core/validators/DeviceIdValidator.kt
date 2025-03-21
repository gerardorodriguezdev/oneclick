package theoneclick.shared.core.validators

import theoneclick.shared.core.validators.base.StringsValidator

val deviceIdValidator = StringsValidator(
    acceptLetters = true,
    acceptLowercase = true,
    acceptDigits = true,
    minLength = 36,
    maxLength = 36,
    acceptedSymbols = listOf('-'),
)
