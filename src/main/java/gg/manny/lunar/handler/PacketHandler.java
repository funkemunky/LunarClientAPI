package gg.manny.lunar.handler;

import gg.manny.lunar.LunarClientAPI;
import gg.manny.lunar.event.PlayerAuthenticateEvent;
import gg.manny.lunar.util.ReflectionUtil;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.io.netty.buffer.ByteBuf;
import net.minecraft.util.io.netty.buffer.Unpooled;
import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelDuplexHandler;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.org.apache.commons.codec.binary.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static gg.manny.lunar.util.ReflectionUtil.getChannel;

public class PacketHandler extends PacketWrapped implements Listener {

    private final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private Constructor<?> constructor, serializerConstructor;

    public PacketHandler() {
        try {
            constructor = ReflectionUtil.getClass("PacketPlayOutCustomPayload")
                    .getConstructor(String.class, ByteBuf.class);
            serializerConstructor = ReflectionUtil.getClass("PacketDataSerializer")
                    .getConstructor(ByteBuf.class);
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends payload using internal reflection for packets
     * @param player
     */
    @Override
    public void sendPayload(Player player) {
        try {
            Object packet = constructor.newInstance("REGISTER", serializerConstructor
                    .newInstance(Unpooled.wrappedBuffer("Lunar-Client".getBytes())));

            ReflectionUtil.sendPacket(player, packet);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                | NoSuchFieldException | ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removing all players with registered channel "manny" and unregistering this class's Bukkit listeners.
     */
    @Override
    public void onShutdown() {
        HandlerList.unregisterAll(this);
        EXECUTOR.shutdownNow();
        Bukkit.getOnlinePlayers().forEach(pl -> {
            try {
                Channel channel = getChannel(pl);
                if (channel != null && channel.pipeline().get("manny") != null) {
                    channel.pipeline().remove("manny");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        EXECUTOR.execute(() -> {
            try {
                Channel channel = getChannel(event.getPlayer());
                if (channel != null) {
                    channel.pipeline().addBefore("packet_handler", "manny", new PacketHandler
                            .PacketListener(event.getPlayer()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLeave(PlayerQuitEvent event) {
        EXECUTOR.execute(() -> {
            try {
                Channel channel = getChannel(event.getPlayer());
                if (channel != null && channel.pipeline().get("manny") != null) {
                    channel.pipeline().remove("manny");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @RequiredArgsConstructor
    public static class PacketListener extends ChannelDuplexHandler {
        private final Player player;
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg.getClass().getSimpleName().equals("PacketPlayInCustomPayload")) {
                byte[] data = (byte[]) ReflectionUtil.getMethod(msg, "e");
                String payload = StringUtils.newStringUtf8(data);
                if (payload.contains("Lunar-Client")) {
                    if (!LunarClientAPI.INSTANCE.getPlayers().contains(player.getUniqueId())) {
                        LunarClientAPI.INSTANCE.getPlayers().add(player.getUniqueId());

                        player.sendMessage(LunarClientAPI.INSTANCE.getAuthMessage());
                        LunarClientAPI.INSTANCE.getServer().getPluginManager()
                                .callEvent(new PlayerAuthenticateEvent(player));

                    }
                }

            } else {
                super.channelRead(ctx, msg);
            }
        }
    }
}
