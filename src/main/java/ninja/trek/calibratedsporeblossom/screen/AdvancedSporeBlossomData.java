package ninja.trek.calibratedsporeblossom.screen;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * Data sent from server to client when opening the Advanced Spore Blossom menu.
 * Contains the block position so the client can locate the block entity.
 */
public record AdvancedSporeBlossomData(BlockPos pos) {

    public static final StreamCodec<RegistryFriendlyByteBuf, AdvancedSporeBlossomData> STREAM_CODEC =
            StreamCodec.of(
                    (buf, data) -> buf.writeBlockPos(data.pos()),
                    buf -> new AdvancedSporeBlossomData(buf.readBlockPos())
            );
}
