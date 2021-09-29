package gg.manny.lunar;

import gg.manny.lunar.command.CheckCommand;
import gg.manny.lunar.command.LunarClientUsers;
import gg.manny.lunar.handler.PacketHandler;
import gg.manny.lunar.handler.PacketWrapped;
import gg.manny.lunar.util.ReflectionUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

@Getter
public class LunarClientAPI extends JavaPlugin {

    public static LunarClientAPI INSTANCE;

    private final Set<UUID> players = new HashSet<>();

    private boolean restrict;
    private String kickMessage;
    private String authMessage;

    public void onEnable() {
        INSTANCE = this;

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        restrict = getConfig().getBoolean("restrict", false);
        kickMessage = ChatColor.translateAlternateColorCodes('&', getConfig()
                .getString("kick-message", "&cYou must use Lunar Client to connect to this server."));
        authMessage = ChatColor.translateAlternateColorCodes('&', getConfig()
                .getString("authenticate", " \n&aYou have connected to the server with &lLunar Client&a.\n "));

        ReflectionUtil.registerCommand(this, new CheckCommand());
        ReflectionUtil.registerCommand(this, new LunarClientUsers());
        PacketWrapped.getInstance();
    }

    public void onDisable() {
        this.saveConfig();
        PacketWrapped.getInstance().onShutdown();
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    /**
     * Checks a HashSet for a UUID. If it contains the UUID, returns the result.
     * @param player org.bukkit.entity.Player
     * @return Is the Player on Lunar client?
     */
    public boolean onClient(Player player) {
        return players.contains(player.getUniqueId());
    }


}
