package shedar.mods.ic2.nuclearcontrol;

import net.minecraft.src.Container;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.StatCollector;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiRangeTrigger extends GuiContainer
{
    private String name;
    private ContainerRangeTrigger container;
    private ItemStack prevCard;

    public GuiRangeTrigger(Container container)
    {
        super(container);
        ySize = 190;
        this.container = (ContainerRangeTrigger)container; 
        name = StatCollector.translateToLocal("tile.blockRangeTrigger.name");
    }
    
    @SuppressWarnings("unchecked")
    private void initControls()
    {
        ItemStack card = container.getSlot(TileEntityRangeTrigger.SLOT_CARD).getStack();
        if(card!=null  && card.equals(prevCard))
            return;
        controlList.clear();
        prevCard = card;
        // ten digits, up to 10 billions
        for(int i=0; i<10; i++)
        {
            this.controlList.add(new CompactButton(i*10, guiLeft + 30 + i*12 + (i+2)/3*6, guiTop + 20, 12, 12, "-"));
            this.controlList.add(new CompactButton(i*10+1, guiLeft + 30 + i*12 + (i+2)/3*6, guiTop + 42, 12, 12, "+"));
        }
        for(int i=0; i<10; i++)
        {
            this.controlList.add(new CompactButton(100+i*10, guiLeft + 30 + i*12 + (i+2)/3*6, guiTop + 57, 12, 12, "-"));
            this.controlList.add(new CompactButton(100+i*10+1, guiLeft + 30 + i*12 + (i+2)/3*6, guiTop + 79, 12, 12, "+"));
        }
        controlList.add(new GuiRangeTriggerInvertRedstone(10, guiLeft + 8, guiTop + 62, container.trigger));
    }
    
    @Override
    public void initGui() 
    {
        super.initGui();
        initControls();
    };

    private void renderValue(long value, int x, int y)
    {
        x+=114;
        for( int i=0; i<10; i++)
        {
            byte digit = (byte)(value % 10);
            String str = Byte.toString(digit);
            fontRenderer.drawString(str, x - 12*i - fontRenderer.getCharWidth(str.charAt(0))/2 + (9-i+2)/3*6, y , 0x404040);
            value /= 10;
        }
        
    }

    @Override 
    protected void actionPerformed(GuiButton button)
    {
        int id = button.id;
        boolean isPlus = id % 2 == 1;
        id /= 10;
        int power = 9 - (id % 10);
        id /= 10;
        boolean isEnd = id % 2 == 1;
        long initValue = isEnd ? container.trigger.levelEnd:container.trigger.levelStart;
        long newValue = initValue;
        long delta = (long)Math.pow(10, power);
        long digit = (initValue / delta) % 10;
        if(isPlus && digit<9)
        {
            newValue += delta;
        }
        else if(!isPlus && digit > 0)
        {
            newValue -= delta;
        }
        if(newValue != initValue)
        {
            TileEntityRangeTrigger trigger = container.trigger;
            NuclearNetworkHelper.setRangeTrigger(trigger.xCoord, trigger.yCoord, trigger.zCoord, newValue, isEnd);
            if(isEnd)
                trigger.setLevelEnd(newValue);
            else
                trigger.setLevelStart(newValue);
        }
    };
    
    @Override
    protected void drawGuiContainerForegroundLayer()
    {
        fontRenderer.drawString(name, (xSize - fontRenderer.getStringWidth(name)) / 2, 6, 0x404040);
        fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
        
        renderValue(container.trigger.levelStart, 30, 33);
        renderValue(container.trigger.levelEnd, 30, 70);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
    {
        int texture = mc.renderEngine.getTexture("/img/GUIRangeTrigger.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(texture);
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
    }
    
}