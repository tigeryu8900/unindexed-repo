package app.morphe.patches.spotify.layout.theme

import app.morphe.patcher.Fingerprint
import app.morphe.util.containsLiteralInstruction
import com.android.tools.smali.dexlib2.AccessFlags

internal val colorSpaceUtilsClassFingerprint = Fingerprint(
    strings = listOf("The specified color must be encoded in an RGB color space.") // Partial string match.
)

internal val convertArgbToRgbaFingerprint = Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC, AccessFlags.FINAL),
    returnType = "J",
    parameters = listOf("J")
)

internal val parseLottieJsonFingerprint = Fingerprint(
    strings = listOf("Unsupported matte type: ")
)

internal val parseAnimatedColorFingerprint = Fingerprint(
    parameters = listOf("L", "F"),
    returnType = "Ljava/lang/Object;",
    custom = { method, _ ->
        method.containsLiteralInstruction(255.0) &&
                method.containsLiteralInstruction(1.0)
    }
)
