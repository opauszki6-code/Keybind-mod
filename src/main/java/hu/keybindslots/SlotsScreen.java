package hu.keybindslots;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class SlotsScreen extends Screen {
    private final ButtonWidget[] loadButtons = new ButtonWidget[SlotStorage.SLOTS];
    private Text status = Text.empty();

    public SlotsScreen() {
        super(Text.translatable("keybindslots.title"));
    }

    @Override
    protected void init() {
        int x0 = this.width / 2 - 129;
        int y0 = this.height / 2 - 50;

        for (int i = 0; i < SlotStorage.SLOTS; i++) {
            final int slot = i;
            int y = y0 + i * 28;

            TextFieldWidget name = new TextFieldWidget(
                    this.textRenderer, x0, y, 130, 20, Text.literal("Slot " + (i + 1)));
            name.setMaxLength(24);
            name.setText(SlotStorage.data.names.get(i));
            name.setChangedListener(s -> {
                SlotStorage.data.names.set(slot, s);
                SlotStorage.save();
            });
            this.addDrawableChild(name);

            this.addDrawableChild(ButtonWidget.builder(Text.translatable("keybindslots.save"), b -> {
                SlotStorage.saveCurrent(this.client, slot);
                loadButtons[slot].active = true;
                status = Text.translatable("keybindslots.saved");
            }).dimensions(x0 + 134, y, 60, 20).build());

            loadButtons[i] = ButtonWidget.builder(Text.translatable("keybindslots.load"), b -> {
                if (SlotStorage.apply(this.client, slot)) {
                    status = Text.translatable("keybindslots.loaded");
                } else {
                    status = Text.translatable("keybindslots.empty");
                }
            }).dimensions(x0 + 198, y, 60, 20).build();
            loadButtons[i].active = !SlotStorage.isEmpty(i);
            this.addDrawableChild(loadButtons[i]);
        }

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("keybindslots.done"),
                b -> this.close()).dimensions(this.width / 2 - 50, y0 + 3 * 28 + 10, 100, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 80, 0xFFFFFF);
        ctx.drawCenteredTextWithShadow(this.textRenderer, status, this.width / 2, this.height / 2 + 62, 0xA0A0A0);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        SlotStorage.save();
        super.close();
    }
}
