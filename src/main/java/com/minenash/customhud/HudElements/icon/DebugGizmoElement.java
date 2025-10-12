package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.data.Flags;
import com.minenash.customhud.mixin.accessors.DebugHudAccessor;
import com.minenash.customhud.render.RenderPiece;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.DynamicUniforms;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.OptionalDouble;
import java.util.OptionalInt;

import static com.minenash.customhud.CustomHud.CLIENT;

// No longer works
public class DebugGizmoElement extends IconElement {

    private final float size;

    public DebugGizmoElement(Flags flags) {
        super(flags, 10);
        this.size = (int)(10*scale) / 2F;
    }

    @Override
    public void render(DrawContext context, RenderPiece piece) {
        float scale = -1 * this.scale * 10/18f;
        Camera camera = CLIENT.gameRenderer.getCamera();
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
//        matrix4fStack.mul(context.getMatrices().peek().getPositionMatrix());
        matrix4fStack.rotateX(-camera.getPitch() * (float) (Math.PI / 180.0));
        matrix4fStack.rotateY(camera.getYaw() * (float) (Math.PI / 180.0));
        matrix4fStack.scale(scale, scale, scale);
        CLIENT.getDebugHud().renderDebugCrosshair(camera);
        matrix4fStack.popMatrix();
    }



}
