package gg.manny.lunar;

import gg.manny.lunar.command.CheckCommand;
import gg.manny.lunar.handler.PacketWrapped;
import gg.manny.lunar.listener.PlayerListener;
import gg.manny.lunar.util.ReflectionUtil;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class LunarClientAPI extends JavaPlugin {

    @Getter
    public static LunarClientAPI instance;

    private final List<UUID> players = new ArrayList<>();

    private boolean restrict;
    private String kickMessage;
    private String authMessage;

    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        restrict = getConfig().getBoolean("restrict", false);
        kickMessage = ChatColor.translateAlternateColorCodes('&', getConfig()
                .getString("kick-message", "&cYou must use Lunar Client to connect to this server."));
        authMessage = ChatColor.translateAlternateColorCodes('&', getConfig()
                .getString("authenticate", " \n&aYou have connected to the server with &lLunar Client&a.\n "));

        ReflectionUtil.registerCommand(this, new CheckCommand(this));
        PacketWrapped.getInstance();
    }

    public void onDisable() {
        this.saveConfig();
    }

    public boolean onClient(Player player) {
        return players.contains(player.getUniqueId());
    }


}
