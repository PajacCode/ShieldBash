package com.omircon.shield_bash;

import com.omircon.shield_bash.network.Network;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.Mth;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ShieldBash.MODID)
public class ShieldBash
{

    public static final String MODID = "shield_bash";
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();




    public ShieldBash() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ShieldBash::commonSetup);
        registerEvents();
        //Network.registerMessages();
        //LOGGER.info("siusiak2");
        Registries.ENCHANTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Registries.GLM.register(FMLJavaModLoadingContext.get().getModEventBus());




    }

    private static void registerEvents()
    {

        MinecraftForge.EVENT_BUS.addListener(ShieldBash::knockbackEvent);
        MinecraftForge.EVENT_BUS.addListener(ShieldBash::livingAttack);
        //ClientEvents.setup();
    }

    public static void livingAttack(LivingAttackEvent event)
    {
        if(!event.getSource().isProjectile() && !event.getSource().isMagic() && !event.getSource().isExplosion())
        if(event.getSource().getEntity() != null && !event.getEntity().getCommandSenderWorld().isClientSide())
        {

            Entity pAttacker = event.getSource().getEntity();
            Entity pUser = event.getEntity();
            //System.out.println(pUser.getType()); //source
            //System.out.println(event.getEntity().getType()); // target

            if(pUser instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer) pUser;
                if(player.isUsingItem() && player.getUseItem().getItem() == Items.SHIELD)
                {
                    int i = EnchantmentHelper.getItemEnchantmentLevel(Registries.SPIKES.get(), player.getUseItem());
                    System.out.println(i);
                    if(i>0)
                    if (pAttacker != null) {
                        pAttacker.hurt(DamageSource.thorns(pUser), i);
                    }
                }
            }
        }
    }

    public static void knockbackEvent(LivingHurtEvent event)
    {
        if(event.getSource().getEntity() != null)
            if(event.getSource().getEntity() instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer) event.getSource().getEntity();
                if(player.isUsingItem() && player.getUseItem().getItem() == Items.SHIELD)
                {
                    int j = EnchantmentHelper.getItemEnchantmentLevel(Registries.SPIKES.get(), player.getUseItem());
                    float f = j+1;
                    boolean flag2 = player.fallDistance > 0.0F && !player.isOnGround() && !player.onClimbable() && !player.isInWater() && !player.hasEffect(MobEffects.BLINDNESS) && !player.isPassenger() && event.getEntity() instanceof LivingEntity;
                    flag2 = flag2 && !player.isSprinting();
                    if (flag2) {
                        f *= 1.5;
                    }
                    event.setAmount(f);
                    int i = EnchantmentHelper.getItemEnchantmentLevel(Registries.GUARD.get(), player.getUseItem());
                    if(i > 0) {
                        Entity pTargetEntity = event.getEntity();

                        if (pTargetEntity instanceof LivingEntity) {
                            ((LivingEntity) pTargetEntity).knockback((float) i * 0.5F, (double) Mth.sin(player.getYRot() * ((float) Math.PI / 180F)), (double) (-Mth.cos(player.getYRot() * ((float) Math.PI / 180F))));
                        } else {
                            pTargetEntity.push((double) (-Mth.sin(player.getYRot() * ((float) Math.PI / 180F)) * (float) i * 0.5F), 0.1D, (double) (Mth.cos(player.getYRot() * ((float) Math.PI / 180F)) * (float) i * 0.5F));
                        }
                    }
                }
            }
    }



    public static void commonSetup(final FMLCommonSetupEvent event)
    {
        //LOGGER.info("siusiak");
        Network.registerMessages();
    }

}

