package app.morphe.patches.spotify.misc.lyrics

import app.morphe.patcher.Fingerprint
import app.morphe.util.getReference
import app.morphe.util.indexOfFirstInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

internal val httpClientBuilderFingerprint = Fingerprint(
    strings = listOf("client == null", "scheduler == null")
)

internal fun getLyricsHttpClientFingerprint(httpClientBuilderMethodReference: MethodReference) =
    Fingerprint(
        returnType = httpClientBuilderMethodReference.returnType,
        parameters = listOf(),
        custom =  { method, _ ->
            method.indexOfFirstInstruction {
                getReference<MethodReference>() == httpClientBuilderMethodReference
            } >= 0
        }
    )
