package ninja.trek.calibratedsporeblossom.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import ninja.trek.calibratedsporeblossom.particle.ParticleTypeRegistry;
import ninja.trek.calibratedsporeblossom.registry.ModBlockEntities;

public class AdvancedSporeBlossomBlockEntity extends BlockEntity implements Nameable {

    // Setting indices (must match ContainerData slot order)
    public static final int SETTING_PARTICLE_TYPE = 0;
    public static final int SETTING_DENSITY = 1;
    public static final int SETTING_X_RADIUS = 2;
    public static final int SETTING_Y_RADIUS = 3;
    public static final int SETTING_Z_RADIUS = 4;
    public static final int SETTING_EMISSION_MODE = 5;
    public static final int SETTING_REDSTONE_MODE = 6;
    public static final int SETTING_VELOCITY_X = 7;
    public static final int SETTING_VELOCITY_Y = 8;
    public static final int SETTING_VELOCITY_Z = 9;
    public static final int SETTING_GRAVITY = 10;
    public static final int SETTING_COLOR_R = 11;
    public static final int SETTING_COLOR_G = 12;
    public static final int SETTING_COLOR_B = 13;
    public static final int SETTING_COLOR_ENABLED = 14;
    public static final int SETTING_DENSITY_FALLOFF = 15;
    public static final int SETTING_PULSE_MODE = 16;
    public static final int SETTING_PULSE_INTERVAL = 17;
    public static final int SETTING_PULSE_DURATION = 18;
    public static final int SETTING_PARTICLE_SCALE = 19;
    public static final int SETTING_RANDOMNESS = 20;
    public static final int SETTING_ANIMATION_SPEED = 21;
    public static final int SETTING_COUNT = 22;

    // Float encoding multiplier for ContainerData
    public static final int FLOAT_SCALE = 10000;

    // --- Fields ---
    private int particleType = 0;
    private float density = 0.001f;
    private int xRadius = 8;
    private int yRadius = 8;
    private int zRadius = 8;
    private int emissionMode = 0;
    private int redstoneMode = 0;
    private float velocityX = 0.0f;
    private float velocityY = 0.0f;
    private float velocityZ = 0.0f;
    private float gravity = 0.0f;
    private int colorR = 255;
    private int colorG = 255;
    private int colorB = 255;
    private int colorEnabled = 0;
    private float densityFalloff = 0.0f;
    private int pulseMode = 0;
    private int pulseInterval = 20;
    private int pulseDuration = 5;
    private float particleScale = 1.0f;
    private float randomness = 1.0f;
    private float animationSpeed = 1.0f;

    public AdvancedSporeBlossomBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ADVANCED_SPORE_BLOSSOM_BE, pos, state);
    }

    // --- Getters ---
    public int getParticleType() { return particleType; }
    public float getDensity() { return density; }
    public int getXRadius() { return xRadius; }
    public int getYRadius() { return yRadius; }
    public int getZRadius() { return zRadius; }
    public int getEmissionMode() { return emissionMode; }
    public int getRedstoneMode() { return redstoneMode; }
    public float getVelocityX() { return velocityX; }
    public float getVelocityY() { return velocityY; }
    public float getVelocityZ() { return velocityZ; }
    public float getGravity() { return gravity; }
    public int getColorR() { return colorR; }
    public int getColorG() { return colorG; }
    public int getColorB() { return colorB; }
    public int getColorEnabled() { return colorEnabled; }
    public float getDensityFalloff() { return densityFalloff; }
    public int getPulseMode() { return pulseMode; }
    public int getPulseInterval() { return pulseInterval; }
    public int getPulseDuration() { return pulseDuration; }
    public float getParticleScale() { return particleScale; }
    public float getRandomness() { return randomness; }
    public float getAnimationSpeed() { return animationSpeed; }
    // --- Setters ---
    public void setParticleType(int v) { particleType = Math.clamp(v, 0, ParticleTypeRegistry.COUNT - 1); setChanged(); }
    public void setDensity(float v) { density = Math.clamp(v, 0.0f, 0.05f); setChanged(); }
    public void setXRadius(int v) { xRadius = Math.clamp(v, 1, 64); setChanged(); }
    public void setYRadius(int v) { yRadius = Math.clamp(v, 1, 64); setChanged(); }
    public void setZRadius(int v) { zRadius = Math.clamp(v, 1, 64); setChanged(); }
    public void setEmissionMode(int v) { emissionMode = Math.clamp(v, 0, 2); setChanged(); }
    public void setRedstoneMode(int v) { redstoneMode = Math.clamp(v, 0, 2); setChanged(); }
    public void setVelocityX(float v) { velocityX = Math.clamp(v, -1.0f, 1.0f); setChanged(); }
    public void setVelocityY(float v) { velocityY = Math.clamp(v, -1.0f, 1.0f); setChanged(); }
    public void setVelocityZ(float v) { velocityZ = Math.clamp(v, -1.0f, 1.0f); setChanged(); }
    public void setGravity(float v) { gravity = Math.clamp(v, -0.1f, 0.1f); setChanged(); }
    public void setColorR(int v) { colorR = Math.clamp(v, 0, 255); setChanged(); }
    public void setColorG(int v) { colorG = Math.clamp(v, 0, 255); setChanged(); }
    public void setColorB(int v) { colorB = Math.clamp(v, 0, 255); setChanged(); }
    public void setColorEnabled(int v) { colorEnabled = Math.clamp(v, 0, 1); setChanged(); }
    public void setDensityFalloff(float v) { densityFalloff = Math.clamp(v, 0.0f, 1.0f); setChanged(); }
    public void setPulseMode(int v) { pulseMode = Math.clamp(v, 0, 1); setChanged(); }
    public void setPulseInterval(int v) { pulseInterval = Math.clamp(v, 5, 100); setChanged(); }
    public void setPulseDuration(int v) { pulseDuration = Math.clamp(v, 1, 50); setChanged(); }
    public void setParticleScale(float v) { particleScale = Math.clamp(v, 0.1f, 5.0f); setChanged(); }
    public void setRandomness(float v) { randomness = Math.clamp(v, 0.0f, 1.0f); setChanged(); }
    public void setAnimationSpeed(float v) { animationSpeed = Math.clamp(v, 0.1f, 5.0f); setChanged(); }

    // --- Int-encoded access for ContainerData ---

    public int getSettingAsInt(int index) {
        return switch (index) {
            case SETTING_PARTICLE_TYPE -> particleType;
            case SETTING_DENSITY -> Math.round(density * FLOAT_SCALE);
            case SETTING_X_RADIUS -> xRadius;
            case SETTING_Y_RADIUS -> yRadius;
            case SETTING_Z_RADIUS -> zRadius;
            case SETTING_EMISSION_MODE -> emissionMode;
            case SETTING_REDSTONE_MODE -> redstoneMode;
            case SETTING_VELOCITY_X -> Math.round(velocityX * FLOAT_SCALE);
            case SETTING_VELOCITY_Y -> Math.round(velocityY * FLOAT_SCALE);
            case SETTING_VELOCITY_Z -> Math.round(velocityZ * FLOAT_SCALE);
            case SETTING_GRAVITY -> Math.round(gravity * FLOAT_SCALE);
            case SETTING_COLOR_R -> colorR;
            case SETTING_COLOR_G -> colorG;
            case SETTING_COLOR_B -> colorB;
            case SETTING_COLOR_ENABLED -> colorEnabled;
            case SETTING_DENSITY_FALLOFF -> Math.round(densityFalloff * FLOAT_SCALE);
            case SETTING_PULSE_MODE -> pulseMode;
            case SETTING_PULSE_INTERVAL -> pulseInterval;
            case SETTING_PULSE_DURATION -> pulseDuration;
            case SETTING_PARTICLE_SCALE -> Math.round(particleScale * FLOAT_SCALE);
            case SETTING_RANDOMNESS -> Math.round(randomness * FLOAT_SCALE);
            case SETTING_ANIMATION_SPEED -> Math.round(animationSpeed * FLOAT_SCALE);
            default -> 0;
        };
    }

    public void setSettingFromInt(int index, int value) {
        switch (index) {
            case SETTING_PARTICLE_TYPE -> setParticleType(value);
            case SETTING_DENSITY -> setDensity(value / (float) FLOAT_SCALE);
            case SETTING_X_RADIUS -> setXRadius(value);
            case SETTING_Y_RADIUS -> setYRadius(value);
            case SETTING_Z_RADIUS -> setZRadius(value);
            case SETTING_EMISSION_MODE -> setEmissionMode(value);
            case SETTING_REDSTONE_MODE -> setRedstoneMode(value);
            case SETTING_VELOCITY_X -> setVelocityX(value / (float) FLOAT_SCALE);
            case SETTING_VELOCITY_Y -> setVelocityY(value / (float) FLOAT_SCALE);
            case SETTING_VELOCITY_Z -> setVelocityZ(value / (float) FLOAT_SCALE);
            case SETTING_GRAVITY -> setGravity(value / (float) FLOAT_SCALE);
            case SETTING_COLOR_R -> setColorR(value);
            case SETTING_COLOR_G -> setColorG(value);
            case SETTING_COLOR_B -> setColorB(value);
            case SETTING_COLOR_ENABLED -> setColorEnabled(value);
            case SETTING_DENSITY_FALLOFF -> setDensityFalloff(value / (float) FLOAT_SCALE);
            case SETTING_PULSE_MODE -> setPulseMode(value);
            case SETTING_PULSE_INTERVAL -> setPulseInterval(value);
            case SETTING_PULSE_DURATION -> setPulseDuration(value);
            case SETTING_PARTICLE_SCALE -> setParticleScale(value / (float) FLOAT_SCALE);
            case SETTING_RANDOMNESS -> setRandomness(value / (float) FLOAT_SCALE);
            case SETTING_ANIMATION_SPEED -> setAnimationSpeed(value / (float) FLOAT_SCALE);
        }
    }

    // --- Persistence ---

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("ParticleType", particleType);
        output.putFloat("Density", density);
        output.putInt("XRadius", xRadius);
        output.putInt("YRadius", yRadius);
        output.putInt("ZRadius", zRadius);
        output.putInt("EmissionMode", emissionMode);
        output.putInt("RedstoneMode", redstoneMode);
        output.putFloat("VelocityX", velocityX);
        output.putFloat("VelocityY", velocityY);
        output.putFloat("VelocityZ", velocityZ);
        output.putFloat("Gravity", gravity);
        output.putInt("ColorR", colorR);
        output.putInt("ColorG", colorG);
        output.putInt("ColorB", colorB);
        output.putInt("ColorEnabled", colorEnabled);
        output.putFloat("DensityFalloff", densityFalloff);
        output.putInt("PulseMode", pulseMode);
        output.putInt("PulseInterval", pulseInterval);
        output.putInt("PulseDuration", pulseDuration);
        output.putFloat("ParticleScale", particleScale);
        output.putFloat("Randomness", randomness);
        output.putFloat("AnimationSpeed", animationSpeed);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        particleType = input.getIntOr("ParticleType", 0);
        density = input.getFloatOr("Density", 0.001f);
        xRadius = input.getIntOr("XRadius", 8);
        yRadius = input.getIntOr("YRadius", 8);
        zRadius = input.getIntOr("ZRadius", 8);
        emissionMode = input.getIntOr("EmissionMode", 0);
        redstoneMode = input.getIntOr("RedstoneMode", 0);
        velocityX = input.getFloatOr("VelocityX", 0.0f);
        velocityY = input.getFloatOr("VelocityY", 0.0f);
        velocityZ = input.getFloatOr("VelocityZ", 0.0f);
        gravity = input.getFloatOr("Gravity", 0.0f);
        colorR = input.getIntOr("ColorR", 255);
        colorG = input.getIntOr("ColorG", 255);
        colorB = input.getIntOr("ColorB", 255);
        colorEnabled = input.getIntOr("ColorEnabled", 0);
        densityFalloff = input.getFloatOr("DensityFalloff", 0.0f);
        pulseMode = input.getIntOr("PulseMode", 0);
        pulseInterval = input.getIntOr("PulseInterval", 20);
        pulseDuration = input.getIntOr("PulseDuration", 5);
        particleScale = input.getFloatOr("ParticleScale", 1.0f);
        randomness = input.getFloatOr("Randomness", 1.0f);
        animationSpeed = input.getFloatOr("AnimationSpeed", 1.0f);
    }

    // --- Client sync on chunk load ---

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // --- Nameable ---

    @Override
    public Component getName() {
        return Component.translatable("block.calibrated-spore-blossom.advanced_calibrated_spore_blossom");
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    /**
     * Marks dirty and sends update to clients tracking this block entity.
     */
    public void syncToClients() {
        if (level != null && !level.isClientSide()) {
            setChanged();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }
}
