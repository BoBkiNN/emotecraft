package com.kosmx.emotes.mixin;

import com.kosmx.emotes.mixinInterface.EmotePlayerInterface;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public class PlayerRendererMixin {


    @Inject(method = "setupTransforms", at = @At("RETURN"))
    private void setRotation(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, float a, float bodyYaw, float tickDelta, CallbackInfo info){
        if(EmotePlayer.isRunningEmote(((EmotePlayerInterface) abstractClientPlayerEntity).getEmote())){
            EmotePlayer emote = ((EmotePlayerInterface) abstractClientPlayerEntity).getEmote();
            emote.setTickDelta(tickDelta);

            Vec3d vec3d = emote.torso.getBodyOffset();
            matrixStack.translate(vec3d.getX(), vec3d.getY() + 0.7, vec3d.getZ());
            Vector3f vec3f = emote.torso.getBodyRotation();
            matrixStack.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion(vec3f.getZ()));    //roll
            matrixStack.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(vec3f.getY()));    //pitch
            matrixStack.multiply(Vector3f.POSITIVE_X.getRadialQuaternion(vec3f.getX()));    //yaw
            matrixStack.translate(0, - 0.7d, 0);
        }
    }
}