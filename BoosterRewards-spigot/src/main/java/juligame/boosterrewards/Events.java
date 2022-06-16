package juligame.boosterrewards;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Events implements Listener {
    public boolean called = false;
    @EventHandler
    public void onJoin(org.bukkit.event.player.PlayerJoinEvent e) throws ExecutionException, InterruptedException, IOException {
        if (called) return;
        called = true;

        Bukkit.getConsoleSender().sendMessage(BoosterRewardsSpigot.name+" A player has connected, checking bungee compativility.");
        Bukkit.getScheduler().scheduleSyncDelayedTask(BoosterRewardsSpigot.plugin, new Runnable() {
            @Override
            public void run() {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(BoosterRewardsSpigot.UsingBungee);
//                        try {
////                            if (BoosterRewardsSpigot.isUsingBungee()) BoosterRewardsSpigot.Init();
                            if (!BoosterRewardsSpigot.UsingBungee){ BoosterRewardsSpigot.Init();}
                            else {

                            }
//                        } catch (ExecutionException | InterruptedException ex) {
//                            ex.printStackTrace();
//                        }
                    }
                });
                thread.run();
            }
        }, 10*20L);





//        Player p = e.getPlayer();

//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        DataOutputStream out = new DataOutputStream(stream);
//        out.writeUTF("CheckingBungee");

//        BoosterRewardsSpigot.plugin.getServer().sendPluginMessage(BoosterRewardsSpigot.plugin,"Return", stream.toByteArray());
//        p.sendPluginMessage(BoosterRewardsSpigot.plugin,"Return", stream.toByteArray());
    }
}

