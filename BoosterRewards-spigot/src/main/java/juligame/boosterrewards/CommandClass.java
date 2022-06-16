package juligame.boosterrewards;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandClass implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(MessageStorage.storage.CM_Link_Help);
            return false;
        }
        // Here, we're looping through each argument.
        if (args.length > 1){
            sender.sendMessage(MessageStorage.storage.CM_Link_Help);
            return false;
        }

        for (String Discord_ID:PlayerDataStorage.storage.SavedPlayers.keySet()) {
//            System.out.println(PlayerDataStorage.storage.SavedPlayers.get(Discord_ID).random_private_hash +"aa"+args[0]);
            if (PlayerDataStorage.storage.SavedPlayers.get(Discord_ID).random_private_hash.equals(args[0])){
                if (!PlayerDataStorage.storage.SavedPlayers.get(Discord_ID).minecraft_name.equals("")){
//                    sender.sendMessage("Alguien ya uso este id.");
                    sender.sendMessage(MessageStorage.storage.CM_Link_UsedIDError);
                    return false;
                }
                ID_Bool id_bool = PlayerDataStorage.storage.SavedPlayers.get(Discord_ID);
                id_bool.minecraft_name = sender.getName();
                sender.sendMessage(MessageStorage.storage.CM_Link_Success.replace("{discord_tag}", BoosterRewardsSpigot.jda.getUserById(Discord_ID).getAsTag()));
//                sender.sendMessage("Su cuenta fue vinculada con exito. "+ BoosterRewardsSpigot.jda.getUserById(Discord_ID).getAsTag());
                PlayerDataStorage.save();
                if (ConfigStorage.storage.GiveOnLink) BoosterRewardsSpigot.ExecuteCommands(ConfigStorage.storage.Entry_commands, sender.getName());
                return true;
            }
        }

        sender.sendMessage(MessageStorage.storage.CM_Link_Error);

//        sender.sendMessage(new TextComponent(ChatColor.GREEN + "Command: '/" + cmd + "' has been executed on all servers."));
        return true;
    }
}