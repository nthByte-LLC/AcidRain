package net.dohaw.acidrain;

import net.dohaw.corelib.CoreLib;
import net.dohaw.corelib.JPUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/*
    TODO: Add announcement messages. If the current time is an hour before the acid rain start time, then broadcast it in chat.
 */
public final class AcidRainPlugin extends JavaPlugin {

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    public static ZoneId zone = ZoneId.of("Europe/Amsterdam");

    private SchedulesConfig schedulesConfig;
    private BaseConfig config;
    private HashSet<String> rainyWorlds = new HashSet<>();

    private List<ScheduleInfo> scheduledInfo = new ArrayList<>();

    @Override
    public void onEnable() {

        CoreLib.setInstance(this);
        JPUtils.validateFiles("config.yml", "schedules.yml");
        JPUtils.registerEvents(new AcidRainListener(this));
        JPUtils.registerCommand("acidrain", new AcidRainCommand(this));
        this.config = new BaseConfig();

        this.schedulesConfig = new SchedulesConfig();
        this.scheduledInfo = schedulesConfig.loadSchedules();

        new AcidRainChecker(this).runTaskTimer(this, 0L, (long) (config.getDamageInterval() * 20L));
        Bukkit.getScheduler().runTaskTimer(this, () -> {

            for(ScheduleInfo info : scheduledInfo){

                ZonedDateTime now = ZonedDateTime.now(zone);
                ZonedDateTime startTime = info.getStartTimeAndDate();
                ZonedDateTime endTime = info.getEndTimeAndDate();
                String worldName = info.getWorld();
                World world = Bukkit.getWorld(worldName);

                boolean isWorldRaining = rainyWorlds.contains(worldName);
                if(isSameTime(now, startTime) && !isWorldRaining){
                    makeItRain(world);
                }else if(isSameTime(now, endTime) && isWorldRaining){
                    stopRain(world);
                }else{
                    // Makes it continuously rain
                    if(isWorldRaining){
                        world.setWeatherDuration(30);
                    }
                }

            }

        }, 0L, 20L);

    }

    @Override
    public void onDisable() {
        schedulesConfig.saveSchedules(scheduledInfo);
    }

    public HashSet<String> getRainyWorlds() {
        return rainyWorlds;
    }

    public BaseConfig getBaseConfig() {
        return config;
    }

    public void makeItRain(World world){
        rainyWorlds.add(world.getName());
        world.setThundering(true);
        world.setStorm(true);
    }

    public void stopRain(World world){
        rainyWorlds.remove(world.getName());
        world.setThundering(false);
        world.setStorm(false);
        rainyWorlds.remove(world.getName());
    }

    public List<ScheduleInfo> getScheduledInfo() {
        return scheduledInfo;
    }

    public boolean isSameTime(ZonedDateTime zdt1, ZonedDateTime zdt2){
        return zdt1.getMonth() == zdt2.getMonth() && zdt1.getDayOfMonth() == zdt2.getDayOfMonth() && zdt1.getYear() == zdt2.getYear()
                && zdt1.getHour() == zdt2.getHour() && zdt1.getMinute() == zdt2.getMinute();
    }

}
