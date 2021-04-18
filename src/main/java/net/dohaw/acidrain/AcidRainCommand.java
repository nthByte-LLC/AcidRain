package net.dohaw.acidrain;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashSet;

public class AcidRainCommand implements CommandExecutor {

    private AcidRainPlugin plugin;

    public AcidRainCommand(AcidRainPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length > 0){

            //acidrain start <world name>
            if(args[0].equalsIgnoreCase("start") && args.length > 1){

                String worldName = args[1];
                World potentialWorld = Bukkit.getWorld(worldName);
                if(potentialWorld != null){

                    HashSet<String> rainyWorlds = plugin.getRainyWorlds();
                    if(!rainyWorlds.contains(worldName)){
                        plugin.makeItRain(potentialWorld);
                        sender.sendMessage("Acid rain has started in the world \"" + worldName + "\".");
                    }else{
                        sender.sendMessage("It is already acid raining in this world!");
                    }

                }else{
                    sender.sendMessage("This is not a valid world!");
                }

            }else if(args[0].equalsIgnoreCase("stop") && args.length == 2){

                String worldName = args[1];
                World potentialWorld = Bukkit.getWorld(worldName);
                if(potentialWorld != null){

                    HashSet<String> rainyWorlds = plugin.getRainyWorlds();
                    if(rainyWorlds.contains(worldName)){
                        plugin.stopRain(potentialWorld);
                    }else{
                        sender.sendMessage("It isn't acid raining in this world!");
                    }

                }else{
                    sender.sendMessage("This is not a valid world!");
                }

                // /ar schedule <world name> <start date> <start time> <end date> <end time>
            }else if(args[0].equalsIgnoreCase("schedule") && args.length == 6){

                String worldName = args[1];
                if(Bukkit.getWorld(worldName) == null){
                    sender.sendMessage("This is not a valid world!");
                    return false;
                }
                // 07-04-2001
                String startDateStr = args[2];
                String endDateStr= args[4];
                String startTimeStr = args[3];
                String endTimeStr = args[5];
                String[] startDateArr = startDateStr.split("-");
                String[] endDateArr = endDateStr.split("-");
                String[] startTimeArr = startTimeStr.split(":");
                String[] endTimeArr = endTimeStr.split(":");
                if(isValidDate(startDateArr) && isValidDate(endDateArr) && isValidTime(startTimeArr) && isValidTime(endTimeArr)){

                    String startSequence = startDateStr + " " + startTimeStr;

                    LocalDateTime localStartTime = LocalDateTime.parse(startSequence, AcidRainPlugin.formatter);
                    ZonedDateTime startDate = ZonedDateTime.of(localStartTime, AcidRainPlugin.zone);
                    System.out.printf("START: %s\n" , startDate.format(AcidRainPlugin.formatter));

                    String endSequence = endDateStr + " " + endTimeStr;
                    LocalDateTime localEndTime = LocalDateTime.parse(endSequence, AcidRainPlugin.formatter);
                    ZonedDateTime endDate = ZonedDateTime.of(localEndTime, AcidRainPlugin.zone);
                    System.out.printf("END: %s\n" , endDate.format(AcidRainPlugin.formatter));

                    ZonedDateTime now = ZonedDateTime.now(AcidRainPlugin.zone);
                    System.out.printf("NOW: %s\n" , now.format(AcidRainPlugin.formatter));
                    boolean isValidTimeRange = startDate.isBefore(endDate);
                    boolean isStartTimeValid = startDate.isAfter(now);
                    if(isValidTimeRange && isStartTimeValid){
                        ScheduleInfo scheduleInfo = new ScheduleInfo(startDate, endDate, worldName);
                        plugin.getScheduledInfo().add(scheduleInfo);
                    }else{
                        sender.sendMessage("There has been an issue!");
                        if(!isValidTimeRange){
                            sender.sendMessage("Your start time is after your end time!");
                        }else{
                            sender.sendMessage("The current time is before the start time you defined!");
                        }
                    }

                }

            }

        }

        return false;
    }

    private boolean isValidDate(String[] dateArr){
        return dateArr.length == 3;
    }

    private boolean isValidTime(String[] timeArr){
        return timeArr.length == 2;
    }

}
