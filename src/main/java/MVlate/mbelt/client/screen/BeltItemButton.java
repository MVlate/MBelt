package MVlate.mbelt.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class BeltItemButton extends AbstractButton {
    private final ItemStack beltStack;
    private final Runnable onPress;

    public BeltItemButton(int x, int y, ItemStack beltStack, Runnable onPress) {
        super(x, y, 18, 18, Component.empty());
        this.beltStack = beltStack;
        this.onPress = onPress;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {

        graphics.renderItem(this.beltStack, this.getX() + 1, this.getY() + 1);
        if (this.isHoveredOrFocused()) {
            graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x40FFFFFF);
        }
    }

    @Override
    public void onPress() {
        this.onPress.run();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }
}