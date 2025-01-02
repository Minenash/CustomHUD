package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.ProfileManager;
import com.minenash.customhud.data.Flags;
import com.minenash.customhud.render.RenderPiece;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4fStack;

public class DebugGizmoElement extends IconElement {

    private final float size;

    public DebugGizmoElement(Flags flags) {
        super(flags, 10);
        this.size = (int)(10*scale) / 2F;
    }

    @Override
    public void render(DrawContext context, RenderPiece piece) {
        float scale = -1 * this.scale;
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
//        matrix4fStack.scale(profileScale,profileScale,1);

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

        matrix4fStack.translate(piece.x + shiftX + x_offset, piece.y + shiftY + y_offset + (size/2), 100);
        matrix4fStack.rotateX(-camera.getPitch() * (float) (Math.PI / 180.0));
        matrix4fStack.rotateY(camera.getYaw() * (float) (Math.PI / 180.0));
        matrix4fStack.scale(scale, scale, scale);
        RenderSystem.renderCrosshair((int) size);
        matrix4fStack.popMatrix();
    }

}
