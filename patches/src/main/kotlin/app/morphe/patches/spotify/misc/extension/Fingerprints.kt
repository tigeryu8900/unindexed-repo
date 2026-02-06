package app.morphe.patches.spotify.misc.extension

import app.morphe.patcher.Fingerprint

internal val loadOrbitLibraryFingerprint = Fingerprint(
    strings = listOf("orbit_library_load", "orbit-jni-spotify")
)
