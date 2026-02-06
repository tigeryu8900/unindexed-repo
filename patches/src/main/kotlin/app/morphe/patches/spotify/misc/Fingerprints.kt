@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package app.morphe.patches.spotify.misc

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.OpcodesFilter
import app.morphe.patcher.patch.BytecodePatchContext
import app.morphe.util.getReference
import app.morphe.util.indexOfFirstInstruction
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.reference.TypeReference

context(BytecodePatchContext)
internal val accountAttributeFingerprint get() = Fingerprint(
    custom = { _, classDef -> classDef.type == "Lcom/spotify/remoteconfig/internal/AccountAttribute;" }
)

context(BytecodePatchContext)
internal val productStateProtoGetMapFingerprint get() = Fingerprint(
    returnType = "Ljava/util/Map;",
    custom = { _, classDef -> classDef.type == "Lcom/spotify/remoteconfig/internal/ProductStateProto;" }
)

internal val buildQueryParametersFingerprint = Fingerprint(
    strings = listOf("trackRows", "device_type:tablet")
)

internal val contextMenuViewModelClassFingerprint = Fingerprint(
    strings = listOf("ContextMenuViewModel(header=")
)

/**
 * Used in versions older than "9.0.60.128".
 */
internal val oldContextMenuViewModelAddItemFingerprint = Fingerprint(
    parameters = listOf("L"),
    returnType = "V",
    custom = { method, _ ->
        method.indexOfFirstInstruction {
            getReference<MethodReference>()?.name == "add"
        } >= 0
    }
)

internal val contextMenuViewModelConstructorFingerprint = Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.CONSTRUCTOR)
)

/**
 * Used to find the interface name of a context menu item.
 */
internal val removeAdsContextMenuItemClassFingerprint = Fingerprint(
    strings = listOf("?displayReason=", "play-without-ads-exp"),
)

internal const val CONTEXT_MENU_ITEM_CLASS_DESCRIPTOR_PLACEHOLDER = "Lapp/morphe/ContextMenuItemPlaceholder;"
internal val extensionFilterContextMenuItemsFingerprint = Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "Ljava/util/List;",
    parameters = listOf("Ljava/util/List;"),
    custom = { method, classDef ->
        method.name == "filterContextMenuItems" && classDef.type == EXTENSION_CLASS_DESCRIPTOR
    }
)

internal val getViewModelFingerprint = Fingerprint(
    custom = { method, _ -> method.name == "getViewModel" }
)

internal val contextFromJsonFingerprint = Fingerprint(
    filters = OpcodesFilter.opcodesToFilters(
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_STATIC
    ),
    custom = { method, classDef ->
        method.name == "fromJson" &&
                classDef.type.endsWith("voiceassistants/playermodels/ContextJsonAdapter;")
    }
)

internal val readPlayerOptionOverridesFingerprint = Fingerprint(
    custom = { method, classDef ->
        method.name == "readPlayerOptionOverrides" &&
                classDef.type.endsWith("voiceassistants/playermodels/PreparePlayOptionsJsonAdapter;")
    }
)

internal val protobufListsFingerprint = Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    custom = { method, _ -> method.name == "emptyProtobufList" }
)

internal val abstractProtobufListEnsureIsMutableFingerprint = Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    parameters = listOf(),
    returnType = "V",
    custom = { method, _ ->
    	method.indexOfFirstInstruction {
            getReference<TypeReference>()?.type == "Ljava/lang/UnsupportedOperationException;"
        } >= 0
    }
)

internal fun structureGetSectionsFingerprint(className: String) = Fingerprint(
    custom = { method, classDef ->
        classDef.type.endsWith(className) && method.indexOfFirstInstruction {
            opcode == Opcode.IGET_OBJECT && getReference<FieldReference>()?.name == "sections_"
        } >= 0
    }
)

internal val homeSectionFingerprint = Fingerprint(
    custom = { _, classDef -> classDef.type.endsWith("homeapi/proto/Section;") }
)

internal val homeStructureGetSectionsFingerprint =
    structureGetSectionsFingerprint("homeapi/proto/HomeStructure;")

internal val browseSectionFingerprint = Fingerprint(
    custom = { _, classDef-> classDef.type.endsWith("browsita/v1/resolved/Section;") }
)

internal val browseStructureGetSectionsFingerprint =
    structureGetSectionsFingerprint("browsita/v1/resolved/BrowseStructure;")

internal fun reactivexFunctionApplyWithClassInitFingerprint(className: String) = Fingerprint(
    returnType = "Ljava/lang/Object;",
    parameters = listOf("Ljava/lang/Object;"),
    custom = { method, _ ->
        method.name == "apply" && method.indexOfFirstInstruction {
            opcode == Opcode.NEW_INSTANCE && getReference<TypeReference>()?.type?.endsWith(className) == true
        } >= 0
    }
)

internal const val PENDRAGON_JSON_FETCH_MESSAGE_REQUEST_CLASS_NAME = "FetchMessageRequest;"
internal val pendragonJsonFetchMessageRequestFingerprint =
    reactivexFunctionApplyWithClassInitFingerprint(PENDRAGON_JSON_FETCH_MESSAGE_REQUEST_CLASS_NAME)

internal const val PENDRAGON_PROTO_FETCH_MESSAGE_LIST_REQUEST_CLASS_NAME = "FetchMessageListRequest;"
internal val pendragonProtoFetchMessageListRequestFingerprint =
    reactivexFunctionApplyWithClassInitFingerprint(PENDRAGON_PROTO_FETCH_MESSAGE_LIST_REQUEST_CLASS_NAME)
