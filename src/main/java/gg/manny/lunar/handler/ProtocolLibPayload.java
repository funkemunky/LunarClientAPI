package gg.manny.lunar.handler;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import gg.manny.lunar.LunarClientAPI;
import gg.manny.lunar.event.PlayerAuthenticateEvent;
import net.minecraft.util.org.apache.commons.codec.binary.StringUtils;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

public class ProtocolLibPayload extends PacketWrapped {

    public ProtocolLibPayload() {
        System.out.println("Implementing ProtocolLib support...");
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
                LunarClientAPI.getInstance(), PacketType.Play.Client.CUSTOM_PAYLOAD) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer container = new PacketContainer(PacketType.Play.Client.CUSTOM_PAYLOAD);

                byte[] data = container.getByteArrays().read(0);

                if(data.length == 0) return;

                String payload = new String(data, StandardCharsets.UTF_8);

                if(payload.contains("Lunar-Client")) {
                    if (!LunarClientAPI.getInstance().getPlayers().contains(event.getPlayer().getUniqueId())) {
                        LunarClientAPI.getInstance().getPlayers().add(event.getPlayer().getUniqueId());

                        event.getPlayer().sendMessage(LunarClientAPI.getInstance().getAuthMessage());
                        LunarClientAPI.getInstance().getServer().getPluginManager()
                                .callEvent(new PlayerAuthenticateEvent(event.getPlayer()));

                    }
                }
            }
        });
    }

    @Override
    public void sendPayload(Player player) {
        PacketContainer outCustomPayload = new PacketContainer(PacketType.Play.Server.CUSTOM_PAYLOAD);

        outCustomPayload.getStrings().write(0, "REGISTER");
        outCustomPayload.getByteArrays().write(0, "Lunar-Client".getBytes());

        //TODO Needs testing to see if it errors or not.
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, outCustomPayload);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
