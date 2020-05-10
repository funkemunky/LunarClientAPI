package gg.manny.lunar.listener;

import gg.manny.lunar.handler.PacketWrapped;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NormalListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PacketWrapped.getInstance().sendPayload(player);
    }
}
