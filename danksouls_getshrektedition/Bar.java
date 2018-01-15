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
public class Bar {
    public Character player;
    public int x, y, width, height;
    public double fullValue, value, fullWidth;
    public int regen, regenRate;
    public boolean canRegen;
    
    public Bar(int x, int y, int height, int width, double value, boolean canRegen) {
        regenRate = 15;
        regen = 0;
        this.x = x; 
        this.y = y;
        this.width = width;
        this.fullWidth = width;
        this.height = height;
        this.value = value;
        this.fullValue = value;
        this.canRegen = canRegen;
    }
    
    public void reduce(double r) {
        value -= r;
        if(value < 0) {
            value = -10;
        }
    }
    
    public void updateValue() {
        width = (int)((value/fullValue) * fullWidth);
    }
    
    public void Regenerate() {
        if(canRegen) {
            regen += 1;
            if(regen >= regenRate) {
                regen = 0;
                width += 1;
                value += 1.25;
                if(width > fullWidth) {
                    width = (int)fullWidth;
                }
                if(value > fullValue) {
                    value = fullValue;
                }
            }
        }
    }
}
