package ninja.trek.calibratedsporeblossom.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import ninja.trek.calibratedsporeblossom.block.AdvancedSporeBlossomBlockEntity;
import ninja.trek.calibratedsporeblossom.network.AdvancedSporeBlossomSyncPayload;
import ninja.trek.calibratedsporeblossom.particle.ParticleTypeRegistry;

public class AdvancedSporeBlossomScreen extends AbstractContainerScreen<AdvancedSporeBlossomMenu> {

    private static final int PANEL_WIDTH = 260;
    private static final int SLIDER_WIDTH = 150;
    private static final int SLIDER_HEIGHT = 20;
    private static final int ROW_HEIGHT = 24;
    private static final int LABEL_WIDTH = 100;
    private static final int TOTAL_ROWS = AdvancedSporeBlossomBlockEntity.SETTING_COUNT;

    private int scrollOffset = 0;
    private int maxScroll = 0;
    private int panelTop;
    private int panelLeft;
    private int visibleRows;

    // Slider definitions: name, isFloat, min, max, floatMin, floatMax
    private static final SliderDef[] DEFS = {
        new SliderDef("Particle Type", false, 0, 44, 0, 0),
        new SliderDef("Density", true, 0, 0, 0.0f, 0.05f),
        new SliderDef("X Radius", false, 1, 64, 0, 0),
        new SliderDef("Y Radius", false, 1, 64, 0, 0),
        new SliderDef("Z Radius", false, 1, 64, 0, 0),
        new SliderDef("Emission Mode", false, 0, 2, 0, 0),
        new SliderDef("Redstone Mode", false, 0, 2, 0, 0),
        new SliderDef("Velocity X", true, 0, 0, -1.0f, 1.0f),
        new SliderDef("Velocity Y", true, 0, 0, -1.0f, 1.0f),
        new SliderDef("Velocity Z", true, 0, 0, -1.0f, 1.0f),
        new SliderDef("Gravity", true, 0, 0, -0.1f, 0.1f),
        new SliderDef("Color R", false, 0, 255, 0, 0),
        new SliderDef("Color G", false, 0, 255, 0, 0),
        new SliderDef("Color B", false, 0, 255, 0, 0),
        new SliderDef("Color Enabled", false, 0, 1, 0, 0),
        new SliderDef("Density Falloff", true, 0, 0, 0.0f, 1.0f),
        new SliderDef("Pulse Mode", false, 0, 1, 0, 0),
        new SliderDef("Pulse Interval", false, 5, 100, 0, 0),
        new SliderDef("Pulse Duration", false, 1, 50, 0, 0),
        new SliderDef("Particle Scale", true, 0, 0, 0.1f, 5.0f),
        new SliderDef("Randomness", true, 0, 0, 0.0f, 1.0f),
        new SliderDef("Animation Speed", true, 0, 0, 0.1f, 5.0f),
    };

    private static final String[] EMISSION_MODE_NAMES = {"Everywhere", "Surfaces", "Empty on Surface"};
    private static final String[] REDSTONE_MODE_NAMES = {"Off", "Density", "Size"};
    private static final String[] PULSE_MODE_NAMES = {"Continuous", "Pulse"};
    private static final String[] COLOR_ENABLED_NAMES = {"Default", "Override"};

    private SettingSlider[] sliders;
    private int[] lastSyncedValues;

    public AdvancedSporeBlossomScreen(AdvancedSporeBlossomMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = PANEL_WIDTH;
        this.imageHeight = 220;
    }

    @Override
    protected void init() {
        // Calculate panel height dynamically based on available screen space
        int idealHeight = 16 + TOTAL_ROWS * ROW_HEIGHT + 4; // title area + all rows + padding
        int maxHeight = this.height - 20; // leave some screen margin
        this.imageHeight = Math.clamp(idealHeight, 220, maxHeight);

        super.init();

        panelLeft = (this.width - PANEL_WIDTH) / 2;
        panelTop = (this.height - this.imageHeight) / 2;
        visibleRows = (this.imageHeight - 20) / ROW_HEIGHT; // leave room for title
        maxScroll = Math.max(0, TOTAL_ROWS - visibleRows);

        lastSyncedValues = new int[TOTAL_ROWS];
        rebuildSliders();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        // Check if ContainerData has changed (e.g. initial sync arrived from server)
        boolean changed = false;
        for (int i = 0; i < TOTAL_ROWS; i++) {
            int current = menu.getSetting(i);
            if (current != lastSyncedValues[i]) {
                changed = true;
                lastSyncedValues[i] = current;
            }
        }
        if (changed) {
            rebuildSliders();
        }
    }

    private void rebuildSliders() {
        // Remove old sliders
        if (sliders != null) {
            for (SettingSlider s : sliders) {
                if (s != null) removeWidget(s);
            }
        }
        sliders = new SettingSlider[TOTAL_ROWS];

        // Snapshot current values so containerTick can detect changes
        for (int i = 0; i < TOTAL_ROWS; i++) {
            lastSyncedValues[i] = menu.getSetting(i);
        }

        for (int i = 0; i < TOTAL_ROWS; i++) {
            int row = i - scrollOffset;
            if (row < 0 || row >= visibleRows) continue;

            int y = panelTop + 16 + row * ROW_HEIGHT;
            int x = panelLeft + LABEL_WIDTH;

            SliderDef def = DEFS[i];
            int rawValue = menu.getSetting(i);
            double sliderValue;

            if (def.isFloat) {
                float fVal = rawValue / (float) AdvancedSporeBlossomBlockEntity.FLOAT_SCALE;
                sliderValue = (fVal - def.floatMin) / (def.floatMax - def.floatMin);
            } else {
                sliderValue = def.max == def.min ? 0 : (double)(rawValue - def.min) / (def.max - def.min);
            }
            sliderValue = Math.clamp(sliderValue, 0.0, 1.0);

            final int settingIndex = i;
            SettingSlider slider = new SettingSlider(x, y, SLIDER_WIDTH, SLIDER_HEIGHT, sliderValue, settingIndex, def);
            sliders[i] = slider;
            addRenderableWidget(slider);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int oldScroll = scrollOffset;
        scrollOffset = (int) Math.clamp(scrollOffset - verticalAmount, 0, maxScroll);
        if (scrollOffset != oldScroll) {
            rebuildSliders();
        }
        return true;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        // Empty — we draw everything in render() to keep fills, widgets, and labels
        // in the same stratum so draw order is deterministic.
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // Empty — labels drawn in render()
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 1) Panel background fills — same stratum as widgets/labels, drawn first
        graphics.fill(panelLeft - 4, panelTop - 4, panelLeft + PANEL_WIDTH + 4, panelTop + this.imageHeight + 4, 0xCC000000);
        graphics.fill(panelLeft, panelTop, panelLeft + PANEL_WIDTH, panelTop + this.imageHeight, 0xCC1A1A2E);

        // 2) Widgets (sliders) via AbstractContainerScreen → Screen.render()
        super.render(graphics, mouseX, mouseY, partialTick);

        // 3) Title and row labels — drawn last so they're on top of everything
        int titleX = panelLeft + (PANEL_WIDTH - this.font.width(this.title)) / 2;
        graphics.drawString(this.font, this.title, titleX, panelTop + 4, 0xFFFFFFFF, true);

        for (int i = 0; i < TOTAL_ROWS; i++) {
            int row = i - scrollOffset;
            if (row < 0 || row >= visibleRows) continue;

            int y = panelTop + 16 + row * ROW_HEIGHT + (SLIDER_HEIGHT - 8) / 2;
            graphics.drawString(this.font, DEFS[i].name, panelLeft + 4, y, 0xFFFFFFFF, true);
        }

        // 4) Scroll indicator
        if (maxScroll > 0) {
            int barHeight = this.imageHeight - 20;
            int thumbHeight = Math.max(10, barHeight * visibleRows / TOTAL_ROWS);
            int thumbY = panelTop + 16 + (int)((barHeight - thumbHeight) * ((float) scrollOffset / maxScroll));
            int barX = panelLeft + PANEL_WIDTH - 4;
            graphics.fill(barX, panelTop + 16, barX + 3, panelTop + this.imageHeight, 0x40FFFFFF);
            graphics.fill(barX, thumbY, barX + 3, thumbY + thumbHeight, 0xA0FFFFFF);
        }
    }

    private void sendUpdate(int settingIndex, int encodedValue) {
        ClientPlayNetworking.send(
                new AdvancedSporeBlossomSyncPayload(menu.getBlockPos(), settingIndex, encodedValue)
        );
    }

    private record SliderDef(String name, boolean isFloat, int min, int max, float floatMin, float floatMax) {}

    private class SettingSlider extends AbstractSliderButton {
        private final int settingIndex;
        private final SliderDef def;

        public SettingSlider(int x, int y, int width, int height, double value, int settingIndex, SliderDef def) {
            super(x, y, width, height, Component.empty(), value);
            this.settingIndex = settingIndex;
            this.def = def;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            if (def == null) return;
            String display;
            if (def.isFloat) {
                float fVal = (float) (def.floatMin + this.value * (def.floatMax - def.floatMin));
                display = String.format("%.4f", fVal);
            } else {
                int iVal = (int) Math.round(def.min + this.value * (def.max - def.min));
                display = formatIntValue(settingIndex, iVal);
            }
            setMessage(Component.literal(display));
        }

        @Override
        protected void applyValue() {
            int encodedValue;
            if (def.isFloat) {
                float fVal = (float) (def.floatMin + this.value * (def.floatMax - def.floatMin));
                encodedValue = Math.round(fVal * AdvancedSporeBlossomBlockEntity.FLOAT_SCALE);
            } else {
                encodedValue = (int) Math.round(def.min + this.value * (def.max - def.min));
            }
            menu.setSetting(settingIndex, encodedValue);
            sendUpdate(settingIndex, encodedValue);
        }

        private String formatIntValue(int index, int val) {
            return switch (index) {
                case AdvancedSporeBlossomBlockEntity.SETTING_PARTICLE_TYPE ->
                    val + " - " + ParticleTypeRegistry.getDisplayName(Math.clamp(val, 0, ParticleTypeRegistry.COUNT - 1));
                case AdvancedSporeBlossomBlockEntity.SETTING_EMISSION_MODE ->
                    val + " - " + (val >= 0 && val < EMISSION_MODE_NAMES.length ? EMISSION_MODE_NAMES[val] : "?");
                case AdvancedSporeBlossomBlockEntity.SETTING_REDSTONE_MODE ->
                    val + " - " + (val >= 0 && val < REDSTONE_MODE_NAMES.length ? REDSTONE_MODE_NAMES[val] : "?");
                case AdvancedSporeBlossomBlockEntity.SETTING_PULSE_MODE ->
                    val + " - " + (val >= 0 && val < PULSE_MODE_NAMES.length ? PULSE_MODE_NAMES[val] : "?");
                case AdvancedSporeBlossomBlockEntity.SETTING_COLOR_ENABLED ->
                    val + " - " + (val >= 0 && val < COLOR_ENABLED_NAMES.length ? COLOR_ENABLED_NAMES[val] : "?");
                default -> String.valueOf(val);
            };
        }
    }
}
