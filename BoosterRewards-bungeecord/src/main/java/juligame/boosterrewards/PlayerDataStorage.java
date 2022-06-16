package juligame.boosterrewards;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.HashMap;


public class PlayerDataStorage {

    // Si se cambia la KEY del hashmap, cambiarlo tambien en load(). "Type type = new TypeToken<HashMap<STRING, Storage>>(){}.getType();"
    public static String file_name = "playerData.json";
    public static PlayerStorage storage = new PlayerStorage(new HashMap<String, ID_Bool>());

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

        storage = gson.fromJson(reader, PlayerStorage.class);
        reader.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Boolean exists( ) {
        return new File(BoosterRewardsBungeeCord.plugin.getDataFolder().getAbsolutePath()+ File.separator + file_name).exists();
    }
}

class PlayerStorage {
    // DISCORD ID \ UUID MC \ BOOSTING?
    HashMap<String, ID_Bool> SavedPlayers;

    PlayerStorage(HashMap<String, ID_Bool> map) {
        this.SavedPlayers = map;
    }
}
class ID_Bool{
    String random_private_hash;
    String minecraft_name;
    Boolean IsBoosting;
    Boolean Hash_Sended;

    ID_Bool(String random_private_hash,String minecraft_name, Boolean IsBoosting, Boolean Hash_Sended){
        this.random_private_hash = random_private_hash;
        this.minecraft_name = minecraft_name;
        this.IsBoosting = IsBoosting;
        this.Hash_Sended = Hash_Sended;
    }
}