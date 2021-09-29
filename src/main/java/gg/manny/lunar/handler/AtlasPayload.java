package gg.manny.lunar.handler;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.tinyprotocol.listener.functions.PacketListener;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInCustomPayload;
import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutCustomPayload;
import cc.funkemunky.api.utils.MiscUtils;
import gg.manny.lunar.LunarClientAPI;
import gg.manny.lunar.event.PlayerAuthenticateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;

public class AtlasPayload extends PacketWrapped {

    public AtlasPayload() {
        MiscUtils.printToConsole("&aImplementing Atlas " + Bukkit.getPluginManager().getPlugin("Atlas")
                .getDescription().getVersion() + " support...");
    }

    public PacketListener listen = Atlas.getInstance().getPacketProcessor()
            .process(LunarClientAPI.INSTANCE, event -> {
                WrappedInCustomPayload packet = new WrappedInCustomPayload(event.getPacket(), event.getPlayer());

                if(packet.getData().length == 0 ) return true;
                String payload = new String(packet.getData(), StandardCharsets.UTF_8);

                if(payload.contains("Lunar-Client")) {
                    if (!LunarClientAPI.INSTANCE.getPlayers().contains(event.getPlayer().getUniqueId())) {
                        LunarClientAPI.INSTANCE.getPlayers().add(event.getPlayer().getUniqueId());

                        event.getPlayer().sendMessage(LunarClientAPI.INSTANCE.getAuthMessage());
                        LunarClientAPI.INSTANCE.getServer().getPluginManager()
                                .callEvent(new PlayerAuthenticateEvent(event.getPlayer()));

                    }
                }
                return true;
            }, Packet.Client.CUSTOM_PAYLOAD);

    /**
     * Sends payload using Atlas 1.10.0 wrappers.
     * @param player
     */
    @Override
    public void sendPayload(Player player) {
        TinyProtocolHandler.sendPacket(player, new WrappedOutCustomPayload("REGISTER", "Lunar-Client".getBytes()));
    }

    /**
     * Removing all instances of Atlas within class.
     */
    @Override
    public void onShutdown() {
        Atlas.getInstance().getPacketProcessor().removeListener(listen);
    }
}
