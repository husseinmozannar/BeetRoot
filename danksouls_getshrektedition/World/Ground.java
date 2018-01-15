/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package danksouls_getshrektedition.World;

import danksouls_getshrektedition.Character;
import danksouls_getshrektedition.Board;

/**
 *
 * @author pantsu
 **/
public class Ground extends CollisionBox {

    private final Character player1;
    private final double force;
    
    public Ground(int x, int y, Character player1) {
        super(x, y, 10, Board.worldX);
        this.player1 = player1;
        force = player1.gravity;
    }
    
    public boolean checkCollisions() {
        if(isColliding(player1)) {
            player1.gravity = 0;
            player1.speedY = 0;
            player1.isOnGround = true;
            return true;
        }
        else {
            player1.gravity = force;
            player1.isOnGround = false;
            return false;
        } 
    }
    
}
