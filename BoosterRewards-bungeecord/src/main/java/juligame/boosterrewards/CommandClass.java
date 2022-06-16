package juligame.boosterrewards;


import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class CommandClass extends Command{

    public CommandClass()  {
        super("link");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
//            sender.sendMessage(ChatColor.RED + "Usage: /link <id>");
            sender.sendMessage(MessageStorage.storage.CM_Link_Help);
            return;
        }
        // Here, we're looping through each argument.
        if (args.length > 1){
//            sender.sendMessage(ChatColor.RED + "Usage: /link <id>");
            sender.sendMessage(MessageStorage.storage.CM_Link_Help);
            return;
        }

        for (String Discord_ID:PlayerDataStorage.storage.SavedPlayers.keySet()) {
            if (PlayerDataStorage.storage.SavedPlayers.get(Discord_ID).random_private_hash.equals(args[0])){
                if (!PlayerDataStorage.storage.SavedPlayers.get(Discord_ID).minecraft_name.equals("")){
//                    sender.sendMessage("Alguien ya uso este id.");
                    sender.sendMessage(MessageStorage.storage.CM_Link_UsedIDError);
                    return;
                }
                ID_Bool id_bool = PlayerDataStorage.storage.SavedPlayers.get(Discord_ID);
                id_bool.minecraft_name = sender.getName();
//                sender.sendMessage("Su cuenta fue vinculada con exito. "+ BoosterRewardsBungeeCord.jda.getUserById(Discord_ID).getAsTag());
                sender.sendMessage(MessageStorage.storage.CM_Link_Success.replace("{discord_tag}", BoosterRewardsBungeeCord.jda.getUserById(Discord_ID).getAsTag()));
                PlayerDataStorage.save();
                if (ConfigStorage.storage.GiveOnLink) BoosterRewardsBungeeCord.ExecuteCommands(ConfigStorage.storage.Entry_commands, sender.getName());
                return;
            }
        }
        sender.sendMessage(MessageStorage.storage.CM_Link_Error);

//        sender.sendMessage(new TextComponent(ChatColor.GREEN + "Command: '/" + cmd + "' has been executed on all servers."));
    }




}