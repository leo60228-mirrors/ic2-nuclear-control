package net.minecraft.src.nuclearcontrol;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiButton;
import net.minecraft.src.ic2.api.NetworkHelper;

import org.lwjgl.opengl.GL11;

public class GuiRemoteThermoSlider extends GuiButton
{
    private static final int ARROW_WIDTH = 6;
    private static final float TEMP_RANGE = 16000;
    private static final int HEAT_STEP = 100;

    public float sliderValue;
    public boolean dragging;
    private String label;
    private TileEntityIC2Thermo thermo;
    private float effectiveWidth;
    private double sliderValueStep;
    

    public GuiRemoteThermoSlider(int id, int x, int y, String label, TileEntityIC2Thermo thermo)
    {
        super(id, x, y, 181, 16, label);
        this.thermo = thermo;
        dragging = false;
        this.label = label;
        sliderValue = (thermo.getHeatLevel())/TEMP_RANGE;
        displayString = String.format(label, getNormalizedHeatLevel());
        effectiveWidth = width - 8 - 2*ARROW_WIDTH;
        sliderValueStep = HEAT_STEP/TEMP_RANGE;
    }
    
    private int getNormalizedHeatLevel()
    {
        return ((int)Math.floor(TEMP_RANGE * sliderValue))/100*100;
    }

    private void setSliderPos(int targetX)
    {
        if(targetX < xPosition + ARROW_WIDTH)//left arrow
        {
            sliderValue -= sliderValueStep;
        }
        else if(targetX > xPosition + width - ARROW_WIDTH)// right arrow
        {
            sliderValue += sliderValueStep;
        }
        else
        {
            sliderValue = (float) (targetX - (xPosition + 4 + ARROW_WIDTH)) / effectiveWidth;
        }
        
        if (sliderValue < 0.0F)
        {
            sliderValue = 0.0F;
        }
        if (sliderValue > 1.0F)
        {
            sliderValue = 1.0F;
        }
        int newHeatLevel = getNormalizedHeatLevel(); 
        if(thermo.getHeatLevel()!=newHeatLevel){
            thermo.setHeatLevel(newHeatLevel);
            NetworkHelper.initiateClientTileEntityEvent(thermo, newHeatLevel);
        }
        displayString = String.format(label, newHeatLevel);
    }
    
    @Override
    public void drawButton(Minecraft minecraft, int targetX, int targetY) {
        if (drawButton)
        {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, minecraft.renderEngine.getTexture("/img/GUIRemoteThermo.png"));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (dragging && (targetX >= xPosition + ARROW_WIDTH) && (targetX <= xPosition + width - ARROW_WIDTH))
            {
                setSliderPos(targetX);
            }
            drawTexturedModalRect(xPosition + ARROW_WIDTH + (int)(sliderValue * effectiveWidth), yPosition, 0, 166, 8, 16);
            minecraft.fontRenderer.drawString(displayString, xPosition, yPosition - 12, 0x404040);
        }
    }

    @Override
    public boolean mousePressed(Minecraft minecraft, int targetX, int j)
    {
        if (super.mousePressed(minecraft, targetX, j))
        {
            setSliderPos(targetX);
            dragging = true;
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void mouseReleased(int i, int j)
    {
        super.mouseReleased(i, j);
        dragging = false;
    }
}
