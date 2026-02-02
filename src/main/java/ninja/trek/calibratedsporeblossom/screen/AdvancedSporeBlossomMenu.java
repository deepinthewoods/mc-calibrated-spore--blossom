package ninja.trek.calibratedsporeblossom.screen;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import ninja.trek.calibratedsporeblossom.block.AdvancedSporeBlossomBlockEntity;
import ninja.trek.calibratedsporeblossom.registry.ModMenuTypes;

public class AdvancedSporeBlossomMenu extends AbstractContainerMenu {

    private final ContainerData data;
    private final BlockPos blockPos;

    // Server-side constructor
    public AdvancedSporeBlossomMenu(int syncId, Inventory playerInv, AdvancedSporeBlossomBlockEntity be) {
        super(ModMenuTypes.ADVANCED_SPORE_BLOSSOM_MENU, syncId);
        this.blockPos = be.getBlockPos();

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return be.getSettingAsInt(index);
            }

            @Override
            public void set(int index, int value) {
                be.setSettingFromInt(index, value);
            }

            @Override
            public int getCount() {
                return AdvancedSporeBlossomBlockEntity.SETTING_COUNT;
            }
        };
        addDataSlots(this.data);
    }

    // Client-side constructor (from network)
    public static AdvancedSporeBlossomMenu fromNetwork(int syncId, Inventory playerInv, AdvancedSporeBlossomData payload) {
        return new AdvancedSporeBlossomMenu(syncId, playerInv, payload.pos());
    }

    // Client-side constructor with blockPos
    private AdvancedSporeBlossomMenu(int syncId, Inventory playerInv, BlockPos pos) {
        super(ModMenuTypes.ADVANCED_SPORE_BLOSSOM_MENU, syncId);
        this.blockPos = pos;
        this.data = new SimpleContainerData(AdvancedSporeBlossomBlockEntity.SETTING_COUNT);
        addDataSlots(this.data);
    }

    public ContainerData getData() {
        return data;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public int getSetting(int index) {
        return data.get(index);
    }

    public void setSetting(int index, int value) {
        data.set(index, value);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5) <= 64.0;
    }
}
