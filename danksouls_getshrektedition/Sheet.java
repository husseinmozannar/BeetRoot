/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package danksouls_getshrektedition;

/**
 *
 * @author pantsu
 */
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

public class Sheet {

    private static BufferedImage spriteSheet;
    private static final int TILE_SIZE = 64;

    public static BufferedImage loadSprite(String filename) {

        BufferedImage sprite = null;
        try {
            sprite = ImageIO.read(new File(Sheet.class.getResource("/resources/sheet.png").toURI()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException ex) {
            Logger.getLogger(Sheet.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sprite;
    }

    public static BufferedImage getSprite(int xGrid, int yGrid) {

        if (spriteSheet == null) {
            spriteSheet = loadSprite("sheet");
        }

        return spriteSheet.getSubimage(xGrid * TILE_SIZE, yGrid * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }

}
