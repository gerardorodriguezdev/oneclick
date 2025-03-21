package theoneclick.server.shared.validators

import theoneclick.server.shared.validators.base.StringsValidator

val roomNameValidator = StringsValidator(
    acceptLetters = true,
    acceptLowercase = true,
    acceptUppercase = true,
    minLength = 3,
    maxLength = 20,
)
