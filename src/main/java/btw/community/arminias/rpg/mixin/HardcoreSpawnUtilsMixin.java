package btw.community.arminias.rpg.mixin;

import btw.community.arminias.rpg.RPGStats;
import btw.util.hardcorespawn.HardcoreSpawnUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HardcoreSpawnUtils.class)
public class HardcoreSpawnUtilsMixin {
    @Inject(method = "handleHardcoreSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/FoodStats;setFoodLevel(I)V", shift = At.Shift.AFTER))
    private static void handleHardcoreSpawnMixin(MinecraftServer server, EntityPlayerMP oldPlayer, EntityPlayerMP newPlayer, CallbackInfo ci) {
        newPlayer.health = Math.min(
                Math.max((int) (newPlayer.getMaxHealth() * 0.5F), newPlayer.health),
                newPlayer.getMaxHealth()
            );

        newPlayer.getFoodStats().setFoodLevel(
                (int) Math.min(
                    Math.max((int) (((RPGStats) newPlayer).getModifiedMaxShanks() * 0.5F), newPlayer.getFoodStats().getFoodLevel()),
                        ((RPGStats) newPlayer).getModifiedMaxShanks()
                )
        );
    }
}
