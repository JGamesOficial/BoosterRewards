package juligame.boosterrewards;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;

import java.io.*;


public class ConfigStorage {

    // Si se cambia la KEY del hashmap, cambiarlo tambien en load(). "Type type = new TypeToken<HashMap<STRING, Storage>>(){}.getType();"
    public static String file_name = "config.json";

    public static Storage storage = new Storage(
            new String[]{"/give %player% diamond 2", "/give %player% emerald"},
            new String[]{"/give %player% coal"},
            true,
            "",
            ""
    );

    public static void _default(){
        storage = new Storage(
                new String[]{"/give %player% diamond 2", "/give %player% emerald"},
                new String[]{"/give %player% coal"},
                true,
                "",
                ""
        );
    }
    public static void save()   {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            File file = new File(BoosterRewardsSpigot.plugin.getDataFolder().getAbsolutePath()+ File.separator + file_name);
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

        File file = new File(BoosterRewardsSpigot.plugin.getDataFolder().getAbsolutePath()+ File.separator + file_name);
        if(!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
            save();
        }

        Reader reader = new FileReader(file);

        storage = gson.fromJson(reader, Storage.class);
        reader.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delete() {
        File file = new File(BoosterRewardsSpigot.plugin.getDataFolder().getAbsolutePath()+ File.separator + file_name);
        if(file.delete())   // delete() will delete the selected file from system and return true if deletes successfully else it'll return false
        {
            Bukkit.getConsoleSender().sendMessage(BoosterRewardsSpigot.name+" "+file_name+" has been deleted.");
        }
        else
        {
            Bukkit.getConsoleSender().sendMessage(BoosterRewardsSpigot.name+" "+file_name+" can't be deleted.");
        }
    }


    public static Boolean exists( ) {
        return new File(BoosterRewardsSpigot.plugin.getDataFolder().getAbsolutePath()+ File.separator + file_name).exists();
    }
}
class Storage {
    String _securityHash;
    String[] Entry_commands;
    String[] Exit_commands;
    Boolean GiveOnLink;
    String token;
    String guild;

    Storage(String[] Entry_commands, String[] Exit_commands,Boolean GiveOnLink, String token, String guild) {
        this.Entry_commands = Entry_commands;
        this.Exit_commands = Exit_commands;
        this.GiveOnLink = GiveOnLink;
        this.token = token;
        this.guild = guild;
    }
    Storage(String _securityHash) {
        this._securityHash = _securityHash;
    }
}