package app.morphe.patches.spotify.misc.privacy

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.instructionsOrNull
import app.morphe.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.StringReference

val shareLinkFingerprint = Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.CONSTRUCTOR),
    parameters = listOf(
        "Ljava/lang/String;",
        "Ljava/lang/String;",
        "Ljava/lang/String;",
        "Ljava/lang/String;"
    ),
    returnType = "V",
    custom =  { _, classDef ->
        val toStringMethod = classDef.methods.firstOrNull {
            it.name == "toString" && it.parameters.isEmpty() && it.returnType == "Ljava/lang/String;"
        } ?: return@Fingerprint false

        val toStringInstructions = toStringMethod.instructionsOrNull ?: return@Fingerprint false
        toStringInstructions.any { instruction ->
            instruction.opcode == Opcode.CONST_STRING &&
                    (instruction as? ReferenceInstruction)?.reference?.let { ref ->
                        (ref as? StringReference)?.string?.startsWith("ShareUrl(url=") == true
                    } == true
        }
    }
)
