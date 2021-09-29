package gg.manny.lunar.handler;

import gg.manny.lunar.LunarClientAPI;
import gg.manny.lunar.listener.NormalListener;
import gg.manny.lunar.listener.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class PacketWrapped {

    /**
     * Abstract method for sending the PacketPlayOutCustomPayload used to detect Lunar Client.
     * @param player
     */
    public abstract void sendPayload(Player player);

    /**
     * Used for clearing out PacketWrapped's listeners and other objects on shutdown.
     */
    public abstract void onShutdown();

    static PacketWrapped INSTANCE = null;

    /**
     * Gets the best possible PacketWrapped for handling Payload packets.
     * @return AtlasPayload, ProtocolLibPaylod, PacketHandler
     */
    public static PacketWrapped getInstance() {
        if(INSTANCE == null) {
            if(Bukkit.getPluginManager().isPluginEnabled("Atlas") && !LunarClientAPI.INSTANCE.getConfig()
                    .getBoolean("force-protocollib")) {
                INSTANCE = new AtlasPayload();
                Bukkit.getPluginManager().registerEvents(new NormalListener(), LunarClientAPI.INSTANCE);
            } else if(Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
                INSTANCE = new ProtocolLibPayload();
                Bukkit.getPluginManager().registerEvents(new NormalListener(), LunarClientAPI.INSTANCE);
            } else {
                Bukkit.getPluginManager().registerEvents(new PacketHandler(), LunarClientAPI.INSTANCE);
            }
        }
        return INSTANCE;
    }
}
