package btw.community.arminias.rpg.mixin;

import btw.community.arminias.rpg.RPGStats;
import btw.util.hardcorespawn.HardcoreSpawnUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.SharedMonsterAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HardcoreSpawnUtils.class)
public class HardcoreSpawnUtilsMixin {
    @Inject(method = "onSoftRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/FoodStats;setFoodLevel(I)V", shift = At.Shift.AFTER))
    private static void handleHardcoreSpawnMixin(EntityPlayerMP oldPlayer, EntityPlayerMP newPlayer, CallbackInfo ci) {
        newPlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((((RPGStats) newPlayer).getModifiedMaxHealth()));
        newPlayer.setHealth(Math.min(
                Math.max((newPlayer.getMaxHealth() * 0.5F), newPlayer.getHealth()),
                newPlayer.getMaxHealth()
            ));

        newPlayer.getFoodStats().setFoodLevel(
                (int) Math.min(
                    Math.max((int) (((RPGStats) newPlayer).getModifiedMaxShanks() * 0.5F), newPlayer.getFoodStats().getFoodLevel()),
                        ((RPGStats) newPlayer).getModifiedMaxShanks()
                )
        );
    }
}
