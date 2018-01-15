/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package danksouls_getshrektedition;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 *
 * @author pantsu
 */
public class Character extends Sprite {

    private final BufferedImage[] walkingLeft = {Sheet.getSprite(0, 2), Sheet.getSprite(1, 2), Sheet.getSprite(2, 2)};
    private final BufferedImage[] walkingRight = {Sheet.getSprite(0, 1), Sheet.getSprite(1, 1), Sheet.getSprite(2, 1)};
    private final BufferedImage[] standing = {Sheet.getSprite(0, 0), Sheet.getSprite(1, 0), Sheet.getSprite(2, 0)};
    private final BufferedImage[] guardingLeft = {Sheet.getSprite(0, 4), Sheet.getSprite(1, 4), Sheet.getSprite(2, 4)};
    private final BufferedImage[] guardingRight = {Sheet.getSprite(0, 3), Sheet.getSprite(1, 3), Sheet.getSprite(2, 3)};
    private final BufferedImage[] attackingRight = {Sheet.getSprite(0, 5), Sheet.getSprite(1, 5), Sheet.getSprite(2, 5)};
    private final BufferedImage[] attackingLeft = {Sheet.getSprite(0, 6), Sheet.getSprite(1, 6), Sheet.getSprite(2, 6)};
    private final BufferedImage[] stunnedBuff = {Sheet.getSprite(0, 7), Sheet.getSprite(1, 7), Sheet.getSprite(2, 7)};

    
    private Animation walkLeft, walkRight, guardLeft, guardRight, attackLeft, attackRight, stunnedAnim;
    private Animation std;
    public Animation animation;
    private String facing;
    public double speedX;
    public double gravity;
    public double speedY;
    public boolean isOnGround, stunned;
    public int stunTmr, stunTime;
    
    public Bar HP;
    public Bar SP;
    
    public Character(int x, int y) {
        super(x, y, 50, 45);
        initChar();
    }
    
    public void initChar() {
        stunned = false;
        stunTime = 150;
        stunTmr = 0;
        HP = new Bar(x - 10 , y - 15, 5, 80 ,100, false);
        SP = new Bar(x - 10 , y - 10, 5, 80, 100, true);
        isOnGround = true;
        gravity = 0.1;
        speedX = 0;
        facing = "front";
        walkLeft = new Animation(walkingLeft, 12);
        walkRight = new Animation(walkingRight, 12);
        guardRight = new Animation(guardingRight, 12);
        guardLeft = new Animation(guardingLeft, 12);
        attackLeft = new Animation(attackingLeft, 15);
        attackRight = new Animation(attackingRight, 15);
        stunnedAnim = new Animation(stunnedBuff, 15);
        std = new Animation(standing, 12);
        animation = std;
        
    }

    public void keyPressed() {
        
        if(SP.value < 0) {
            stunned = true;
            animation = stunnedAnim;
            animation.start();
        }
        
        if(stunned == true) {
            stunTmr += 1;
            if(stunTmr > stunTime) {
                stunned = false;
                stunTmr = 0;
                animation = std;
                animation.start();
            }
        }
        
        if (animation.done == true && !stunned) {

            if (Key.special.isDown && facing.equals("left") && SP.value >= 5) {
                SP.reduce(20);
                SP.updateValue();
                animation = attackLeft;
                animation.reset();
                animation.isAttack = true;
                animation.done = false;
            }
            if (Key.special.isDown && facing.equals("right") && SP.value >= 5) {
                SP.reduce(20);
                SP.updateValue();
                animation = attackRight;
                animation.reset();
                animation.isAttack = true;
                animation.done = false;
            }

            if (Key.guard.isDown && facing.equals("left") && SP.value > 5) {
                SP.reduce(0.2);
                SP.updateValue();
                animation = guardLeft;
                animation.start();
            }
            if (Key.guard.isDown && facing.equals("right") && SP.value > 5) {
                SP.reduce(0.2);
                SP.updateValue();
                animation = guardRight;
                animation.start();
            }
            
            if (facing.equals("right") && !Key.guard.isDown && animation.done) {
                animation = walkRight;
                animation.start();
            }
            if (facing.equals("left") && !Key.guard.isDown && animation.done) {
                animation = walkLeft;
                animation.start();
            }
        }
    }
    
    public void move() {
        if (!stunned) {
            x += speedX;
            HP.x += speedX;
            SP.x += speedX;

            y += speedY;
            HP.y += speedY;
            SP.y += speedY;

            speedY += gravity;
        }
    }
    
    public void moveUpdt() {
        SP.Regenerate();
        if (Key.up.isDown && isOnGround) {
            speedY = -3.8;
        }
        if(!Key.left.isDown && !Key.right.isDown || Key.guard.isDown) {
            speedX = 0;
        }
        if(Key.right.isDown && !Key.left.isDown) {
            facing = "right";
            if (!Key.guard.isDown) {
                if (Key.shift.isDown) {
                    speedX = 2;
                } else {
                    speedX = 1;
                }
            }
        }
        if(Key.left.isDown && !Key.right.isDown) {
            facing = "left";
            if (!Key.guard.isDown) {
                if (Key.shift.isDown) {
                    speedX = -2;
                } else {
                    speedX = -1;
                }
            }
        }
    }
    
    public String getAnimation() {
        if (animation.equals(walkLeft)) {
            return "walkingLeft";
        }
        else if (animation.equals(walkRight)) {
            return "walkingRight";
        }
        else if(animation.equals(stunnedAnim)) {
            return "stunnedAnim";
        }
        else if(animation.equals(guardLeft)) {
            return "guardLeft";
        }
        else if(animation.equals(guardRight)) {
            return "guardRight";
        }
        else if(animation.equals(attackRight)) {
            return "attackRight";
        }
        else if(animation.equals(attackLeft)) {
            return "attackLeft";
        }
        else return "std";
    }
    
    public void resetAnim() {
        animation = std;
        animation.start();
    }
    
    public class Camera {
        public int camX;
        public int camY;
        
        public Camera(int playerX, int playerY, int viewX, int viewY) {
            camX = playerX - viewX/2;
            camY = playerY - viewY/2;
        }
        
        
    }
}

