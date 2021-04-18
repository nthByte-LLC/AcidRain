package net.dohaw.acidrain;

import net.dohaw.corelib.Config;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class SchedulesConfig extends Config {

    public SchedulesConfig() {
        super("schedules.yml");
    }

    public void saveSchedules(List<ScheduleInfo> allScheduleInfo){

        List<String> scheduleInfoInConfig = new ArrayList<>();
        for(ScheduleInfo info : allScheduleInfo){
            String worldName = info.getWorld();
            ZonedDateTime startTime = info.getStartTimeAndDate();
            ZonedDateTime endTime = info.getEndTimeAndDate();
            scheduleInfoInConfig.add(worldName + ";" + startTime.format(AcidRainPlugin.formatter) + ";" + endTime.format(AcidRainPlugin.formatter));
        }

        config.set("Schedules", scheduleInfoInConfig);
        saveConfig();

    }

    public List<ScheduleInfo> loadSchedules(){

        List<ScheduleInfo> allScheduleInfo = new ArrayList<>();
        List<String> scheduleInfoInConfig = config.getStringList("Schedules");


        for(String line : scheduleInfoInConfig){

            String[] lineArr = line.split(";");
            String worldName = lineArr[0];

            LocalDateTime localStartTime = LocalDateTime.parse(lineArr[1], AcidRainPlugin.formatter);
            ZonedDateTime startTime = ZonedDateTime.of(localStartTime, AcidRainPlugin.zone);

            LocalDateTime localEndTime = LocalDateTime.parse(lineArr[2], AcidRainPlugin.formatter);
            ZonedDateTime endTime = ZonedDateTime.of(localEndTime, AcidRainPlugin.zone);
            ScheduleInfo info = new ScheduleInfo(startTime, endTime, worldName);
            allScheduleInfo.add(info);

        }

        return allScheduleInfo;

    }

}
