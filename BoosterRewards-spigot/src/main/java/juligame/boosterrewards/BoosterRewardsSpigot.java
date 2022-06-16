package juligame.boosterrewards;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.*;

public final class BoosterRewardsSpigot extends JavaPlugin implements PluginMessageListener {


    public static BoosterRewardsSpigot plugin;
    public static String separator = "_SEPARATOR_";
    public static boolean UsingBungee = false;
    static JDA jda = null;
    public static String name = ChatColor.DARK_PURPLE+"["+ ChatColor.LIGHT_PURPLE+"BoosterRewards"+ChatColor.DARK_PURPLE+"]"+ChatColor.WHITE;

    @Override
    public void onEnable() {
        plugin = this;
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "Return");
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "Return", this);
        UsingBungee = false;
        Bukkit.getConsoleSender().sendMessage(name+" Waiting player to init plugin.");
        getServer().getPluginManager().registerEvents(new Events(), this);
        ConfigStorage.load();
        PlayerDataStorage.load();
        MessageStorage.load();
    }

    public static Boolean loaded = false;
    public static void Init(){
        if (loaded) return;
        loaded = true;

        Bukkit.getConsoleSender().sendMessage(name+" BungeeCord not detected. Using this as main.");

        plugin.getCommand("link").setExecutor(new CommandClass());
        plugin.getCommand("brforce").setExecutor(new ForceCommandClass());
        ConfigStorage.load();
        MessageStorage.load();

        try {
            if (ConfigStorage.storage.token == null) {
                Bukkit.getConsoleSender().sendMessage(name+" DISCORD BOT | Token error a");
                ConfigStorage.delete();
                ConfigStorage._default();
                ConfigStorage.save();
                return;
            }
            if (ConfigStorage.storage.guild == null) {
                Bukkit.getConsoleSender().sendMessage(name+" DISCORD BOT | Guild error a");
                ConfigStorage.delete();
                ConfigStorage._default();
                ConfigStorage.save();
                return;
            }

            jda = JDABuilder.createDefault(ConfigStorage.storage.token)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS) // also enable privileged intent
                    .build()
                    .awaitReady();
            if (jda == null || ConfigStorage.storage.token == null) {Bukkit.getConsoleSender().sendMessage(name+" DISCORD BOT | Token error"); return;}

            Guild guild = jda.getGuildById(ConfigStorage.storage.guild);
            if (guild == null || ConfigStorage.storage.guild == null) {Bukkit.getConsoleSender().sendMessage(name+" DISCORD BOT | Guild error"); return;}

            checkBoosts(guild);

        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] message) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
        try {
            String sub = in.readUTF(); // Sub-Channel
            String hash = in.readUTF();

            System.out.println("[Bungee] Received message on channel " + sub + " with hash " + hash);
            if (ConfigStorage.storage._securityHash != "" && ConfigStorage.storage._securityHash != null) {
                if (!hash.contains(ConfigStorage.storage._securityHash)){
                    Bukkit.getConsoleSender().sendMessage(name+" Security hash not valid from " + p.getName()+" "+p.getAddress()+".");
                    Bukkit.getConsoleSender().sendMessage(name+" Maybe someone is trying to hack the server? or maybe you forgot to change the hash in the config? :(");
                    return;
                }
            }

            if (sub.equals("PluginInBungee") && !UsingBungee){
                UsingBungee = true;
                if (ConfigStorage.storage._securityHash == null || ConfigStorage.storage._securityHash.equals("")){
                    ConfigStorage.delete();
                    MessageStorage.delete();
                    ConfigStorage.storage = new Storage(hash);
                    ConfigStorage.save();
                    Bukkit.getConsoleSender().sendMessage(name+" BungeeCord detected. Using Bungee config :) using security hash: ["+ConfigStorage.storage._securityHash+"]");
                }
            }
            if (sub.equals("BoosterRewards")) {
                String cmd = in.readUTF();

                String[] commands = cmd.split(separator);
                ExecuteCommands(commands);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }



    private static void checkBoosts(Guild guild)
    {
        if (loaded) return;
        PlayerDataStorage.load();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                PlayerDataStorage.load(); // BORRAR ESTO DSP
                HashMap<String, ID_Bool> savedPlayers = PlayerDataStorage.storage.SavedPlayers;
                ArrayList<String> ids = new ArrayList<>();
                //ADDS NEW BOOSTERS
                for (Member member : guild.getBoosters()) {
                    ids.add(member.getId());
                    if  (!savedPlayers.containsKey(member.getUser().getId())) {
                        savedPlayers.put(member.getUser().getId(), new ID_Bool(generateHash(),"", true, false));
                    }
                }

                //UPDATE OLD BOOST STATES
                for (String discord_id : savedPlayers.keySet()){
                    ID_Bool id_bool = savedPlayers.get(discord_id);
                    id_bool.IsBoosting = ids.contains(discord_id);
                    savedPlayers.replace(discord_id, id_bool);
                }

                //IF ITS BOOSTING AND HASNT SENDED MSG, SEND IT.
                for (String discord_id : savedPlayers.keySet()){
                    ID_Bool id_bool = savedPlayers.get(discord_id);
                    if (!id_bool.IsBoosting) { // IF ITS NOT BOOSTING
                        ExecuteCommands(ConfigStorage.storage.Exit_commands, id_bool.minecraft_name);
                        continue;
                    }
                    if (id_bool.Hash_Sended) continue;
                    User user = guild.getMemberById(discord_id).getUser();
                    user.openPrivateChannel()
                            .flatMap(channel -> channel.sendMessage(MessageStorage.storage.Discord_Message.replace("{id}", id_bool.random_private_hash)))
                            .queue();

                    id_bool.Hash_Sended = true;
                    savedPlayers.replace(discord_id, id_bool);
                }
                PlayerDataStorage.save();
            }
        },0*20, 5*20);
    }


    static void ExecuteCommandsForAll(String cmd[]){
        HashMap<String, ID_Bool> savedPlayers = PlayerDataStorage.storage.SavedPlayers;
        for (String discord_id : savedPlayers.keySet()){
            ID_Bool id_bool = savedPlayers.get(discord_id);
            if (!id_bool.IsBoosting) continue;
            if (id_bool.minecraft_name == "") continue;

            ExecuteCommands(cmd, id_bool.minecraft_name);
        }
    }

    static void ExecuteCommands(String[] cmd, String minecraft_name){

        cmd = translate(cmd, minecraft_name);
        for (String command : cmd) {
            BoosterRewardsSpigot.plugin.getServer().dispatchCommand(BoosterRewardsSpigot.plugin.getServer().getConsoleSender(), command); // Executing the command!!
        }
    }
    static void ExecuteCommands(String[] cmd){
        for (String command : cmd) {
            BoosterRewardsSpigot.plugin.getServer().dispatchCommand(BoosterRewardsSpigot.plugin.getServer().getConsoleSender(), command); // Executing the command!!
        }
    }

    //// UTILS
    private static String[] translate(String[] action, String player_name){

        String[] tmp = new String[action.length];
        int i = 0;
        for (String s : action) {
            tmp[i] = s.replace("%player%", player_name).replace("/","");
            i++;
        }

        return tmp;
    }

    static String generateHash(){
        Random rnd = new Random();
        String n = rnd.nextInt(999999)+"";

        ArrayList<String> hashes = new ArrayList<>();
        for (String discord_id : PlayerDataStorage.storage.SavedPlayers.keySet()){
            ID_Bool id_bool = PlayerDataStorage.storage.SavedPlayers.get(discord_id);
            hashes.add(id_bool.random_private_hash);
        }
        while (hashes.contains(n)) n = rnd.nextInt(999999)+"";

        return n;
    }
}
