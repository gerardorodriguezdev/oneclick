package theoneclick.shared.core.validators

import theoneclick.shared.core.validators.base.StringsValidator

val usernameValidator = StringsValidator(
    acceptLetters = true,
    acceptLowercase = true,
    acceptUppercase = true,
    minLength = 8,
    maxLength = 25,
    acceptedSymbols = listOf('!'),
)
