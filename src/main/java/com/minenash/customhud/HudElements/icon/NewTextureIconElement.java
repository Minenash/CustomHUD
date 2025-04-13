package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.CustomHud;
import com.minenash.customhud.HudElements.interfaces.ExecuteElement;
import com.minenash.customhud.conditionals.ExpressionParser;
import com.minenash.customhud.conditionals.Operation;
import com.minenash.customhud.data.Flags;
import com.minenash.customhud.render.RenderPiece;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.Optional;

public class NewTextureIconElement extends IconElement {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final Identifier TEXTURE_NOT_FOUND = Identifier.of("textures/item/barrier.png");

    private final Identifier texture;
    private final Operation u;
    private final Operation v;
    private final int textureWidth;
    private final int textureHeight;
    private final Operation regionWidth;
    private final Operation regionHeight;
    private final Operation width;
    private final Operation height;
    private final int textWidth;

    private final boolean iconAvailable;


    public NewTextureIconElement(Identifier texture, Operation u, Operation v, Operation w, Operation h, Operation width, Operation height, Flags flags) {
        super(flags, 0);
        this.u = u != null ? u : new Operation.Literal(0);
        this.v = v != null ? v : new Operation.Literal(0);
        this.width = width;
        this.height = height;

        NativeImage img = null;
        try {
            Optional<Resource> resource = client.getResourceManager().getResource(texture);
            if (resource.isPresent())
                img = NativeImage.read(resource.get().getInputStream());
        }
        catch (IOException e) { CustomHud.LOGGER.catching(e); }


        iconAvailable = img != null;
        this.texture = iconAvailable ? texture : TEXTURE_NOT_FOUND;

        textureWidth = iconAvailable ? img.getWidth() : 16;
        textureHeight = iconAvailable ? img.getHeight() : 16;
        regionWidth = w != null ? w : new Operation.BiMathOperation(new Operation.Literal(textureWidth), this.u, ExpressionParser.MathOperator.SUBTRACT);
        regionHeight = h != null ? h : new Operation.BiMathOperation(new Operation.Literal(textureHeight), this.v, ExpressionParser.MathOperator.SUBTRACT);;

        textWidth = flags.iconWidth;

    }

    @Override
    public Number getNumber() {
        return 0;
    }

    @Override
    public boolean getBoolean() {
        return true;
    }

    @Override
    public int getTextWidth() {
        return (int) (textWidth != -1 ? textWidth
                : width != null ? width.getValue()
                : height() * regionWidth.getValue()/regionHeight.getValue() );
    }

    public double height() {
        return height != null ? height.getValue() : 11 * scale;
    }

    public boolean isIconAvailable() {
        return iconAvailable;
    }

    @Override
    public void render(DrawContext context, RenderPiece piece) {
        if (calcWidth == 0 || calcHeight == 0)
            return;
        context.getMatrices().push();
        context.getMatrices().translate(piece.x+shiftX, piece.y+shiftY-2 - (referenceCorner? 0 : (calcHeight*scale-calcHeight)/(scale*2)), 0);
        rotate(context.getMatrices(), calcWidth, calcHeight);
        context.drawTexture(RenderLayer::getGuiTexturedOverlay, texture, 0, 0,  calcU, calcV, calcWidth, calcHeight, (int) calcRegionWidth, (int) calcRegionHeight, textureWidth, textureHeight);
        context.getMatrices().pop();
    }

    int calcU = 0;
    int calcV = 0;
    double calcRegionWidth = 0;
    double calcRegionHeight = 0;
    int calcWidth = 0;
    int calcHeight = 0;

    public void calculate() {
        calcU = (int) u.getValue();
        calcV = (int) v.getValue();
        calcRegionWidth = regionWidth.getValue();
        calcRegionHeight = regionHeight.getValue();
        calcHeight = (int) (height != null ? height.getValue() : 11 * scale);
        calcWidth = (int) (width != null ? width.getValue() : calcHeight * calcRegionWidth/calcRegionHeight);
    }
}
