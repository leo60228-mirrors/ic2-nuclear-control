package net.minecraft.src.nuclearcontrol;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.GuiScreen;
import net.minecraft.src.OpenGlHelper;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.StatCollector;
import net.minecraft.src.mod_IC2NuclearControl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiHowlerAlarm extends GuiScreen
{
    private TileEntityHowlerAlarm alarm;
    private GuiHowlerAlarmSlider slider;
    private GuiHowlerAlarmListBox listBox;
    private int guiLeft;
    private int guiTop;
    private int xSize = 131;
    private int ySize = 136;
    private String name;

    public GuiHowlerAlarm(TileEntityHowlerAlarm alarm)
    {
        super();
        this.alarm = alarm;
        name = StatCollector.translateToLocal("tile.blockHowlerAlarm.name");
    }
    
    @Override
    public void initGui() {
        super.initGui();
        guiLeft = (this.width - xSize) / 2;
        guiTop = (this.height - ySize) / 2;
        controlList.clear();
        slider = new GuiHowlerAlarmSlider(3, guiLeft+12, guiTop + 33, "Sound range: %s", alarm);
        List<String> items;
        if(alarm.worldObj.isRemote)
        {
            items =  new ArrayList<String>(mod_IC2NuclearControl.availableAlarms);
            items.retainAll(mod_IC2NuclearControl.serverAllowedAlarms);
        }
        else
        {
            items = mod_IC2NuclearControl.availableAlarms;
        }
        listBox = new GuiHowlerAlarmListBox(4, guiLeft+13, guiTop+63, 105, 65, items, alarm);
        controlList.add(slider);
        controlList.add(listBox);
        
    };
    
    public boolean doesGuiPauseGame()
    {
        return false;
    }
    
    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        drawDefaultBackground();
        drawGuiContainerBackgroundLayer();
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glPushMatrix();
        GL11.glTranslatef((float)guiLeft, (float)guiTop, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        drawGuiContainerForegroundLayer();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
        super.drawScreen(par1, par2, par3);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private void drawGuiContainerForegroundLayer()
    {
        fontRenderer.drawString(name, (xSize - fontRenderer.getStringWidth(name)) / 2, 6, 0x404040);
        fontRenderer.drawString(StatCollector.translateToLocal("Sound"), 12, 53, 0x404040);
    }

    private void drawGuiContainerBackgroundLayer()
    {
        int texture = mc.renderEngine.getTexture("/img/GUIHowlerAlarm.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(texture);
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
    }
    
    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int which)
    {
        super.mouseMovedOrUp(mouseX, mouseY, which);
        if((which == 0 || which == 1) && slider.dragging )
        {
            slider.mouseReleased(mouseX, mouseY);
            listBox.mouseReleased(mouseX, mouseY);
        }
    }

}
