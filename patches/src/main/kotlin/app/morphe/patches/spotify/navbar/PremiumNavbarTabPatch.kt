package app.morphe.patches.spotify.navbar

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patches.spotify.misc.unlockPremiumPatch

@Deprecated("Superseded by unlockPremiumPatch", ReplaceWith("unlockPremiumPatch"))
@Suppress("unused")
val premiumNavbarTabPatch = bytecodePatch(
    description = "Hides the premium tab from the navigation bar.",
) {
    dependsOn(unlockPremiumPatch)
}
