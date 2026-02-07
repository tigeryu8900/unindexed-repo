package app.morphe.patches.shared.extension

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.FingerprintBuilder
import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.fingerprint
import app.morphe.patcher.patch.BytecodePatchContext
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.util.proxy.mutableTypes.encodedValue.MutableLongEncodedValue
import app.morphe.patches.shared.extension.Constants.EXTENSION_PATCH_STATUS_CLASS_DESCRIPTOR
import app.morphe.patches.shared.extension.Constants.EXTENSION_UTILS_CLASS_DESCRIPTOR
import app.morphe.util.mutableClassDefByOrNull
import app.morphe.util.returnEarly
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.immutable.value.ImmutableLongEncodedValue
import java.util.jar.Manifest

/**
 * A patch to extend with an extension shared with multiple patches.
 *
 * @param extensionName The name of the extension to extend with.
 */
@Suppress("unused")
fun sharedExtensionPatch(
    extensionName: String,
    vararg hooks: ExtensionHook,
) = bytecodePatch {
    dependsOn(sharedExtensionPatch(*hooks))

    extendWith("extensions/shared.mpe")
}

fun sharedExtensionPatch(
    vararg hooks: ExtensionHook,
) = bytecodePatch(
    description = "sharedExtensionPatch"
) {
    extendWith("extensions/shared.mpe")

    execute {
        if (classDefByOrNull { EXTENSION_UTILS_CLASS_DESCRIPTOR == it.type } == null) {
            throw PatchException(
                "Shared extension has not been merged yet. This patch can not succeed without merging it.",
            )
        }
        hooks.forEach { hook -> hook(EXTENSION_UTILS_CLASS_DESCRIPTOR) }
    }

    finalize {
        var mutableClassDef = mutableClassDefByOrNull(EXTENSION_PATCH_STATUS_CLASS_DESCRIPTOR)
            ?: throw PatchException("No matching methods found in: $EXTENSION_PATCH_STATUS_CLASS_DESCRIPTOR")

        mutableClassDef.methods.forEach { method ->
            when (method.name) {
                "PatchedTime" -> method.replaceInstruction(
                    0,
                    "const-wide v0, ${MutableLongEncodedValue(ImmutableLongEncodedValue(System.currentTimeMillis()))}L"
                )

                "PatchVersion" -> method.apply {
                    val manifest = object {}
                        .javaClass
                        .classLoader
                        .getResources("META-INF/MANIFEST.MF")

                    while (manifest.hasMoreElements()) {
                        Manifest(manifest.nextElement().openStream())
                            .mainAttributes
                            .getValue("Version")
                            ?.let {
                                returnEarly(it)
                                return@finalize
                            }
                    }
                }
        }
    }
}

@Suppress("CONTEXT_RECEIVERS_DEPRECATED")
class ExtensionHook internal constructor(
    val fingerprint: Fingerprint,
    private val insertIndexResolver: (BytecodePatchContext.(Method) -> Int),
    private val contextRegisterResolver: BytecodePatchContext.(Method) -> String,
) {
    context(BytecodePatchContext)
    operator fun invoke(extensionClassDescriptor: String) {
        val insertIndex = insertIndexResolver(fingerprint.method)
        val contextRegister = contextRegisterResolver(fingerprint.method)

        fingerprint.method.addInstruction(
            insertIndex,
            "invoke-static/range { $contextRegister .. $contextRegister }, " +
                    "$extensionClassDescriptor->setContext(Landroid/content/Context;)V",
        )
    }
}

fun extensionHook(
    insertIndexResolver: BytecodePatchContext.(Method) -> Int = { 0 },
    contextRegisterResolver: BytecodePatchContext.(Method) -> String = { "p0" },
    fingerprint: Fingerprint,
) = ExtensionHook(fingerprint, insertIndexResolver, contextRegisterResolver)
