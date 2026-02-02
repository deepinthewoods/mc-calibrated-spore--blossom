package ninja.trek.calibratedsporeblossom.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import ninja.trek.calibratedsporeblossom.CalibratedSporeBlossom;

/**
 * Client-to-server packet sent when a slider value changes in the GUI.
 */
public record AdvancedSporeBlossomSyncPayload(BlockPos pos, int settingIndex, int value) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AdvancedSporeBlossomSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(CalibratedSporeBlossom.MOD_ID, "advanced_spore_sync"));

    public static final StreamCodec<FriendlyByteBuf, AdvancedSporeBlossomSyncPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeBlockPos(payload.pos());
                        buf.writeVarInt(payload.settingIndex());
                        buf.writeVarInt(payload.value());
                    },
                    buf -> new AdvancedSporeBlossomSyncPayload(buf.readBlockPos(), buf.readVarInt(), buf.readVarInt())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
