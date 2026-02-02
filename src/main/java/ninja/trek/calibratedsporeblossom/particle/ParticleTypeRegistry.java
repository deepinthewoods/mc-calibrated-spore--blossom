package ninja.trek.calibratedsporeblossom.particle;

import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.PowerParticleOption;

import java.util.List;

public final class ParticleTypeRegistry {

    public static final int COUNT = 45;

    public record Entry(String displayName, ParticleOptions... particles) {}

    // Foliage colors (ARGB -> RGB floats)
    private static final int OAK_FOLIAGE      = 0x48B518;
    private static final int BIRCH_FOLIAGE     = 0x80A755;
    private static final int SPRUCE_FOLIAGE    = 0x619961;
    private static final int JUNGLE_FOLIAGE    = 0x30BB0B;
    private static final int ACACIA_FOLIAGE    = 0x48B518; // same as oak default
    private static final int DARK_OAK_FOLIAGE  = 0x48B518;
    private static final int MANGROVE_FOLIAGE  = 0x92C648;
    private static final int AZALEA_FOLIAGE    = 0x48B518;
    private static final int FL_AZALEA_FOLIAGE = 0x48B518;

    private static ColorParticleOption tintedLeaves(int rgb) {
        float r = ((rgb >> 16) & 0xFF) / 255.0f;
        float g = ((rgb >> 8) & 0xFF) / 255.0f;
        float b = (rgb & 0xFF) / 255.0f;
        return ColorParticleOption.create(ParticleTypes.TINTED_LEAVES, r, g, b);
    }

    private static final List<Entry> ENTRIES = List.of(
        /* 0  */ new Entry("Spore Blossom", ParticleTypes.SPORE_BLOSSOM_AIR, ParticleTypes.FALLING_SPORE_BLOSSOM),
        /* 1  */ new Entry("Cherry Leaves", ParticleTypes.CHERRY_LEAVES),
        /* 2  */ new Entry("Pale Oak Leaves", ParticleTypes.PALE_OAK_LEAVES),
        /* 3  */ new Entry("Oak Leaves", tintedLeaves(OAK_FOLIAGE)),
        /* 4  */ new Entry("Birch Leaves", tintedLeaves(BIRCH_FOLIAGE)),
        /* 5  */ new Entry("Spruce Leaves", tintedLeaves(SPRUCE_FOLIAGE)),
        /* 6  */ new Entry("Jungle Leaves", tintedLeaves(JUNGLE_FOLIAGE)),
        /* 7  */ new Entry("Acacia Leaves", tintedLeaves(ACACIA_FOLIAGE)),
        /* 8  */ new Entry("Dark Oak Leaves", tintedLeaves(DARK_OAK_FOLIAGE)),
        /* 9  */ new Entry("Mangrove Leaves", tintedLeaves(MANGROVE_FOLIAGE)),
        /* 10 */ new Entry("Azalea Leaves", tintedLeaves(AZALEA_FOLIAGE)),
        /* 11 */ new Entry("Flowering Azalea Leaves", tintedLeaves(FL_AZALEA_FOLIAGE)),
        /* 12 */ new Entry("Ash", ParticleTypes.ASH),
        /* 13 */ new Entry("White Ash", ParticleTypes.WHITE_ASH),
        /* 14 */ new Entry("Crimson Spore", ParticleTypes.CRIMSON_SPORE),
        /* 15 */ new Entry("Warped Spore", ParticleTypes.WARPED_SPORE),
        /* 16 */ new Entry("Mycelium", ParticleTypes.MYCELIUM),
        /* 17 */ new Entry("Snowflake", ParticleTypes.SNOWFLAKE),
        /* 18 */ new Entry("Underwater", ParticleTypes.UNDERWATER),
        /* 19 */ new Entry("Firefly", ParticleTypes.FIREFLY),
        /* 20 */ new Entry("Flame", ParticleTypes.FLAME),
        /* 21 */ new Entry("Soul Fire", ParticleTypes.SOUL_FIRE_FLAME),
        /* 22 */ new Entry("Copper Fire", ParticleTypes.COPPER_FIRE_FLAME),
        /* 23 */ new Entry("Smoke", ParticleTypes.SMOKE),
        /* 24 */ new Entry("Campfire Smoke", ParticleTypes.CAMPFIRE_COSY_SMOKE),
        /* 25 */ new Entry("White Smoke", ParticleTypes.WHITE_SMOKE),
        /* 26 */ new Entry("End Rod", ParticleTypes.END_ROD),
        /* 27 */ new Entry("Enchant", ParticleTypes.ENCHANT),
        /* 28 */ new Entry("Portal", ParticleTypes.PORTAL),
        /* 29 */ new Entry("Reverse Portal", ParticleTypes.REVERSE_PORTAL),
        /* 30 */ new Entry("Note", ParticleTypes.NOTE),
        /* 31 */ new Entry("Glow", ParticleTypes.GLOW),
        /* 32 */ new Entry("Sculk Soul", ParticleTypes.SCULK_SOUL),
        /* 33 */ new Entry("Rain", ParticleTypes.RAIN),
        /* 34 */ new Entry("Cloud", ParticleTypes.CLOUD),
        /* 35 */ new Entry("Dolphin", ParticleTypes.DOLPHIN),
        /* 36 */ new Entry("Heart", ParticleTypes.HEART),
        /* 37 */ new Entry("Happy Villager", ParticleTypes.HAPPY_VILLAGER),
        /* 38 */ new Entry("Dripping Water", ParticleTypes.DRIPPING_WATER),
        /* 39 */ new Entry("Dripping Lava", ParticleTypes.DRIPPING_LAVA),
        /* 40 */ new Entry("Dripping Honey", ParticleTypes.DRIPPING_HONEY),
        /* 41 */ new Entry("Falling Nectar", ParticleTypes.FALLING_NECTAR),
        /* 42 */ new Entry("Dragon Breath", PowerParticleOption.create(ParticleTypes.DRAGON_BREATH, 1.0f)),
        /* 43 */ new Entry("Nautilus", ParticleTypes.NAUTILUS),
        /* 44 */ new Entry("Dust Plume", ParticleTypes.DUST_PLUME)
    );

    public static Entry get(int index) {
        return ENTRIES.get(index);
    }

    public static String getDisplayName(int index) {
        return ENTRIES.get(index).displayName();
    }

    public static ParticleOptions[] getParticles(int index) {
        return ENTRIES.get(index).particles();
    }

    private ParticleTypeRegistry() {}
}
