/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package danksouls_getshrektedition.World;

import danksouls_getshrektedition.Board;
import danksouls_getshrektedition.Sprite;
import danksouls_getshrektedition.Character;
import java.awt.Rectangle;

/**
 *
 * @author pantsu
 */
public class CollisionBox {
    private int ypos;
    private int xpos;
    private int height;
    private int width;
    private Rectangle R;
    
    public CollisionBox(int xpos, int ypos, int height, int width) {
        this.xpos = xpos;
        this.ypos = ypos;
        this.height = height;
        this.width = width;
        R = new Rectangle(xpos, ypos, height, width);
    }
    
    public int getX() {
        return xpos;
    }
    
    public int getY() {
        return ypos;
    }
    
    public int getH() {
        return height;
    }
    
    public int getW() {
        return width;
    }
    
    public boolean isColliding(Character x) {
        Rectangle r = x.getBounds();
        return xpos < r.x + r.width && xpos + width > r.x && ypos < r.y + r.height && ypos + height > r.y;
    }
}
