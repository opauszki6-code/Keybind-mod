package hu.keybindslots;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeybindSlotsClient implements ClientModInitializer {
    public static KeyBinding openKey;

    @Override
    public void onInitializeClient() {
        SlotStorage.load();

        // Az "í" a magyar kiosztáson az ISO extra billentyű (Y mellett balra),
        // amit a GLFW WORLD_2 néven ismer. A Controls menüben átállítható.
        openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.keybindslots.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_WORLD_2,
                "category.keybindslots"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new SlotsScreen());
                }
            }
        });
    }
}
