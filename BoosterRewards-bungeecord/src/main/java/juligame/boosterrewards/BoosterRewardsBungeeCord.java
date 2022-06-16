package juligame.boosterrewards;


import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class BoosterRewardsBungeeCord extends Plugin implements Listener {

    public static BoosterRewardsBungeeCord plugin;

    static JDA jda = null;
    public static ProxyServer BungeeCord = ProxyServer.getInstance();
    public static String name = ChatColor.DARK_PURPLE+"["+ChatColor.LIGHT_PURPLE+"BoosterRewards"+ChatColor.DARK_PURPLE+"]"+ChatColor.WHITE;
    @Override
    public void onEnable() {
        plugin = this;

        BungeeCord.registerChannel("Return"); // Here, we are registering the channel. This channel name will be sent to Bukkit and Bukkit will check if the incoming message is from this channel and if so, execute our cmd.
        BungeeCord.getPluginManager().registerCommand(this, new CommandClass()); // Registering the command.
        BungeeCord.getPluginManager().registerCommand(this, new ForceCommandClass()); // Registering the listener.

        this.getProxy().getPluginManager().registerListener(this, this);



//        sendAliveMessage();

        PlayerDataStorage.load();
        ConfigStorage.load();
        MessageStorage.load();
        if (ConfigStorage.storage._securityHash.equals("") || ConfigStorage.storage._securityHash == null) {
            ConfigStorage.storage._securityHash = _randomSecurityHash();
            ConfigStorage.save();
        }
        _securityHash = ConfigStorage.storage._securityHash;

        try {
            jda = JDABuilder.createDefault(ConfigStorage.storage.token)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS) // also enable privileged intent
                    .build()
                    .awaitReady();
            if (jda == null) {BungeeCord.getConsole().sendMessage(name+" DISCORD BOT | Token error"); return;}

            Guild guild = jda.getGuildById(ConfigStorage.storage.guild);
            if (guild == null) {BungeeCord.getConsole().sendMessage(name+" DISCORD BOT | Guild error"); return;}

            checkBoosts(guild);

        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
        //You can also add event listeners to the already built JDA instance
        // Note that some events may not be received if the listener is added after calling build()
        // This includes events such as the ReadyEvent

    }

    //Used when bukkit send a packet toc check bungee existence.
    @EventHandler
    public void onPluginMessage(PluginMessageEvent ev) throws IOException {
        if (!ev.getTag().equals("REGISTER")) return;

        Thread t = new Thread(() -> {
            int n = 3;
            while (true){
                try {
                    if (n == 0) return;
                    Server server = (Server) ev.getSender();
                    ServerInfo info = server.getInfo();
                    sendAliveMessage(info);
                    n--;
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public static String _randomSecurityHash() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 32;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

    static String _securityHash = "";
    public void sendAliveMessage(ServerInfo serverInfo) {
        sendToBukkit("PluginInBungee", _securityHash, serverInfo);
    }

    static void ExecuteCommands(String[] cmd, String minecraft_name){
        cmd = translate(cmd, minecraft_name);
        for (String command : cmd) {
            sendToAllBungee(command);
        }
    }

    static void sendToAllBungee(String command){
        Map<String, ServerInfo> servers = BungeeCord.getInstance().getServers();
        for (Map.Entry<String, ServerInfo> en : servers.entrySet()) { // Looping through each Server of Bungee.
            String name = en.getKey();
            ServerInfo all = BungeeCord.getInstance().getServerInfo(name);
            sendToBukkit("BoosterRewards", command, all);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private static String[] translate(String[] action, String player_name){

        String[] tmp = new String[action.length];
        int i = 0;
        for (String s : action) {
            tmp[i] = s.replace("%player%", player_name).replace("/","");
            i++;
        }

        return tmp;
    }



    private static void sendToBukkit(String channel, String message, ServerInfo server) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
            out.writeUTF(channel);
            out.writeUTF(_securityHash);
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Note the "Return". It is the channel name that we registered in our Main class of Bungee plugin.
        server.sendData("Return", stream.toByteArray());
    }


    private void checkBoosts(Guild guild)
    {
        PlayerDataStorage.load();
        getProxy().getScheduler().schedule(this, new Runnable() {
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
        }, 1, 5, TimeUnit.SECONDS);
    }

    String generateHash(){
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
