package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.data.Flags;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class DebugGizmoElement extends IconElement {

    private final int width;
    private final float size;

    public DebugGizmoElement(Flags flags) {
        super(flags);
        this.width = flags.iconWidth != -1 ? flags.iconWidth : (int)(10*scale);
        this.size = width / 2F;
    }

    @Override
    public void render(MatrixStack stack, int x, int y, float profileScale) {
        float scale = -1 * this.scale;
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
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
        matrixStack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(camera.getPitch()));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw()));

        matrixStack.scale(scale, scale, scale);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.renderCrosshair((int) size);
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
    }



    @Override
    public int getTextWidth() {
        return width;
    }
}
