package btw.community.arminias.rpg;

import btw.AddonHandler;
import btw.BTWAddon;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;

public class RPGAddon extends BTWAddon {
    private static RPGAddon instance;

    public static RPGPointsAllocation pointsAllocation = null;

    public static final float BASE_HP = 10;
    public static final float BASE_SHANKS = 30;
    public static final float BASE_MOVEMENT_SPEED = 0.088889f;
    public static final float BASE_HUNGER_DROP_RATE = 1.5f;
    public static final float BASE_BLOCK_BREAK_SPEED = 0.6667f;

    public static final int MIN_HP_POINTS = -4;
    public static final int MIN_SHANKS_POINTS = -4;
    public static final int MIN_MOVEMENT_SPEED_POINTS = -19;
    public static final int MIN_HUNGER_DROP_RATE_POINTS = -19;
    public static final int MIN_BLOCK_BREAK_SPEED_POINTS = -19;

    public static final float HP_PER_POINT = 2;
    public static final float SHANKS_PER_POINT = 6;
    public static final float MOVEMENT_SPEED_PER_POINT = 0.025f * BASE_MOVEMENT_SPEED;
    public static final float HUNGER_DROP_RATE_PER_POINT = 0.0397355f;
    public static final float BLOCK_BREAK_SPEED_PER_POINT = 0.0413745f;


    private RPGAddon() {
        super("RPG Addon", "0.3.0", "RPG");
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
        registerPacketHandler("RPG|Screen", packet -> {
            // Client-Side: You get a packet to open the stats screen
            EntityPlayer player;
            Minecraft.getMinecraft().displayGuiScreen(new GuiRPGStats(Minecraft.getMinecraft().currentScreen,
                    (player = Minecraft.getMinecraft().thePlayer) != null ? ((RPGStats) player).getAllocation() : RPGPointsAllocation.defaultAllocation(RPGPointsAllocation.DEFAULT_POINTS),
                    true));
        });
        AddonHandler.registerCommand(new CommandBase() {
            @Override
            public String getCommandName() {
                return "resetrpgstats";
            }

            @Override
            public String getCommandUsage(ICommandSender par1ICommandSender) {
                return super.getCommandUsage(par1ICommandSender) + " <player>";
            }

            @Override
            public void processCommand(ICommandSender var1, String[] var2) {
                if (var2.length == 1) {
                    EntityPlayerMP player = func_82359_c(var1, var2[0]);
                    RPGStats stats = (RPGStats) player;
                    stats.doReinit(RPGPointsAllocation.defaultAllocation(RPGPointsAllocation.DEFAULT_POINTS));
                    player.setEntityHealth((int) Math.min(player.getHealth(), stats.getModifiedMaxHealth()));
                    player.foodStats.setFoodLevel((int) Math.min(player.foodStats.getFoodLevel(), stats.getModifiedMaxShanks()));
                    sendStatAllocationToPlayer(player.playerNetServerHandler, player);
                    player.sendChatToPlayer("Your stats have been reset.");
                    var1.sendChatToPlayer("Stats for " + player.getEntityName() + " have been reset.");
                    sendStatAllocationScreenToPlayer(player);
                } else {
                    throw new WrongUsageException(getCommandUsage(var1), new Object[0]);
                }
            }

            @Override
            public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
                return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getAllUsernames()) : null;
            }

        }, false);
    }

    public static void sendStatAllocationScreenToPlayer(EntityPlayerMP player) {
        Packet250CustomPayload packet = new Packet250CustomPayload("RPG|Screen", new byte[0]);
        player.playerNetServerHandler.sendPacketToPlayer(packet);
    }

    @Override
    public boolean serverCustomPacketReceived(NetServerHandler handler, Packet250CustomPayload packet) {
        //2.5
        if (packet.channel.startsWith("RPG|StatsS")) {
            DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
            RPGPointsAllocation points = new RPGPointsAllocation(data);
            RPGStats stats = (RPGStats) handler.playerEntity;
            if (stats != null && (stats.getAllocation() == null || packet.channel.endsWith("1"))) {
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

    public static void sendStatAllocationToServer(RPGPointsAllocation pointsAllocation, boolean force) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);
        pointsAllocation.writeToOutputStream(dataStream);
        Packet250CustomPayload returnPacket = new Packet250CustomPayload("RPG|StatsS" + (force ? "1" : ""), byteStream.toByteArray());
        Minecraft.getMinecraft().getNetHandler().addToSendQueue(returnPacket);
        ((RPGStats) Minecraft.getMinecraft().thePlayer).doReinit(pointsAllocation);
    }

    public static RPGAddon getInstance() {
        if (instance == null)
            instance = new RPGAddon();
        return instance;
    }

}
