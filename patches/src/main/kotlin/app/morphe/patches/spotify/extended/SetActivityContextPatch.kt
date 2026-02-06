package app.morphe.patches.spotify.extended

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.getReference
import app.morphe.util.indexOfFirstInstructionOrThrow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

private const val UTILS_CLASS_DESCRIPTOR = "Lapp/morphe/extension/shared/utils/Utils;"
private const val MAIN_ACTIVITY_DESCRIPTOR = "Lcom/spotify/music/SpotifyMainActivity;"
// private const val NOW_PLAYING_ACTIVITY_DESCRIPTOR = "Lcom/spotify/nowplaying/musicinstallation/NowPlayingActivity;"

val mainActivityOnCreateFingerprint = Fingerprint(
    custom = { method, classDef ->
        classDef.type == MAIN_ACTIVITY_DESCRIPTOR &&
                method.name == "onCreate" &&
                method.parameters.size == 1 &&
                method.parameters.first().type == "Landroid/os/Bundle;" &&
                method.returnType == "V"
    }
)

@Suppress("unused")
val setActivityContextPatch = bytecodePatch(
    description = "setActivityContextPatch",
) {
    compatibleWith("com.spotify.music")

    execute {
        val targetMethod = mainActivityOnCreateFingerprint.method

        val invokeSuperIndex = targetMethod.indexOfFirstInstructionOrThrow {
            (opcode == Opcode.INVOKE_SUPER_RANGE || opcode == Opcode.INVOKE_SUPER) &&
                    (getReference<MethodReference>()?.let { methodRef -> methodRef.name == "onCreate" } == true)
        }

        val injectionIndex = invokeSuperIndex + 1
        val smaliCodeToInsert = """
            invoke-static/range {p0 .. p0}, $UTILS_CLASS_DESCRIPTOR->setActivity(Landroid/app/Activity;)V
        """.trimIndent()

        targetMethod.addInstructions(injectionIndex, smaliCodeToInsert)
    }
}
