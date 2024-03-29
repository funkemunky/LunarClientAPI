package gg.manny.lunar.handler;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.events.AtlasListener;
import cc.funkemunky.api.events.Listen;
import cc.funkemunky.api.events.impl.PacketReceiveEvent;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInCustomPayload;
import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutCustomPayload;
import cc.funkemunky.api.utils.MiscUtils;
import gg.manny.lunar.LunarClientAPI;
import gg.manny.lunar.event.PlayerAuthenticateEvent;
import net.minecraft.util.org.apache.commons.codec.binary.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class AtlasPayload extends PacketWrapped {

    public AtlasPayload() {
        MiscUtils.printToConsole("&aImplementing Atlas " + Bukkit.getPluginManager().getPlugin("Atlas")
                .getDescription().getVersion() + " support...");

        Atlas.getInstance().getPacketProcessor().process(LunarClientAPI.getInstance(), event -> {
            WrappedInCustomPayload packet = new WrappedInCustomPayload(event.getPacket(), event.getPlayer());

            if(packet.getData().length == 0 ) return;
            String payload = new String(packet.getData(), StandardCharsets.UTF_8);

            if(payload.contains("Lunar-Client")) {
                if (!LunarClientAPI.getInstance().getPlayers().contains(event.getPlayer().getUniqueId())) {
                    LunarClientAPI.getInstance().getPlayers().add(event.getPlayer().getUniqueId());

                    event.getPlayer().sendMessage(LunarClientAPI.getInstance().getAuthMessage());
                    LunarClientAPI.getInstance().getServer().getPluginManager()
                            .callEvent(new PlayerAuthenticateEvent(event.getPlayer()));

                }
            }
        }, Packet.Client.CUSTOM_PAYLOAD);
    }

    @Override
    public void sendPayload(Player player) {
        TinyProtocolHandler.sendPacket(player, new WrappedOutCustomPayload(ProtocolVersion.getGameVersion()
                .isOrAbove(ProtocolVersion.V1_13) ? "register" : "REGISTER", "Lunar-Client"
                .getBytes()));
    }
}
