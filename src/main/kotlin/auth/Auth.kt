package com.bieniucieniu.auth

import com.bieniucieniu.auth.oauth2.configureDiscordOauth2
import com.bieniucieniu.auth.oauth2.installGoogleOauth2
import com.bieniucieniu.errors.auth.UnauthorizedException
import com.bieniucieniu.features.auth.models.OAuth2Provider
import com.bieniucieniu.features.auth.models.UserSession
import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import io.ktor.utils.io.*
import org.koin.ktor.ext.inject

private const val USER_SESSION_KEY = "auth-session"
private const val OAUTH_DISCORD_KEY = "auth-oauth-discord"
private const val OAUTH_GOOGLE_KEY = "auth-oauth-google"

fun Application.installAuthPlugins() {
    val httpClient by inject<HttpClient>()
    val config = environment.config

    val frontendUrl by lazy {
        config.propertyOrNull("frontend.url")?.getString() ?: "http://localhost:3000"
    }

    install(Sessions) {
        cookie<UserSession>(USER_SESSION_KEY) {
            this.cookie.secure
            cookie.path = "/"
            cookie.maxAgeInSeconds = 3600
            transform(
                SessionTransportTransformerEncrypt(
                    hex(config.property("session.encryptKey").getString()),
                    hex(config.property("session.signKey").getString())
                )
            )
        }
    }



    install(Authentication) {
        session<UserSession>(USER_SESSION_KEY) {
            validate { session ->
                when (session.provider) {
                    OAuth2Provider.Google -> session
                    OAuth2Provider.Discord -> session
                    null -> null
                }
            }
        }
        val config = this@installAuthPlugins.environment.config
        installGoogleOauth2(
            OAUTH_GOOGLE_KEY,
            clientId = config.propertyOrNull("oauth2.google.clientId")?.getString(),
            clientSecret = config.propertyOrNull("oauth2.google.clientSecret")?.getString(),
            frontendUrl = frontendUrl,
            httpClient = httpClient
        )
        configureDiscordOauth2(
            OAUTH_DISCORD_KEY,
            clientId = config.propertyOrNull("oauth2.discord.clientId")?.getString(),
            clientSecret = config.propertyOrNull("oauth2.discord.clientSecret")?.getString(),
            frontendUrl = frontendUrl,
            httpClient = httpClient
        )
    }

}

fun RoutingContext.getUserSession(): UserSession =
    call.principal<UserSession>(USER_SESSION_KEY) ?: throw UnauthorizedException("Unauthorized")

fun Route.authenticateUserSession(build: Route.() -> Unit) = authenticate(USER_SESSION_KEY, build = build)

fun Route.authenticateOauth2Session(
    strategy: AuthenticationStrategy = AuthenticationStrategy.FirstSuccessful,
    build: Route.() -> Unit
) = authenticate(OAUTH_DISCORD_KEY, OAUTH_GOOGLE_KEY, strategy = strategy, build = build)

fun Route.authenticateDiscordSession(build: Route.() -> Unit) = authenticate(OAUTH_DISCORD_KEY, build = build)
fun Route.authenticateGoogleSession(build: Route.() -> Unit) = authenticate(OAUTH_GOOGLE_KEY, build = build)
fun Route.isDiscordSessionActive(): Boolean = hasAuthConfiguration(OAUTH_DISCORD_KEY)
fun Route.isGoogleSessionActive(): Boolean = hasAuthConfiguration(OAUTH_GOOGLE_KEY)

@OptIn(InternalAPI::class)
fun Route.hasAuthConfiguration(name: String): Boolean {
    return application
        .pluginOrNull(Authentication)
        ?.configuration()
        ?.allProviders()
        ?.containsKey(name)
        ?: false
}