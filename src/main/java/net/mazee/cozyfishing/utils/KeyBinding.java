package net.mazee.cozyfishing.utils;

import com.mojang.blaze3d.platform.InputConstants;
import net.mazee.cozyfishing.CozyFishing;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

//public class KeyBinding {
//    public static final String KEY_CATEGORY_COZYFISHING = "key.category.cozyfishing";
//    public static final String KEY_REEL_IN = "key.cozyfishing.reel_in";
//
//    public static final KeyMapping REELING_IN_KEY = new KeyMapping(KEY_REEL_IN, KeyConflictContext.IN_GAME,
//            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O, KEY_CATEGORY_COZYFISHING);
//}
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public enum KeyBinding {

    REEL_IN("reel_in", GLFW.GLFW_KEY_B),

    ;

    private KeyMapping keybind;
    private String description;
    private int key;
    private boolean modifiable;

    private KeyBinding(String description, int defaultKey) {
        this.description = CozyFishing.MODID + ".keyinfo." + description;
        this.key = defaultKey;
        this.modifiable = !description.isEmpty();
    }

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        for (KeyBinding key : values()) {
            key.keybind = new KeyMapping(key.description, key.key, CozyFishing.MODID);
            if (!key.modifiable)
                continue;

            event.register(key.keybind);
        }
    }

    public KeyMapping getKeybind() {
        return keybind;
    }

    public boolean isPressed() {
        System.out.println(keybind.isDown());
        if (!modifiable)
            return isKeyDown(key);
        return keybind.isDown();
    }

    public String getBoundKey() {
        return keybind.getTranslatedKeyMessage()
                .getString()
                .toUpperCase();
    }

    public int getBoundCode() {
        return keybind.getKey()
                .getValue();
    }

    public static boolean isKeyDown(int key) {
        return InputConstants.isKeyDown(Minecraft.getInstance()
                .getWindow()
                .getWindow(), key);
    }

    public static boolean isMouseButtonDown(int button) {
        return GLFW.glfwGetMouseButton(Minecraft.getInstance()
                .getWindow()
                .getWindow(), button) == 1;
    }

    public static boolean ctrlDown() {
        return Screen.hasControlDown();
    }

    public static boolean shiftDown() {
        return Screen.hasShiftDown();
    }

    public static boolean altDown() {
        return Screen.hasAltDown();
    }

}
