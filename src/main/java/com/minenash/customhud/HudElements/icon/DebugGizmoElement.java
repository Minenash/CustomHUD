package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.CustomHud;
import com.minenash.customhud.data.Flags;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class DebugGizmoElement extends IconElement {

    private final float size;
    private final Flags flags;

    public DebugGizmoElement(Flags flags) {
        super(flags, 10);
        this.size = (int)(10*scale) / 2F;
        this.flags = flags;
    }

    //TODO: Mark as non-rotatable
    @Override
    public void render(DrawContext context, int x, int y) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.multiplyPositionMatrix(context.getMatrices().peek().getPositionMatrix());
        float profileScale = CustomHud.getActiveProfile().baseTheme.scale;
        matrixStack.scale(profileScale,profileScale,1);

        float yaw = MathHelper.wrapDegrees(camera.getYaw());
        float pitch = MathHelper.wrapDegrees(camera.getPitch());

        float x_offset = size / 2;
        float y_offset = (pitch + 90) / 180 * size * 2 - 2;
        if (y_offset > size) y_offset = size;

        if (yaw > 90) {
            x_offset += size - (((yaw - 90) / 90) * size);
            y_offset += size * (-pitch / 90);
        }
        else if (yaw > 0) {
            x_offset += size;
            y_offset += (yaw / 90) * size * (-pitch / 90);
        }
        else if (yaw < -90) {
            x_offset += 0;
            y_offset += ((yaw + 90)/90) * -size * (-pitch / 90);
        }
        else {
            x_offset += size + ((yaw) / 90) * size;
            y_offset += 0;
        }

        matrixStack.translate(x + shiftX + x_offset, y + shiftY + y_offset + (size/2), 100);
        if (!referenceCorner)
            matrixStack.translate(0, -(10*scale-10)/2, 0);
        matrixStack.scale(-1, -1, -1);

        matrixStack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(camera.getPitch()));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw()));

        RenderSystem.applyModelViewMatrix();
        RenderSystem.renderCrosshair((int) size);
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
    }

}
