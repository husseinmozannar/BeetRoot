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
public class Key {

    public static Key up = new Key();
    public static Key down = new Key();
    public static Key left = new Key();
    public static Key right = new Key();
    public static Key shift = new Key();
    public static Key special = new Key();
    public static Key guard = new Key();
    public boolean isDown;

    /* toggles the keys current state*/
    public void toggle() {
        isDown = !isDown;
    }

}
