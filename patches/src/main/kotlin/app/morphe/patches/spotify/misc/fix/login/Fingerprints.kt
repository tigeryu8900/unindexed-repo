package app.morphe.patches.spotify.misc.fix.login

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.literal

internal val katanaProxyLoginMethodHandlerClassFingerprint = Fingerprint(
    strings = listOf("katana_proxy_auth")
)

internal val katanaProxyLoginMethodTryAuthorizeFingerprint = Fingerprint(
    strings = listOf("e2e"),
    filters = listOf(literal(0))
)
