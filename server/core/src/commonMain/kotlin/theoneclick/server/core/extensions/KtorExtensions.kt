package theoneclick.server.core.extensions

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import theoneclick.shared.core.models.endpoints.base.Endpoint

val Parameters.clientId: String? get() = get("client_id")
val Parameters.redirectUri: String? get() = get("redirect_uri")
val Parameters.refreshToken: String? get() = get("refresh_token")
val Parameters.clientSecret: String? get() = get("client_secret")
val Parameters.state: String? get() = get("state")
val Parameters.responseType: String? get() = get("response_type")
val Parameters.grantType: String? get() = get("grant_type")
val Parameters.code: String? get() = get("code")
val Parameters.username: String? get() = get("username")
val Parameters.password: String? get() = get("password")

val HttpHeaders.ContentSecurityPolicy: String
    get() = "Content-Security-Policy"

fun <Params, ValidationResult> Routing.get(
    endpoint: Endpoint,
    paramsParsing: suspend RoutingContext.() -> Params,
    paramsValidation: (Params) -> ValidationResult,
    block: suspend RoutingContext.(validationResult: ValidationResult) -> Unit,
): Route {
    return route(endpoint.route, HttpMethod.Get) {
        handle {
            val params = paramsParsing(this)
            val validationResult = paramsValidation(params)
            block(validationResult)
        }
    }
}

fun <Params, ValidationResult> Routing.post(
    endpoint: Endpoint,
    paramsParsing: suspend RoutingContext.() -> Params,
    paramsValidation: (Params) -> ValidationResult,
    block: suspend RoutingContext.(validationResult: ValidationResult) -> Unit
): Route {
    return route(endpoint.route, HttpMethod.Post) {
        handle {
            val params = paramsParsing(this)
            val validationResult = paramsValidation(params)
            block(validationResult)
        }
    }
}

inline fun <reified Params : Any, reified ValidationResult : Any> Routing.post(
    endpoint: Endpoint,
    crossinline requestValidation: (Params) -> ValidationResult,
    crossinline block: suspend RoutingContext.(ValidationResult) -> Unit
): Route = post(endpoint.route) {
    val request: Params = call.receive()
    val validationResult = requestValidation(request)
    block(validationResult)
}
