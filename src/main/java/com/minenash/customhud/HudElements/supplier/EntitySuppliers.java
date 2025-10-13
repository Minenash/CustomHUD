package com.minenash.customhud.HudElements.supplier;

import com.minenash.customhud.HudElements.list.AttributeHelpers;
import com.minenash.customhud.complex.ComplexData;
import com.minenash.customhud.data.StatFormatters;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

import static com.minenash.customhud.CustomHud.CLIENT;
import static com.minenash.customhud.HudElements.supplier.NumberSupplierElement.of;

public class EntitySuppliers {
    private static Entity hooked() { return CLIENT.player.fishHook == null ? null : CLIENT.player.fishHook.getHookedEntity(); }
    private static Entity veh() { return CLIENT.player.getVehicle(); }

    public static final Supplier<String> TARGET_ENTITY = () -> type(ComplexData.targetEntity);
    public static final Supplier<Identifier> TARGET_ENTITY_ID = () -> id(ComplexData.targetEntity);
    public static final Supplier<Text> TARGET_ENTITY_NAME = () -> name(ComplexData.targetEntity);
    public static final Supplier<String> TARGET_ENTITY_UUID = () -> uuid(ComplexData.targetEntity);
    public static final NumberSupplierElement.Entry TARGET_ENTITY_X = of( () -> x(ComplexData.targetEntity), 0);
    public static final NumberSupplierElement.Entry TARGET_ENTITY_Y = of( () -> y(ComplexData.targetEntity), 0);
    public static final NumberSupplierElement.Entry TARGET_ENTITY_Z = of( () -> z(ComplexData.targetEntity), 0);
    public static final NumberSupplierElement.Entry TARGET_ENTITY_DISTANCE = of( () -> dist(ComplexData.targetEntity), 1);

    public static final Supplier<String> HOOKED_ENTITY = () -> type(hooked());
    public static final Supplier<Identifier> HOOKED_ENTITY_ID = () -> id(hooked());
    public static final Supplier<Text> HOOKED_ENTITY_NAME = () -> name(hooked());
    public static final Supplier<String> HOOKED_ENTITY_UUID = () -> uuid(hooked());
    public static final NumberSupplierElement.Entry HOOKED_ENTITY_X = of ( () -> x(hooked()), 0);
    public static final NumberSupplierElement.Entry HOOKED_ENTITY_Y = of ( () -> y(hooked()), 0);
    public static final NumberSupplierElement.Entry HOOKED_ENTITY_Z = of ( () -> z(hooked()), 0);
    public static final NumberSupplierElement.Entry HOOKED_ENTITY_DISTANCE = of( () -> dist(hooked()), 1);
    public static final NumberSupplierElement.Entry HOOKED_ENTITY_DIRECTION_YAW = of( () -> yaw(hooked()), 1);
    public static final NumberSupplierElement.Entry HOOKED_ENTITY_DIRECTION_PITCH = of( () -> pitch(hooked()), 1);


    public static final Supplier<String> LAST_HIT_ENTITY = () -> type(ComplexData.lastHitEntity);
    public static final Supplier<Identifier> LAST_HIT_ENTITY_ID = () -> id(ComplexData.lastHitEntity);
    public static final Supplier<Text> LAST_HIT_ENTITY_NAME = () -> name(ComplexData.lastHitEntity);
    public static final Supplier<String> LAST_HIT_ENTITY_UUID = () -> uuid(ComplexData.lastHitEntity);
    public static final NumberSupplierElement.Entry LAST_HIT_ENTITY_DISTANCE = of( () -> ComplexData.lastHitEntityDist, 1);
    public static final NumberSupplierElement.Entry LAST_HIT_ENTITY_AGO = of( () -> (System.currentTimeMillis() - ComplexData.lastHitEntityTime) / 1000D, 0, StatFormatters.SEC_HMS);



    public static final Supplier<String> VEHICLE_ENTITY = () -> type(veh());
    public static final Supplier<Identifier> VEHICLE_ENTITY_ID = () -> id(veh());
    public static final Supplier<Text> VEHICLE_ENTITY_NAME = () -> name(veh());
    public static final Supplier<String> VEHICLE_ENTITY_UUID = () -> uuid(veh());
    public static final NumberSupplierElement.Entry VEHICLE_ENTITY_HEALTH = of ( () -> !(veh() instanceof LivingEntity le)? null : le.getHealth(), 0);
    public static final NumberSupplierElement.Entry VEHICLE_ENTITY_MAX_HEALTH = of ( () -> !(veh() instanceof LivingEntity le)? null : le.getMaxHealth(), 0);
    public static final Supplier<Number> VEHICLE_ENTITY_ARMOR = () -> !(veh() instanceof LivingEntity le)? null : le.getArmor();
    public static final NumberSupplierElement.Entry VEHICLE_HORSE_JUMP = of ( () -> !(veh() instanceof HorseEntity)? null : CLIENT.player.getMountJumpStrength()*100, 0);
    public static final Supplier<Text> VEHICLE_HORSE_ARMOR = () -> !(veh() instanceof HorseEntity he)? null : he.getBodyArmor().getName();


    private static String type(Entity e) { return e == null ? null : I18n.translate(e.getType().getTranslationKey()); }
    private static Identifier id(Entity e) { return e == null ? null : Registries.ENTITY_TYPE.getId(e.getType()); }
    private static Text name(Entity e) { return e == null ? null : e.getDisplayName(); }
    private static String uuid(Entity e) { return e == null ? null : e.getUuidAsString(); }

    private static Number x(Entity e) { return e == null ? null : e.getX(); }
    private static Number y(Entity e) { return e == null ? null : e.getY(); }
    private static Number z(Entity e) { return e == null ? null : e.getZ(); }
    private static Number dist(Entity e) { return e == null ? null : e.getPos().distanceTo(CLIENT.cameraEntity.getPos()); }
    private static Number yaw(Entity e) { return e == null ? null : AttributeHelpers.getRelativeYaw(CLIENT.cameraEntity.getPos(), e.getEyePos()); }
    private static Number pitch(Entity e) { return e == null ? null : AttributeHelpers.getRelativePitch(CLIENT.cameraEntity.getPos(), e.getEyePos()); }
}
