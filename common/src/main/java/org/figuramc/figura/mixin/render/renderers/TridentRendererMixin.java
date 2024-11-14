package org.figuramc.figura.mixin.render.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ThrownTridentRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import org.figuramc.figura.FiguraMod;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.ducks.FiguraEntityRenderStateExtension;
import org.figuramc.figura.ducks.FiguraProjectileRenderStateExtension;
import org.figuramc.figura.lua.api.entity.EntityAPI;
import org.figuramc.figura.permissions.Permissions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownTridentRenderer.class)
public abstract class TridentRendererMixin<T extends ThrownTrident, S extends ThrownTridentRenderState> extends EntityRenderer<T, S> {

    protected TridentRendererMixin(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Inject(at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;getFoilBuffer(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;ZZ)Lcom/mojang/blaze3d/vertex/VertexConsumer;"), method = "render(Lnet/minecraft/client/renderer/entity/state/ThrownTridentRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", cancellable = true)
    private void render(ThrownTridentRenderState thrownTridentRenderState, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, CallbackInfo ci) {
        Projectile trident = (Projectile) Minecraft.getInstance().level.getEntity(((FiguraEntityRenderStateExtension)thrownTridentRenderState).figura$getEntityId());
        float tickDelta = ((FiguraProjectileRenderStateExtension)thrownTridentRenderState).figura$getTickDelta();

        Entity owner = trident.getOwner();
        if (owner == null)
            return;

        Avatar avatar = AvatarManager.getAvatar(owner);
        if (avatar == null || avatar.permissions.get(Permissions.VANILLA_MODEL_EDIT) == 0)
            return;

        FiguraMod.pushProfiler(FiguraMod.MOD_ID);
        FiguraMod.pushProfiler(avatar);
        FiguraMod.pushProfiler("tridentRender");

        FiguraMod.pushProfiler("event");
        boolean bool = avatar.tridentRenderEvent(tickDelta, EntityAPI.wrap(trident));

        FiguraMod.popPushProfiler("render");
        if (bool || avatar.renderTrident(poseStack, multiBufferSource, tickDelta, light)) {
            poseStack.popPose();
            ci.cancel();
        }

        FiguraMod.popProfiler(4);
    }

    @Inject(at = @At("HEAD"), method = "extractRenderState(Lnet/minecraft/world/entity/projectile/ThrownTrident;Lnet/minecraft/client/renderer/entity/state/ThrownTridentRenderState;F)V")
    void appendFiguraProperties(T trident, S tridentState, float f, CallbackInfo ci) {
        ((FiguraProjectileRenderStateExtension)tridentState).figura$setTickDelta(f);
    }
}
