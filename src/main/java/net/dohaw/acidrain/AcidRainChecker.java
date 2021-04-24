package net.dohaw.acidrain;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;

public class AcidRainChecker extends BukkitRunnable {

    private BaseConfig config;
    private AcidRainPlugin plugin;

    public AcidRainChecker(AcidRainPlugin plugin){
        this.plugin = plugin;
        this.config = plugin.getBaseConfig();
    }

    @Override
    public void run() {

        for(Player player : Bukkit.getOnlinePlayers()){

            World world = player.getWorld();
            String worldName = world.getName();

            HashSet<String> rainyWorlds = plugin.getRainyWorlds();
            // It's raining
            if(rainyWorlds.contains(worldName)){
                if(isExposedToRain(player) && !hasAcidRainProtection(player)){

                    double playerHealth = player.getHealth();
                    double damageFromRain = config.getDamageFromRain();
                    double newPlayerHealth = playerHealth - damageFromRain;

                    if(newPlayerHealth < 0){
                        newPlayerHealth = 0;
                    }

                    player.setHealth(newPlayerHealth);
                    player.playEffect(EntityEffect.HURT);

                }
            }

        }

    }

    private boolean isExposedToRain(Player player){
        Location playerLocation = player.getLocation();
        int highestBlock = player.getWorld().getHighestBlockYAt(playerLocation);
        double playerYLevel = playerLocation.getY();
        return highestBlock < playerYLevel;
    }

    private boolean hasAcidRainProtection(Player player){
        ItemStack helmet = player.getEquipment().getHelmet();
        if(helmet != null){
            if(helmet.hasItemMeta()){
                return helmet.getItemMeta().hasEnchant(AcidRainPlugin.acidRainProtection);
            }
        }
        return false;
    }

}
