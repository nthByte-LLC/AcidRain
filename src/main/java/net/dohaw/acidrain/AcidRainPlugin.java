package net.dohaw.acidrain;

import net.dohaw.corelib.CoreLib;
import net.dohaw.corelib.JPUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/*
    TODO: Add announcement messages. If the current time is an hour before the acid rain start time, then broadcast it in chat.
 */
public final class AcidRainPlugin extends JavaPlugin {

    private List<Enchantment> enchantList = new ArrayList<>();

    public static AcidRainProtection acidRainProtection;

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

        acidRainProtection = new AcidRainProtection();
        enchantList.add(acidRainProtection);
        registerEnchantment(acidRainProtection);

    }

    @Override
    public void onDisable() {
        schedulesConfig.saveSchedules(scheduledInfo);
        unRegisterEnchantments();
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

    private void registerEnchantment(Enchantment ench) {

        boolean registered = true;
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
            Enchantment.registerEnchantment(ench);
        }catch(Exception e) {
            registered = false;
            //e.printStackTrace();
        }

        if(registered) {
            getLogger().info("Registered enchantment " + ench.getName());
        }
    }

    public void unRegisterEnchantments(){

        getLogger().info("Getting rid of enchantments...");
        try {
            Field keyField = Enchantment.class.getDeclaredField("byKey");

            keyField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<Integer, Enchantment> byKey = (HashMap<Integer, Enchantment>) keyField.get(null);

            for(Enchantment e : enchantList) {
                byKey.remove(e.getId());
            }

            Field nameField = Enchantment.class.getDeclaredField("byName");

            nameField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<String, Enchantment> byName = (HashMap<String, Enchantment>) nameField.get(null);

            for(Enchantment e : enchantList) {
                byName.remove(e.getName());
            }

        } catch (Exception ignored) { }
    }

}
