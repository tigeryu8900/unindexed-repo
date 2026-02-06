package app.morphe.patches.spotify.layout.hide.createbutton

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.OpcodesFilter
import app.morphe.util.getReference
import app.morphe.util.indexOfFirstInstruction
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

internal val navigationBarItemSetClassFingerprint = Fingerprint(
    strings = listOf("NavigationBarItemSet(")
)

internal val navigationBarItemSetConstructorFingerprint = Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.CONSTRUCTOR),
    // Make sure the method checks whether navigation bar items are null before adding them.
    // If this is not true, then we cannot patch the method and potentially transform the parameters into null.
    filters = OpcodesFilter.opcodesToFilters(Opcode.IF_EQZ, Opcode.INVOKE_VIRTUAL),
    custom = { method, _ ->
        method.indexOfFirstInstruction {
            getReference<MethodReference>()?.name == "add"
        } >= 0
    }
)

internal val oldNavigationBarAddItemFingerprint = Fingerprint(
    strings = listOf("Bottom navigation tabs exceeds maximum of 5 tabs")
)
