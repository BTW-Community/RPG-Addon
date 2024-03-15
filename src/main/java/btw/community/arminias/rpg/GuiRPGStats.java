package btw.community.arminias.rpg;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;

public class GuiRPGStats extends GuiScreen {
    private GuiScreen parentScreen;
    private GuiButton buttonDone;

    private GuiButton[] buttonsPlus = new GuiButton[RPGPointsAllocation.NUM_STATS];
    private GuiButton[] buttonsMinus = new GuiButton[RPGPointsAllocation.NUM_STATS];

    private RPGPointsAllocation allocation;
    private RPGStatsSurrogate stats;

    public GuiRPGStats(GuiScreen guiScreen, RPGPointsAllocation allocation) {
        this.parentScreen = guiScreen;
        this.allocation = allocation;
        this.stats = new RPGStatsSurrogate(this.allocation);
    }

    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(this.buttonDone = new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120, "Done"));

        int x = this.width / 2 - 95;
        int y = this.height / 4 + 14;
        int w = 200;
        int h = 20;
        int spacing = 20;

        for (int i = 0; i < RPGPointsAllocation.NUM_STATS; i++) {
            this.buttonList.add(this.buttonsPlus[i] = new GuiButton(i + 1,
                    x + w - 10, y + i * spacing, 20, 20, "+"));
            this.buttonList.add(this.buttonsMinus[i] = new GuiButton(i + 1 + RPGPointsAllocation.NUM_STATS,
                    x + w - 55, y + i * spacing, 20, 20, "-"));
        }
    }

    public void drawScreen(int i, int j, float f) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "RPG Stats", this.width / 2, 40, 0xffffff);
        this.drawCenteredString(this.fontRenderer, "Spent Points: " + allocation.getSpentPoints() + " / " + allocation.getMaxPoints(), this.width / 2, 60, 0xffffff);

        super.drawScreen(i, j, f);

        int x = this.width / 2 - 100;
        int y = this.height / 4 + 20;
        int w = 200;
        int h = 20;
        int spacing = 20;

        for (int k = 0; k < RPGPointsAllocation.NUM_STATS; k++) {
            this.drawString(this.fontRenderer, allocation.getStatName(k), x, y + k * spacing, 0xffffff);
            this.drawString(this.fontRenderer, Integer.toString(allocation.getStatValue(k)), x + w - 22, y + k * spacing, 0xffffff);
            // Draw modifier of stat
            this.drawString(this.fontRenderer, stats.getModifierString(k), x + w + 20, y + k * spacing, 0xffffff);
        }
    }

    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.id == 0) {
            RPGAddon.pointsAllocation = allocation;
            this.mc.displayGuiScreen(this.parentScreen);
        } else {
            int i = guibutton.id - 1;
            if (i < RPGPointsAllocation.NUM_STATS) {
                allocation.incrementStat(i);
            } else {
                allocation.decrementStat(i % RPGPointsAllocation.NUM_STATS);
            }
        }
    }
}
