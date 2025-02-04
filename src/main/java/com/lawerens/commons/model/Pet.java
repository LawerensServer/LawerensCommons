package com.lawerens.commons.model;

import com.mojang.authlib.GameProfile;
import lombok.Data;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_20_R2.CraftServer;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.Objects;
import java.util.UUID;

@Data
public class Pet {

    private OfflinePlayer owner;
    private UUID uniqueID;
    private String name;
    private int level, exp;
    private EntityType entityType;
    private LivingEntity entity;

    private void spawn(){
        if(entity == null || entity.isDead() && owner.isOnline())
            entity = (LivingEntity) Objects.requireNonNull(owner.getPlayer()).getLocation().getWorld()
                    .spawnEntity(owner.getPlayer().getLocation(), entityType, false);
    }

}
