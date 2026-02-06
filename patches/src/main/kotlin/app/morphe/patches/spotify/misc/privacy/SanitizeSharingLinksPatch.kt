package app.morphe.patches.spotify.extended

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.instructionsOrNull
import app.morphe.patcher.fingerprint
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patches.spotify.misc.privacy.shareLinkFingerprint
import app.morphe.util.getReference
import app.morphe.util.indexOfFirstInstructionOrThrow
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.reference.StringReference

private const val EXTENSION_CLASS_DESCRIPTOR =
    "Lapp/morphe/extension/spotify/misc/privacy/SanitizeSharingLinksPatch;"

@Suppress("unused")
val sanitizeSharingLinksPatch = bytecodePatch(
    name = "Sanitize sharing links",
    description = "Removes the tracking query parameters from links before they are shared.",
) {
    compatibleWith(
        "com.spotify.music"(
            "9.0.90.1229",
        ),
    )

    execute {
        val originalMethod = shareLinkFingerprint.method
        val invokeDirectIndex = originalMethod.indexOfFirstInstructionOrThrow {
            opcode == Opcode.INVOKE_DIRECT &&
                    (this as ReferenceInstruction).reference is MethodReference &&
                    (getReference<MethodReference>()?.name == "<init>") &&
                    (getReference<MethodReference>()?.definingClass == "Ljava/lang/Object;")
        }

        val smaliCodeToInsert = """
                invoke-static {p1}, $EXTENSION_CLASS_DESCRIPTOR->sanitizeUrl(Ljava/lang/String;)Ljava/lang/String;
                move-result-object p1
            """.trimIndent()

        originalMethod.addInstructions(invokeDirectIndex + 1, smaliCodeToInsert)
    }
}
