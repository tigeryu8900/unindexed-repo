package app.morphe.patches.spotify.misc.widgets

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.OpcodeFilter
import com.android.tools.smali.dexlib2.Opcode

internal val canBindAppWidgetPermissionFingerprint = Fingerprint(
    strings = listOf("android.permission.BIND_APPWIDGET"),
    filters = listOf(OpcodeFilter(Opcode.AND_INT_LIT8))
)
