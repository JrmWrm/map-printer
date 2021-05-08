package me.jrm_wrm.map_printer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.MaterialColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;

public class MapSerializer {

    private static String imageFolder = "maps";

    /**
     * Saves a Filled Map Item to an image
     * @param mapStack the map to save
     * @param client the instance of the minecraft client
     * @return true if the saving was succesful
     */
    @Environment(EnvType.CLIENT)
    public static boolean saveMap(ItemStack mapStack, MinecraftClient client) {
        if (mapStack.getItem() != Items.FILLED_MAP) return false;

        // get the pixels from the map item
        MapState mapState = FilledMapItem.getMapState(mapStack, client.world);
        byte[] colors = mapState.colors;

        // create a new image
        BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB); 

        // loop through all pixels
        for (int i = 0; i < colors.length; i++) {
            int x = i % 128;
            int y = i / 128;

            // get the right color
            int l = colors[i] & 255;
            int color = MaterialColor.COLORS[l / 4].getRenderColor(l & 3);
            
            // convert from abgr to argb
            int a = color >> 24 & 255;
            int b = color >> 16 & 255;
            int g = color >> 8 & 255;
            int r = color >> 0 & 255;

            if (r == 0 && g == 0 && b == 0) a = 0;

            color = a << 24 | r << 16 | g << 8 | b << 0;

            // set the color in the image
            image.setRGB(x, y, color);
        }

        // create a file directory
        String imageName = mapState.getId() + ".png";
        String imageSubfolder = client.isIntegratedServerRunning() 
            ? client.getServer().getSaveProperties().getLevelName() 
            : "servers\\" + client.getCurrentServerEntry().address;

        String imageDirectory = client.runDirectory.getAbsolutePath() +"\\"+ imageFolder +"\\"+ imageSubfolder +"\\"+ imageName;
        File imageFile = new File(imageDirectory);

        // create the folders if they don't exist
        imageFile.getParentFile().mkdirs(); 

        // try writing to the png image
        try {
            ImageIO.write(image, "png", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // notify the player
        client.player.sendChatMessage("Map saved to: " + imageDirectory);

        return true;
    }

}
