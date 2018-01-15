/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package danksouls_getshrektedition;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

/**
 *
 * @author pantsu
 */
    public class Controller implements KeyListener {

        public Controller() {
            myBind(KeyEvent.VK_UP, Key.up);
            myBind(KeyEvent.VK_LEFT, Key.left);
            myBind(KeyEvent.VK_DOWN, Key.down);
            myBind(KeyEvent.VK_RIGHT, Key.right);
            myBind(KeyEvent.VK_SHIFT, Key.shift);
            myBind(KeyEvent.VK_SPACE, Key.special);
            myBind(KeyEvent.VK_ALT, Key.guard);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            keyBindings.get(e.getKeyCode()).isDown = true;
        }

        @Override
        public void keyReleased(KeyEvent e) {
            keyBindings.get(e.getKeyCode()).isDown = false;
        }

        public boolean isKeyBinded(int extendedKey) {
            return keyBindings.containsKey(extendedKey);
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        private void myBind(Integer keyCode, Key key) {
            keyBindings.put(keyCode, key);
        }

        public void releaseAll() {
            keyBindings.values().stream().forEach((key) -> {
                key.isDown = false;
            });
        }

        public HashMap<Integer, Key> keyBindings = new HashMap<Integer, Key>();
    }
