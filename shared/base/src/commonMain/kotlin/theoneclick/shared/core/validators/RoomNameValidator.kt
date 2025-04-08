package theoneclick.shared.core.validators

import theoneclick.shared.core.validators.base.StringsValidator

val roomNameValidator = StringsValidator(
    acceptLetters = true,
    acceptLowercase = true,
    acceptUppercase = true,
    minLength = 3,
    maxLength = 20,
)
