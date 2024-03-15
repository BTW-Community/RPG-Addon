package btw.community.arminias.rpg;

import btw.AddonHandler;
import btw.BTWAddon;
import btw.network.packet.handler.CustomPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.NetServerHandler;
import net.minecraft.src.Packet250CustomPayload;

import java.io.*;

public class RPGAddon extends BTWAddon {
    private static RPGAddon instance;

    public static RPGPointsAllocation pointsAllocation = null;

    public static final float BASE_HP = 10;
    public static final float BASE_SHANKS = 40;
    public static final float BASE_MOVEMENT_SPEED = 0.088889f;
    public static final float BASE_HUNGER_DROP_RATE = 1.5f;
    public static final float BASE_BLOCK_BREAK_SPEED = 0.6667f;

    public static final int MIN_HP_POINTS = -4;
    public static final int MIN_SHANKS_POINTS = -8;
    public static final int MIN_MOVEMENT_SPEED_POINTS = -19;
    public static final int MIN_HUNGER_DROP_RATE_POINTS = -19;
    public static final int MIN_BLOCK_BREAK_SPEED_POINTS = -19;

    public static final float HP_PER_POINT = 2;
    public static final float SHANKS_PER_POINT = 4;
    public static final float MOVEMENT_SPEED_PER_POINT = 0.025f * BASE_MOVEMENT_SPEED;
    public static final float HUNGER_DROP_RATE_PER_POINT = 0.0397355f;
    public static final float BLOCK_BREAK_SPEED_PER_POINT = 0.0413745f;


    private RPGAddon() {
        super("RPG Addon", "0.2.2", "RPG");
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
        registerPacketHandler("RPG|StatsC", packet -> {
            // 1.5
            // Client-Side: You get a packet with the stats of the player
            DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
            ((RPGStats) Minecraft.getMinecraft().thePlayer).doReinit(new RPGPointsAllocation(data));
        });
    }

    @Override
    public boolean serverCustomPacketReceived(NetServerHandler handler, Packet250CustomPayload packet) {
        //2.5
        if (packet.channel.equals("RPG|StatsS")) {
            DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
            RPGPointsAllocation points = new RPGPointsAllocation(data);
            RPGStats stats = (RPGStats) handler.playerEntity;
            if (stats != null && stats.getAllocation() == null) {
                stats.doReinit(points);
                handler.playerEntity.setEntityHealth(
                        (int) Math.min(handler.playerEntity.getHealth(), stats.getModifiedMaxHealth())
                );
                handler.playerEntity.foodStats.setFoodLevel(
                        (int) Math.min(handler.playerEntity.foodStats.getFoodLevel(), stats.getModifiedMaxShanks())
                );
                sendStatAllocationToPlayer(handler, (EntityPlayerMP) stats);
            }
            return true;
        }
        return false;
    }

    @Override
    public void serverPlayerConnectionInitialized(NetServerHandler serverHandler, EntityPlayerMP playerMP) {
        //1
        sendStatAllocationToPlayer(serverHandler, playerMP);
    }

    public static void sendStatAllocationToPlayer(NetServerHandler serverHandler, EntityPlayerMP playerMP) {
        RPGStats stats = (RPGStats) playerMP;
        if (stats.getAllocation() != null) {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            DataOutputStream dataStream = new DataOutputStream(byteStream);
            stats.getAllocation().writeToOutputStream(dataStream);
            byte[] data = byteStream.toByteArray();
            Packet250CustomPayload packet = new Packet250CustomPayload("RPG|StatsC", data);
            serverHandler.sendPacketToPlayer(packet);
        }
    }

    public static RPGAddon getInstance() {
        if (instance == null)
            instance = new RPGAddon();
        return instance;
    }

}
