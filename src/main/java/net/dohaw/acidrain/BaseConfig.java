package net.dohaw.acidrain;

import net.dohaw.corelib.Config;
import net.dohaw.corelib.StringUtils;
import org.bukkit.Material;

import java.util.List;

public class BaseConfig extends Config {

    public BaseConfig() {
        super("config.yml");
    }

    // /acidrain schedule 21:00 24:00
    public double getDamageFromRain(){
        return config.getDouble("Damage From Rain");
    }

    public double getDamageInterval(){
        return config.getDouble("Damage Interval");
    }

    public Material getMaterialHelmet(){

        String materialStr = config.getString("Material Helmet Given");
        Material materialHelmet;
        try{
            materialHelmet = Material.valueOf(materialStr);
        }catch(IllegalArgumentException | NullPointerException e){
            materialHelmet = Material.IRON_HELMET;
        }
        return materialHelmet;

    }

    public List<String> getReminderIntervals(){
        return getCommaList("Reminder Intervals");
    }

    public String getPrefix(){
        return StringUtils.colorString(config.getString("Prefix"));
    }

}
