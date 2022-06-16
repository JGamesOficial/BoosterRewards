package juligame.boosterrewards;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.md_5.bungee.api.ChatColor;

import java.io.*;


public class MessageStorage {

    // Si se cambia la KEY del hashmap, cambiarlo tambien en load(). "Type type = new TypeToken<HashMap<STRING, Storage>>(){}.getType();"
    public static String file_name = "Messages.json";

    public static Message_Storage storage = new Message_Storage(
            "Your id is {id}. Please type /link {id} in game.",
            "Use /link your_id to link your discord with this account.",
            "You have successfully linked your discord account with this account. {discord_tag}",
            "Error, That id does not exist.",
            "Error, There is already someone linked with that id."
    );

    public static void save()   {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            File file = new File(BoosterRewardsBungeeCord.plugin.getDataFolder().getAbsolutePath()+ File.separator + file_name);
            file.getParentFile().mkdirs();
            file.createNewFile();
            Writer writer = null;
            writer = new FileWriter(file, false);
            gson.toJson(storage, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        try {
            Gson gson = new Gson();

            File file = new File(BoosterRewardsBungeeCord.plugin.getDataFolder().getAbsolutePath()+ File.separator + file_name);
            if(!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
                save();
            }

            Reader reader = new FileReader(file);

            storage = gson.fromJson(reader, Message_Storage.class);
            storage.Reformat();
            reader.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delete() {
        File file = new File(BoosterRewardsBungeeCord.plugin.getDataFolder().getAbsolutePath()+ File.separator + file_name);
        if(file.delete())   // delete() will delete the selected file from system and return true if deletes successfully else it'll return false
        {
            BoosterRewardsBungeeCord.BungeeCord.getConsole().sendMessage(BoosterRewardsBungeeCord.name+" "+file_name+" has been deleted.");
        }
        else
        {
            BoosterRewardsBungeeCord.BungeeCord.getConsole().sendMessage(BoosterRewardsBungeeCord.name+" "+file_name+" could not be deleted.");
        }
    }


    public static Boolean exists( ) {
        return new File(BoosterRewardsBungeeCord.plugin.getDataFolder().getAbsolutePath()+ File.separator + file_name).exists();
    }
}

class Message_Storage {
    String Discord_Message;
    String CM_Link_Help;
    String CM_Link_Success;
    String CM_Link_Error;
    String CM_Link_UsedIDError;

    Message_Storage(String Discord_Message, String CM_Link_Help, String CM_Link_Linked, String CM_Link_Error, String CM_Link_UsedIDError) {
        this.Discord_Message = Discord_Message;
        this.CM_Link_Help = CM_Link_Help;
        this.CM_Link_Success = CM_Link_Linked;
        this.CM_Link_Error = CM_Link_Error;
        this.CM_Link_UsedIDError = CM_Link_UsedIDError;
    }

    public void Reformat(){
        Discord_Message = ChatColor.translateAlternateColorCodes('&', Discord_Message);
        CM_Link_Help = ChatColor.translateAlternateColorCodes('&', CM_Link_Help);
        CM_Link_Success = ChatColor.translateAlternateColorCodes('&', CM_Link_Success);
        CM_Link_Error = ChatColor.translateAlternateColorCodes('&', CM_Link_Error);
        CM_Link_UsedIDError = ChatColor.translateAlternateColorCodes('&', CM_Link_UsedIDError);
    }
}