/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package danksouls_getshrektedition;

import java.awt.image.BufferedImage;

/**
 *
 * @author pantsu
 */
public class Enemy extends Sprite {
    
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
    public boolean canHit = true;
    
    public Bar HP;
    public Bar SP;
    
    
    public Enemy(int x, int y) {
        super(x, y, 50, 45);
        initEnemy();
    }
    
    public void initEnemy() {
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
        animation.start();
    }
    
    public void setAnimation(String anim) {
        switch (anim) {
            case "walkingLeft":
                animation = walkLeft;
                animation.start();
                facing = "left";
                canHit = true;
                break;
            case "walkingRight":
                animation = walkRight;
                animation.start();
                facing = "right";
                canHit = true;
                break;
            case "guardRight":
                animation = guardRight;
                animation.start();
                facing = "right";
                canHit = true;
                break;
            case "guardLeft":
                animation = guardLeft;
                animation.start();
                facing = "left";
                canHit = true;
                break;
            case "attackRight":
                animation = attackRight;
                animation.start();
                facing = "right";
                break;
            case "attackLeft":
                animation = attackLeft;
                animation.start();
                facing = "left";
                break;
            case "stunnedAnim":
                animation = stunnedAnim;
                animation.start();
                canHit = true;
                break;
            default:
                animation = std;
                facing = "front";
                canHit = true;
                break;
        }
    }
        public void resetAnim() {
        animation = std;
        animation.start();
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
    
    public void setBars(int HPvalue, int SPvalue) {
        this.HP.value = HPvalue;
        this.HP.updateValue();
        
        this.SP.value = SPvalue;
        this.SP.updateValue();
    }
    
    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
        
        this.HP.x = x - 10;
        this.HP.y = y - 15;
    }
    
}
