package net.minecraft.src.nuclearcontrol;

public interface IRotation
{
    void rotate();
    
    int getRotation();
    
    void setRotation(int rotation);
    
    short getFacing();
}
