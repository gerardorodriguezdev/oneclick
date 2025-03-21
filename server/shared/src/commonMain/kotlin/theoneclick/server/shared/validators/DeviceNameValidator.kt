package theoneclick.server.shared.validators

import theoneclick.server.shared.validators.base.StringsValidator

val deviceNameValidator = StringsValidator(
    acceptLetters = true,
    acceptLowercase = true,
    acceptUppercase = true,
    acceptDigits = true,
    minLength = 3,
    maxLength = 20,
)
