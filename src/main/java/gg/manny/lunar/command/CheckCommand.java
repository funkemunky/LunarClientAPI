package gg.manny.lunar.command;

import gg.manny.lunar.LunarClientAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CheckCommand extends Command {

    private final LunarClientAPI plugin;

    public CheckCommand(LunarClientAPI plugin) {
        super("check");

        this.setPermission("lunar.command.check");
        this.setPermissionMessage(ChatColor.RED + "No permission.");
        this.setUsage("/check <playerName>");
        this.setAliases(Arrays.asList("lc", "lcheck"));

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!testPermission(sender)) return true;

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.usageMessage);
            return true;
        }

        Player player = this.plugin.getServer().getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Player " + args[0] + " not found.");
            return true;
        }

        boolean client = this.plugin.onClient(player);
        sender.sendMessage((client ? ChatColor.GREEN : ChatColor.RED) + player.getName() + " is " + (client ? "using" : "not using") + " lunar client.");
        return true;
    }
}
