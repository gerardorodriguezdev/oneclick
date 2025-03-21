package theoneclick.shared.core.validators

import theoneclick.shared.core.validators.base.StringsValidator

val deviceNameValidator = StringsValidator(
    acceptLetters = true,
    acceptLowercase = true,
    acceptUppercase = true,
    acceptDigits = true,
    minLength = 3,
    maxLength = 20,
)
