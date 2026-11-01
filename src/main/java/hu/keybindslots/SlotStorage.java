package hu.keybindslots;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SlotStorage {
    public static final int SLOTS = 3;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE =
            FabricLoader.getInstance().getConfigDir().resolve("keybindslots.json");

    public static class Data {
        public List<String> names = new ArrayList<>();
        public List<Map<String, String>> binds = new ArrayList<>();
    }

    public static Data data = new Data();

    public static void load() {
        if (Files.exists(FILE)) {
            try (Reader r = Files.newBufferedReader(FILE)) {
                Data d = GSON.fromJson(r, Data.class);
                if (d != null) data = d;
            } catch (Exception e) {
                System.err.println("[KeybindSlots] Nem sikerült betölteni: " + e);
            }
        }
        normalize();
    }

    private static void normalize() {
        if (data.names == null) data.names = new ArrayList<>();
        if (data.binds == null) data.binds = new ArrayList<>();
        while (data.names.size() < SLOTS) data.names.add("Slot " + (data.names.size() + 1));
        while (data.binds.size() < SLOTS) data.binds.add(new LinkedHashMap<>());
        for (int i = 0; i < SLOTS; i++) {
            if (data.binds.get(i) == null) data.binds.set(i, new LinkedHashMap<>());
        }
    }

    public static void save() {
        try (Writer w = Files.newBufferedWriter(FILE)) {
            GSON.toJson(data, w);
        } catch (IOException e) {
            System.err.println("[KeybindSlots] Nem sikerült menteni: " + e);
        }
    }

    public static boolean isEmpty(int slot) {
        return data.binds.get(slot).isEmpty();
    }

    /** Elmenti a jelenlegi összes keybindot a slotba. */
    public static void saveCurrent(MinecraftClient mc, int slot) {
        Map<String, String> m = new LinkedHashMap<>();
        for (KeyBinding kb : mc.options.allKeys) {
                        m.put(kb.getId(), kb.getBoundKeyTranslationKey());
        }
        data.binds.set(slot, m);
        save();
    }

    /** Alkalmazza a slot keybindjait. Igaz, ha volt mit betölteni. */
    public static boolean apply(MinecraftClient mc, int slot) {
        Map<String, String> m = data.binds.get(slot);
        if (m.isEmpty()) return false;
        for (KeyBinding kb : mc.options.allKeys) {
                        String key = m.get(kb.getId());
            if (key == null) continue;
            try {
                kb.setBoundKey(InputUtil.fromTranslationKey(key));
            } catch (IllegalArgumentException ignored) {
            }
        }
        KeyBinding.updateKeysByCode();
        mc.options.write();
        return true;
    }
}
