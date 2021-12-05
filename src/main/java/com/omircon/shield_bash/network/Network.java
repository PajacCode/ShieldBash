package com.omircon.shield_bash.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import static com.omircon.shield_bash.ShieldBash.MODID;

public class Network {

    private static SimpleChannel INSTANCE;

    private static int ID = 0;

    private static int nextID()
    {
        return ID++;
    }

    public static void registerMessages()
    {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, "enchantconfig"),
                () -> "1.0",
                s -> true,
                s -> true);

        INSTANCE.messageBuilder(ShieldBashPacket.class, nextID())
                .encoder(ShieldBashPacket::toBytes)
                .decoder(ShieldBashPacket::new)
                .consumer(ShieldBashPacket::handle)
                .add();

    }

    public static void sendToClient(Object packet, ServerPlayerEntity player)
    {
        INSTANCE.sendTo(packet, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToServer(Object packet)
    {
        INSTANCE.sendToServer(packet);
    }
}