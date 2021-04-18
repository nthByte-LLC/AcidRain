package net.dohaw.acidrain;

import net.dohaw.corelib.Config;

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

}
