package gg.manny.lunar.handler;

import cc.funkemunky.api.utils.Init;
import gg.manny.lunar.LunarClientAPI;
import gg.manny.lunar.listener.NormalListener;
import gg.manny.lunar.listener.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class PacketWrapped {

    public abstract void sendPayload(Player player);

    static PacketWrapped INSTANCE = null;

    public static PacketWrapped getInstance() {
        if(INSTANCE == null) {
            if(Bukkit.getPluginManager().isPluginEnabled("Atlas")) {
                INSTANCE = new AtlasPayload();
                Bukkit.getPluginManager().registerEvents(new NormalListener(), LunarClientAPI.getInstance());
            } else if(Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
                INSTANCE = new ProtocolLibPayload();
                Bukkit.getPluginManager().registerEvents(new NormalListener(), LunarClientAPI.getInstance());
            } else {
                Bukkit.getPluginManager().registerEvents(new PlayerListener(), LunarClientAPI.getInstance());
            }
        }
        return INSTANCE;
    }
}
