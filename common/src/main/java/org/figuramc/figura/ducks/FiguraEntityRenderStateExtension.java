package org.figuramc.figura.ducks;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.UUID;

public interface FiguraEntityRenderStateExtension {
    int figura$getEntityId();
    void figura$setEntityId(int id);
    float figura$getTickDelta();
    void figura$setTickDelta(float tickDelta);
}
