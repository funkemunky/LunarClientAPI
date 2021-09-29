package gg.manny.lunar.listener;

import gg.manny.lunar.LunarClientAPI;
import gg.manny.lunar.handler.PacketHandler;
import gg.manny.lunar.util.ReflectionUtil;
import net.minecraft.util.io.netty.buffer.ByteBuf;
import net.minecraft.util.io.netty.buffer.Unpooled;
import net.minecraft.util.io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Constructor;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static gg.manny.lunar.util.ReflectionUtil.getChannel;

public class PlayerListener implements Listener {

    private final Executor INJECT_EXECUTOR = Executors.newSingleThreadExecutor();
    private final Executor EJECT_EXECUTOR = Executors.newSingleThreadExecutor(); //when u gotta pull out faster than ygore

    public PlayerListener() {
        System.out.println("Implementing 1.7.10 only support...");
        if(!Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]
                .contains("1_7")) {
            Bukkit.getLogger().severe("Without Atlas or ProtocolLib installed, " +
                    "you can only run LunarClientAPI on 1.7.10.");
            Bukkit.getPluginManager().disablePlugin(LunarClientAPI.INSTANCE);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        INJECT_EXECUTOR.execute(() -> {
            // ¯\_(ツ)_/¯
            try {
                Constructor constructor = ReflectionUtil.getClass("PacketPlayOutCustomPayload").getConstructor(String.class, ByteBuf.class);
                Constructor serializerConstructor = ReflectionUtil.getClass("PacketDataSerializer").getConstructor(ByteBuf.class);
                Object packet = constructor.newInstance("REGISTER", serializerConstructor.newInstance(Unpooled.wrappedBuffer("Lunar-Client".getBytes())));
                ReflectionUtil.sendPacket(player, packet);
                Channel channel = getChannel(player);
                if (channel != null) {
                    channel.pipeline().addBefore("packet_handler", "manny", new PacketHandler.PacketListener(player));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        LunarClientAPI.INSTANCE.getPlayers().remove(player.getUniqueId());

        EJECT_EXECUTOR.execute(() -> {
            try {
                Channel channel = getChannel(player);
                if (channel != null && channel.pipeline().get("manny") != null) {
                    channel.pipeline().remove("manny");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}