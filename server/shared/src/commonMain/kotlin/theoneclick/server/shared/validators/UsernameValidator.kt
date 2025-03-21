package theoneclick.server.shared.validators

import theoneclick.server.shared.validators.base.StringsValidator

val usernameValidator = StringsValidator(
    acceptLetters = true,
    acceptLowercase = true,
    acceptUppercase = true,
    minLength = 8,
    maxLength = 25,
    acceptedSymbols = listOf('!'),
)
