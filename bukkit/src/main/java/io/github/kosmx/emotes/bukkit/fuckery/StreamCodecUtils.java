package io.github.kosmx.emotes.bukkit.fuckery;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;

@SuppressWarnings({"unchecked", "deprecation"})
public class StreamCodecUtils {
    protected static final MethodHandles.Lookup TRUSTED_LOOKUP;

    static {
        try {
            Field hackfield = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            TRUSTED_LOOKUP = (MethodHandles.Lookup) UnsafeAccess.UNSAFE.getObject(
                    UnsafeAccess.UNSAFE.staticFieldBase(hackfield),
                    UnsafeAccess.UNSAFE.staticFieldOffset(hackfield)
            );
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void replaceFallback(StreamCodec<? extends ByteBuf, ? extends Packet<?>> codec, CustomPacketPayload.FallbackProvider<?> provider) throws ReflectiveOperationException {
        VarHandle varHandle = TRUSTED_LOOKUP.findVarHandle(codec.getClass(), "val$fallbackProvider", CustomPacketPayload.FallbackProvider.class);
        varHandle.set(codec, provider);
    }

    public static StreamCodec<? extends ByteBuf, ? extends Packet<?>> getThis(StreamCodec<? extends ByteBuf, ? extends Packet<?>> codec) throws ReflectiveOperationException {
        VarHandle varHandle = TRUSTED_LOOKUP.findVarHandle(codec.getClass(), "this$0", StreamCodec.class);
        return (StreamCodec<? extends ByteBuf, ? extends Packet<?>>) varHandle.get(codec);
    }
}
