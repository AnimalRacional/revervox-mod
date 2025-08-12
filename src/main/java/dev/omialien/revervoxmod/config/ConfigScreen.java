package dev.omialien.revervoxmod.config;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ConfigScreen extends Screen {
    /** Distance from top of the screen to this GUI's title */
    private static final int TITLE_HEIGHT = 8;

    public ConfigScreen() {
        // Use the super class' constructor to set the screen's title
        super(Component.translatable("revervox.configGui.title",
                "Revervox"));
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        // First draw the background of the screen
        this.renderBackground(pGuiGraphics);
        // Draw the title
        pGuiGraphics.drawCenteredString(this.font, this.title.getString(),
                this.width / 2, TITLE_HEIGHT, 0xFFFFFF);
        // Call the super class' method to complete rendering
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

}
