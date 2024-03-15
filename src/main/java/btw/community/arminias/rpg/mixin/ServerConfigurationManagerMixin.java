package btw.community.arminias.rpg.mixin;

import btw.community.arminias.rpg.RPGAddon;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.ServerConfigurationManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerConfigurationManager.class)
public class ServerConfigurationManagerMixin {
    @Inject(method = "respawnPlayer", at = @At("RETURN"))
    private void respawnPlayer(EntityPlayerMP oldPlayer, int iDefaultDimension, boolean bPlayerLeavingTheEnd, CallbackInfoReturnable<EntityPlayerMP> cir) {
        RPGAddon.sendStatAllocationToPlayer(cir.getReturnValue().playerNetServerHandler, cir.getReturnValue());
    }

    @Inject(method = "initializeConnectionToPlayer", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void initializeConnectionToPlayer(INetworkManager par1INetworkManager, EntityPlayerMP par2EntityPlayerMP, CallbackInfo ci, NBTTagCompound var3) {
        if (var3 == null) {
            par2EntityPlayerMP.health = Integer.MAX_VALUE;
            par2EntityPlayerMP.foodStats.setFoodLevel(Integer.MAX_VALUE);
        }
    }
}
