package btw.community.arminias.rpg.mixin;

import btw.community.arminias.rpg.RPGAddon;
import btw.community.arminias.rpg.RPGPointsAllocation;
import btw.community.arminias.rpg.RPGStats;
import net.minecraft.client.Minecraft;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Packet250CustomPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

@Mixin(NetClientHandler.class)
public abstract class NetClientHandlerMixin {
    @Shadow public abstract void addToSendQueue(Packet par1Packet);

    @Shadow private Minecraft mc;

    @Inject(method = "handleLogin", at = @At("RETURN"))
    private void handleLoginInject(Packet1Login par1Packet1Login, CallbackInfo ci) {
        if (RPGAddon.pointsAllocation != null && ((RPGStats) this.mc.thePlayer).getAllocation() == null) {
            //2
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            DataOutputStream dataStream = new DataOutputStream(byteStream);
            RPGAddon.pointsAllocation.writeToOutputStream(dataStream);
            Packet250CustomPayload packet = new Packet250CustomPayload("RPG|StatsS", byteStream.toByteArray());
            this.addToSendQueue(packet);
            ((RPGStats) this.mc.thePlayer).doReinit(RPGAddon.pointsAllocation);
        } else if (RPGAddon.pointsAllocation == null && ((RPGStats) this.mc.thePlayer).getAllocation() == null) {
            //2
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            DataOutputStream dataStream = new DataOutputStream(byteStream);
            RPGPointsAllocation.defaultAllocation(RPGPointsAllocation.DEFAULT_POINTS).writeToOutputStream(dataStream);
            Packet250CustomPayload packet = new Packet250CustomPayload("RPG|StatsS", byteStream.toByteArray());
            this.addToSendQueue(packet);
            ((RPGStats) this.mc.thePlayer).doReinit(RPGPointsAllocation.defaultAllocation(RPGPointsAllocation.DEFAULT_POINTS));
        }
    }
}
