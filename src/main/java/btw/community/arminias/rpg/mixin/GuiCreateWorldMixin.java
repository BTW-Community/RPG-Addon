package btw.community.arminias.rpg.mixin;

import btw.community.arminias.rpg.GuiRPGStats;
import btw.community.arminias.rpg.RPGPointsAllocation;
import btw.community.arminias.rpg.SettableRPGPointsAllocation;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiCreateWorld;
import net.minecraft.src.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiCreateWorld.class)
public abstract class GuiCreateWorldMixin extends GuiScreen implements SettableRPGPointsAllocation {
    private GuiButton buttonRPGStats;
    private RPGPointsAllocation allocation = RPGPointsAllocation.defaultAllocation(RPGPointsAllocation.DEFAULT_POINTS);

    @Override
    public RPGPointsAllocation getRPGPointsAllocation() {
        return this.allocation;
    }

    @Override
    public void setRPGPointsAllocation(RPGPointsAllocation allocation) {
        this.allocation = allocation;
    }

    @Inject(method = "initGui", at = @At("RETURN"))
    public void initGuiInject(CallbackInfo ci) {
        //RPGAddon.pointsAllocation = null;
        this.buttonRPGStats = new GuiButton(73, 8, 60, 98, 20, "RPG Stats");
        this.buttonList.add(this.buttonRPGStats);
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    public void actionPerformedInject(GuiButton button, CallbackInfo ci) {
        if (button.id == 73) {
            this.mc.displayGuiScreen(new GuiRPGStats(this, allocation));
        }
    }

}
