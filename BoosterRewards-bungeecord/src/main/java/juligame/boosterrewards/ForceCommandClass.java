package juligame.boosterrewards;


import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ForceCommandClass extends Command{

    public ForceCommandClass()  {
        super("brforce");
    }

    //Add permission requirement.

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!sender.hasPermission("boosterrewards.force")) return;
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /brforce <command>");
            sender.sendMessage(ChatColor.RED + "Usage: /brforce <entry|exit> <player>");
            return;
        }
        String command = "";
        if (args[0].equals("entry")) {
            if (args.length == 2){
                BoosterRewardsBungeeCord.ExecuteCommands(ConfigStorage.storage.Entry_commands, args[1]);
            }else{
                sender.sendMessage(ChatColor.RED + "Usage: /brforce <entry|exit> <player>");
            }
        }
        else if (args[0].equals("exit")) {
            if (args.length == 2) {

                BoosterRewardsBungeeCord.ExecuteCommands( ConfigStorage.storage.Exit_commands, args[1]);
                return;
            }else{
                sender.sendMessage(ChatColor.RED + "Usage: /brforce <entry|exit> <player>");
                return;
            }
        }else{
            for (String s : args) {
                command = command + s + " ";
            }
            BoosterRewardsBungeeCord.sendToAllBungee(command);
        }


//        sender.sendMessage(new TextComponent(ChatColor.GREEN + "Command: '/" + cmd + "' has been executed on all servers."));
    }




}