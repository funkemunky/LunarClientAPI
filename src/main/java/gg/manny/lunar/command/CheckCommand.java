package gg.manny.lunar.command;

import gg.manny.lunar.LunarClientAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CheckCommand extends Command {

    public CheckCommand() {
        super("check");

        this.setPermission("lunar.command.check");
        this.setPermissionMessage(ChatColor.RED + "No permission.");
        this.setUsage("/<command> <playerName>");
        this.setAliases(Arrays.asList("lc", "lcheck"));
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!testPermission(sender)) return true;

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.usageMessage);
            return true;
        }

        Player player = LunarClientAPI.INSTANCE.getServer().getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Player " + args[0] + " not found.");
            return true;
        }

        boolean client = LunarClientAPI.INSTANCE.onClient(player);
        sender.sendMessage((client ? ChatColor.GREEN : ChatColor.RED) + player.getName() + " is "
                + (client ? "using" : "not using") + " lunar client.");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {

        if(args.length > 0) {
            List<String> playerNames = Bukkit.getOnlinePlayers().stream().map(Player::getName)
                    .collect(Collectors.toList());
            return StringUtil.copyPartialMatches(args[0], playerNames, new ArrayList<>());
        }

        return super.tabComplete(sender, alias, args);
    }
}
