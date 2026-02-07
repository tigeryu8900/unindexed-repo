package app.morphe.patches.spotify.misc.extension

import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patches.shared.misc.extension.ExtensionHook
import app.morphe.patches.spotify.shared.mainActivityOnCreateFingerprint
import app.morphe.util.getReference
import app.morphe.util.indexOfFirstInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference

internal val mainActivityOnCreateHook = ExtensionHook(fingerprint = mainActivityOnCreateFingerprint)

internal val loadOrbitLibraryHook = ExtensionHook(
    insertIndexResolver = {
        loadOrbitLibraryFingerprint.stringMatches!!.last().index
    },
    contextRegisterResolver = { method ->
        val contextReferenceIndex = method.indexOfFirstInstruction {
            getReference<FieldReference>()?.type == "Landroid/content/Context;"
        }
        val contextRegister = method.getInstruction<TwoRegisterInstruction>(contextReferenceIndex).registerA

        "v$contextRegister"
    },
    fingerprint = loadOrbitLibraryFingerprint,
)
