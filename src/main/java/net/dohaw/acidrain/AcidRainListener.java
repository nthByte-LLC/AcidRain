package net.dohaw.acidrain;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.HashSet;

public class AcidRainListener implements Listener {

    private AcidRainPlugin plugin;

    public AcidRainListener(AcidRainPlugin plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorldRain(WeatherChangeEvent e){

        boolean isRaining = e.toWeatherState();
        World world = e.getWorld();
        String worldName = world.getName();
        HashSet<String> rainyWorlds = plugin.getRainyWorlds();

        if(isRaining){
            rainyWorlds.add(worldName);
        }else{
            if(rainyWorlds.contains(worldName)){
                rainyWorlds.remove(worldName);
            }
        }

    }

}
