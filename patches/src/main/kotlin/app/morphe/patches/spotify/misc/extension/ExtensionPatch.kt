package app.morphe.patches.spotify.misc.extension

import app.morphe.patches.shared.misc.extension.sharedExtensionPatch

val sharedExtensionPatch = sharedExtensionPatch(
    "spotify", 
    mainActivityOnCreateHook,
    loadOrbitLibraryHook
)
