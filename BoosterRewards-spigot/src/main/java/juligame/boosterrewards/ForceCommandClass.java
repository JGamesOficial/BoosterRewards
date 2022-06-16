package juligame.boosterrewards;


import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ForceCommandClass implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /brforce <entry|exit> <player>");
            return false;
        }
        if (args[0].equals("entry")) {
            if (args.length == 2){
                BoosterRewardsSpigot.ExecuteCommands(ConfigStorage.storage.Entry_commands, args[1]);
            }else{
                sender.sendMessage(ChatColor.RED + "Usage: /brforce <entry|exit> <player>");
            }
        }
        else if (args[0].equals("exit")) {
            if (args.length == 2) {

                BoosterRewardsSpigot.ExecuteCommands( ConfigStorage.storage.Exit_commands, args[1]);
                return true;
            }else{
                sender.sendMessage(ChatColor.RED + "Usage: /brforce <entry|exit> <player>");
                return true;
            }
        }else{
            sender.sendMessage(ChatColor.RED + "Usage: /brforce <entry|exit> <player>");
            return true;
        }
        return true;
    }
}