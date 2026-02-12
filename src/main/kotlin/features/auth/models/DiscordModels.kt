package com.bieniucieniu.features.auth.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a Discord User structure.
 *
 * @property id The user's id (Snowflake).
 * @property username The user's username, not unique across the platform.
 * @property discriminator The user's Discord-tag.
 * @property globalName The user's display name, if it is set. For bots, this is the application name.
 * @property avatar The user's avatar hash.
 * @property bot Whether the user belongs to an OAuth2 application.
 * @property system Whether the user is an Official Discord System user (part of the urgent message system).
 * @property mfaEnabled Whether the user has two factor enabled on their account.
 * @property banner The user's banner hash.
 * @property accentColor The user's banner color encoded as an integer representation of hexadecimal color code.
 * @property locale The user's chosen language option.
 * @property verified Whether the email on this account has been verified.
 * @property email The user's email.
 * @property flags The flags on a user’s account.
 * @property premiumType The type of Nitro subscription on a user’s account.
 * @property publicFlags The public flags on a user’s account.
 * @property avatarDecorationData Data for the user’s avatar decoration.
 * @property collectibles Data for the user’s collectibles.
 * @property primaryGuild The user’s primary guild.
 */
@Serializable
data class DiscordUser(
    val id: ULong,
    val username: String,
    val discriminator: String,

    @SerialName("global_name")
    val globalName: String?,

    val avatar: String?,

    val bot: Boolean? = null,

    val system: Boolean? = null,

    @SerialName("mfa_enabled")
    val mfaEnabled: Boolean? = null,

    val banner: String? = null,

    @SerialName("accent_color")
    val accentColor: Int? = null,

    val locale: String? = null,

    val verified: Boolean? = null,

    val email: String? = null,

    val flags: Int? = null,

    @SerialName("premium_type")
    val premiumType: Int? = null,

    @SerialName("public_flags")
    val publicFlags: Int? = null,

    @SerialName("avatar_decoration_data")
    val avatarDecorationData: AvatarDecorationData? = null,

    val collectibles: CollectiblesObject? = null,

    @SerialName("primary_guild")
    val primaryGuild: UserPrimaryGuild? = null
)

// Placeholder classes for the nested objects mentioned in your description
// You will need to define the fields for these based on their specific documentation.

@Serializable
data class AvatarDecorationData(
    val asset: String,
    @SerialName("sku_id") val skuId: String
)

@Serializable
data class CollectiblesObject(
    // Define properties based on "collectibles object" structure
    val id: String
)

@Serializable
data class UserPrimaryGuild(
    // Define properties based on "user primary guild object" structure
    val id: String
)