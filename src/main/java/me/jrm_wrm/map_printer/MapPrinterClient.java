package me.jrm_wrm.map_printer;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;

public class MapPrinterClient implements ClientModInitializer{

    public static final String MOD_ID = "map_printer";
    public static final String MOD_NAME = "Map Printer";

    // keybinding for printing/saving maps
    private static final KeyBinding PRINT_KEY_BINDING = new KeyBinding(
        "key."+MOD_ID+".print", 
        InputUtil.Type.KEYSYM, 
        GLFW.GLFW_KEY_M, 
        "key.category."+MOD_ID);

    @Override
    public void onInitializeClient() {
        // register keybinding
        KeyBindingHelper.registerKeyBinding(PRINT_KEY_BINDING);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            
            while (PRINT_KEY_BINDING.wasPressed()) {
                ItemStack mainHandStack = client.player.getMainHandStack();
                MapSerializer.saveMap(mainHandStack, client);
            }

        });
    }
    
}
