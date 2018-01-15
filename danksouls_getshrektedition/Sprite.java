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
import java.awt.Rectangle;

public class Sprite {

    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected boolean vis;
//    protected Image image;

    public Sprite(int x, int y, int h, int w) {
        this.height = h;
        this.width = w;
        this.x = x;
        this.y = y;
        vis = true;
    }

//    protected void getImageDimensions() {
//        width = image.getWidth(null);
//        height = image.getHeight(null);
//    }
//
//    protected void loadImage(String imageName) {
//        ImageIcon ii = new ImageIcon(getClass().getClassLoader().getResource(imageName));
//        image = ii.getImage();
//    }
//
//    public Image getImage() {
//        return image;
//    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isVisible() {
        return vis;
    }

    public void setVisible(Boolean visible) {
        vis = visible;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    
    public boolean isColliding(Sprite x) {
        Rectangle r = x.getBounds();
        return this.x < r.x + r.width && this.x + width > r.x && this.y < r.y + r.height && this.y + height > r.y;
    }    
}
