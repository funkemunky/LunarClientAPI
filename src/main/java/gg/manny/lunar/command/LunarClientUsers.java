package gg.manny.lunar.command;

import gg.manny.lunar.LunarClientAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

public class LunarClientUsers extends Command {

    public LunarClientUsers() {
        super("lunarclientusers");

        setAliases(Arrays.asList("lcusers", "lcs", "lcu", "clients", "users"));
        setUsage("/<command>");
        setPermissionMessage(org.bukkit.ChatColor.RED + "No permission.");
        setPermission("lunar.command.users");
    }

    private TextComponent usersString = new TextComponent(new ComponentBuilder("Lunar Client Users: ")
            .color(ChatColor.BLUE).create());
    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return true;

        TextComponent message = new TextComponent(usersString.duplicate());

        synchronized (LunarClientAPI.INSTANCE.getPlayers()) {
            message.addExtra(new TextComponent(TextComponent
                    .fromLegacyText(LunarClientAPI.INSTANCE.getPlayers().stream()
                    .map(uuid -> org.bukkit.ChatColor.YELLOW + Bukkit.getPlayer(uuid).getName())
                    .collect(Collectors.joining(ChatColor.GRAY + ", ")))));

            if(sender instanceof Player) {
                ((Player)sender).spigot().sendMessage(message);
            } else sender.sendMessage(TextComponent.toLegacyText(message));
        }

        return false;
    }
}
