package ninja.trek.calibratedsporeblossom.registry;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;
import ninja.trek.calibratedsporeblossom.CalibratedSporeBlossom;
import ninja.trek.calibratedsporeblossom.screen.AdvancedSporeBlossomMenu;
import ninja.trek.calibratedsporeblossom.screen.AdvancedSporeBlossomData;

public final class ModMenuTypes {

    public static final MenuType<AdvancedSporeBlossomMenu> ADVANCED_SPORE_BLOSSOM_MENU =
            new ExtendedScreenHandlerType<>(AdvancedSporeBlossomMenu::fromNetwork, AdvancedSporeBlossomData.STREAM_CODEC);

    public static void register() {
        Registry.register(
                BuiltInRegistries.MENU,
                Identifier.fromNamespaceAndPath(CalibratedSporeBlossom.MOD_ID, "advanced_spore_blossom_menu"),
                ADVANCED_SPORE_BLOSSOM_MENU
        );
    }

    private ModMenuTypes() {}
}
