package btw.community.arminias.rpg.mixin;

import btw.community.arminias.rpg.FoodStatsExtension;
import btw.community.arminias.rpg.RPGStats;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.FoodStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodStats.class)
public class FoodStatsMixin implements FoodStatsExtension {
    private EntityPlayer player;

    @Override
    public void setPlayer(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public EntityPlayer getPlayer() {
        return player;
    }

    @ModifyConstant(method = {"addStats(IF)V", "needFood"}, constant = @Constant(intValue = 60, ordinal = 0))
    private int modifyAddStatsMaxHunger(int original) {
        return player != null ? (int) ((RPGStats) player).getModifiedMaxShanks() : original;
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void onUpdateHead(EntityPlayer player, CallbackInfo ci) {
        this.player = player;
    }

    @ModifyVariable(method = "addExhaustion", argsOnly = true, ordinal = 0, at = @At("HEAD"))
    private float modifyExhaustion(float exhaustion) {
        return player != null ? ((RPGStats) player).getHungerRateModifier() * exhaustion : exhaustion;
    }

    @ModifyConstant(method = "onUpdate", constant = @Constant(intValue = 24, ordinal = 0))
    private int modifyHungerTickRate(int hungerThreshold) {
        return player != null ? (int)(((RPGStats) player).getModifiedMaxShanks() / 60F * hungerThreshold) : hungerThreshold;
    }
}
