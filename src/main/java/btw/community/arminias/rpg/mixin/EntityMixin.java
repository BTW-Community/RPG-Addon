package btw.community.arminias.rpg.mixin;

import btw.community.arminias.rpg.RPGStats;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Entity.class)
public class EntityMixin {
    @ModifyVariable(method = "moveFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/MathHelper;sin(F)F"),
                     argsOnly = true, ordinal = 0)
    private float modifyMoveFlying(float f) {
        if ((Object) this instanceof EntityPlayer) {
            RPGStats stats = (RPGStats) (Object) this;
            return f * stats.getMovementSpeedModifier() / 0.1F;
        }
        return f;
    }

    @ModifyVariable(method = "moveFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/MathHelper;sin(F)F"),
            argsOnly = true, ordinal = 1)
    private float modifyMoveFlying2(float f) {
        if ((Object) this instanceof EntityPlayer) {
            RPGStats stats = (RPGStats) (Object) this;
            return f * stats.getMovementSpeedModifier() / 0.1F;
        }
        return f;
    }
}
