package juligame.boosterrewards;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.*;


public class ConfigStorage {

    // Si se cambia la KEY del hashmap, cambiarlo tambien en load(). "Type type = new TypeToken<HashMap<STRING, Storage>>(){}.getType();"
    public static String file_name = "config.json";
    public static Storage storage = new Storage(
            BoosterRewardsBungeeCord._randomSecurityHash(),
            new String[]{"/give %player% diamond 2", "/give %player% emerald"},
            new String[]{"/give %player% coal"},
            true,
            "",
            ""
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

        storage = gson.fromJson(reader, Storage.class);
        reader.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Boolean exists( ) {
        return new File(BoosterRewardsBungeeCord.plugin.getDataFolder().getAbsolutePath()+ File.separator + file_name).exists();
    }
}

class Storage {
    String _securityHash = "";
    String[] Entry_commands;
    String[] Exit_commands;
    Boolean GiveOnLink;
    String token;
    String guild;


    Storage(String _securityHash ,String[] Entry_commands, String[] Exit_commands,Boolean GiveOnLink, String token, String guild) {
        this._securityHash = _securityHash;
        this.Entry_commands = Entry_commands;
        this.Exit_commands = Exit_commands;
        this.GiveOnLink = GiveOnLink;
        this.token = token;
        this.guild = guild;
    }
}