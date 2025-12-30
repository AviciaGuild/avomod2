package cf.avicia.avomod2.inventoryoverlay.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;

public class RegularButtonWidget extends ButtonWidget {
    private float elevation = 360f;
    private PressAction onRightClickPress = null;
    public RegularButtonWidget(int x, int y, int width, int height, net.minecraft.text.Text text, PressAction onPress) {
        super(x, y, width, height, text, onPress, DEFAULT_NARRATION_SUPPLIER);
    }
    public RegularButtonWidget(int x, int y, int width, int height, net.minecraft.text.Text text, net.minecraft.text.Text tooltip, PressAction onPress) {
        super(x, y, width, height, text, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.setTooltip(Tooltip.of(tooltip));
    }
    public RegularButtonWidget(int x, int y, int width, int height, net.minecraft.text.Text text, net.minecraft.text.Text tooltip, PressAction onPress, PressAction onRightClickPress) {
        super(x, y, width, height, text, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.setTooltip(Tooltip.of(tooltip));
        this.onRightClickPress = onRightClickPress;
    }
    public RegularButtonWidget(int x, int y, int width, int height, net.minecraft.text.Text text, net.minecraft.text.Text tooltip, PressAction onPress, float elevation) {
        super(x, y, width, height, text, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.elevation = elevation;
        this.setTooltip(Tooltip.of(tooltip));
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (this.active && this.visible && onRightClickPress != null && click.button() == 1) {
            playClickSound(MinecraftClient.getInstance().getSoundManager());
            onRightClickPress.onPress(this);
        }
        return super.mouseClicked(click, doubled);
    }


    @Override
    protected void drawIcon(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        drawContext.fill(getX(), getY(), getX() + width, getY() + 1, 0xFFFFFFFF);
        drawContext.fill(getX() + width - 1, getY(), getX() + width, getY() + 20, 0xFFFFFFFF);
        drawContext.fill(getX(), getY(), getX() + 1, getY() + 20, 0xFFFFFFFF);
        drawContext.fill(getX(), getY() + 19, getX() + width, getY() + 20, 0xFFFFFFFF);

        this.drawTextWithMargin(drawContext.getTextConsumer(), this.getMessage(), 0);
    }
}
