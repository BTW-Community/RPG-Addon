package btw.community.arminias.rpg.mixin;

import btw.community.arminias.rpg.RPGStats;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiIngame;
import net.minecraft.src.MathHelper;
import net.minecraft.src.ScaledResolution;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GuiIngame.class)
public abstract class GuiIngameMixin {
    private int times_added = 0;
    private int renderWidth = 0;
    @Shadow @Final private Minecraft mc;


    @ModifyVariable(method = "drawFoodOverlay", ordinal = 1,
            at = @At(value = "LOAD", ordinal = 0), slice = @Slice(from = @At(value = "CONSTANT", args = "intValue=10", ordinal = 0)))
    private int modifyFoodOverlay2(int original_y) {
        return original_y - (times_added / 10) * 10;
    }

    @ModifyVariable(method = "drawFoodOverlay", ordinal = 0,
            at = @At(value = "LOAD", ordinal = 0), slice = @Slice(from = @At(value = "CONSTANT", args = "intValue=10", ordinal = 0)))
    private int modifyFoodOverlay3(int original_x) {
        int ret = original_x + (times_added++ / 10) * 80;
        times_added = ((times_added - 1) % 10) + 1;
        return ret;
    }

    @ModifyConstant(method = "drawFoodOverlay", constant = @Constant(intValue = 10, ordinal = 0))
    private int modifyFoodOverlay(int original) {
        return (int) (((RPGStats)mc.thePlayer).getModifiedMaxShanks() / 6F);
    }

    @Inject(method = "drawFoodOverlay", at = @At("RETURN"))
    private void resetTimesAdded(CallbackInfo ci) {
        times_added = 0;
    }

    @ModifyConstant(method = "renderGameOverlay", constant = @Constant(intValue = 10, ordinal = 0),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/src/Profiler;endStartSection(Ljava/lang/String;)V", ordinal = 0)))
    private int modifyHealthOverlay(int original) {
        return Math.max((int) (((RPGStats)mc.thePlayer).getModifiedMaxHealth() / 2F), original);
    }

    @Shadow
    public abstract void renderGameOverlay(float par1, boolean par2, int par3, int par4);


    @Inject(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ScaledResolution;getScaledWidth()I", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private void getThatRenderWidthLocal(float par1, boolean par2, int par3, int par4, CallbackInfo ci, ScaledResolution var5) {
        renderWidth = var5.getScaledWidth();
    }


    @ModifyVariable(method = "renderGameOverlay",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldInfo;isHardcoreModeEnabled()Z"),
            name = "var29",
            print = false,
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/src/Profiler;endStartSection(Ljava/lang/String;)V", ordinal = 0),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/src/Profiler;endStartSection(Ljava/lang/String;)V", ordinal = 1),
                    id = "modifyHealthOverlay2"
            ))
    private int modifyHealthOverlay2(int original) {
        return original - (times_added / 10) * 10;
    }

    @ModifyVariable(method = "renderGameOverlay",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldInfo;isHardcoreModeEnabled()Z"),
            name = "var28",
            print = false,
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/src/Profiler;endStartSection(Ljava/lang/String;)V", ordinal = 0),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/src/Profiler;endStartSection(Ljava/lang/String;)V", ordinal = 1),
                    id = "modifyHealthOverlay2"
            ))
    private int modifyHealthOverlay3(int original) {
        return times_added >= (int) (((RPGStats) mc.thePlayer).getModifiedMaxHealth() / 2F) ? renderWidth : original - (times_added++ / 10) * 80;
    }

    @ModifyVariable(method = "renderGameOverlay",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldInfo;isHardcoreModeEnabled()Z"),
            name = "var23",
            print = false,
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/src/Profiler;endStartSection(Ljava/lang/String;)V", ordinal = 0),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/src/Profiler;endStartSection(Ljava/lang/String;)V", ordinal = 1),
                    id = "modifyHealthOverlay2"
            ))
    private int modifyHealthOverlay4(int original) {
        return times_added >= 10 ? 0 : original;
    }

    @ModifyVariable(method = "renderGameOverlay",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityClientPlayerMP;getTotalArmorValue()I"),
            name = "var22",
            print = false)
    private int modifyHealthOverlay5(int original) {
        int i =  (int) (((RPGStats) mc.thePlayer).getModifiedMaxHealth() / 2F) - 10;
        // Ceil i to next larger multiple of 10
        i = (i + 9) / 10 * 10;
        return original - i;
    }

    @Inject(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Profiler;endStartSection(Ljava/lang/String;)V", ordinal = 1))
    private void resetTimesAdded2(CallbackInfo ci) {
        times_added = 0;
    }
}
