package play.bratiwkaslam.gamecore;

import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

public class Main
        extends JavaPlugin
        implements Listener
{
    public FileConfiguration config;
    Logger log = Logger.getLogger("Minecraft");
    public float Yaw;
    public float Pitch;
    public double X;
    public double Y;
    public double Z;
    public String World;

    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        updateConfig();

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
        {
            public void run()
            {
                for (World w : Bukkit.getServer().getWorlds()) {
                    w.setTime(0L);
                }
            }
        }, 0L, 10000L);
    }

    public void onDisable() {}

    public void updateConfig()
    {
        this.Yaw = getConfig().getInt("Yaw");
        this.Pitch = getConfig().getInt("Pitch");
        this.X = getConfig().getDouble("X");
        this.Y = getConfig().getDouble("Y");
        this.Z = getConfig().getDouble("Z");
        this.World = getConfig().getString("World");
        saveConfig();
        reloadConfig();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if ((sender instanceof Player))
        {
            Player p = (Player)sender;
            if ((cmd.getName().equalsIgnoreCase("spawnset")) &&
                    (p.isOp()))
            {
                Location l = p.getLocation();
                int x = l.getBlockX();
                int y = l.getBlockY();
                int z = l.getBlockZ();
                getConfig().set("X", Double.valueOf(l.getBlockX() + 0.5D));
                getConfig().set("Y", Double.valueOf(l.getBlockY() + 0.5D));
                getConfig().set("Z", Double.valueOf(l.getBlockZ() + 0.5D));
                getConfig().set("Yaw", Float.valueOf(l.getYaw()));
                getConfig().set("Pitch", Float.valueOf(l.getPitch()));
                getConfig().set("World", String.valueOf(l.getWorld().getName()));
                p.getWorld().setSpawnLocation(x, y, z);
                p.sendMessage("Setted");
                updateConfig();
            }
        }
        return true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();

        p.teleport(new Location(Bukkit.getWorld(this.World), this.X, this.Y, this.Z, this.Yaw, this.Pitch));

        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
    }

    @EventHandler
    public void onWater(BlockFromToEvent e)
    {
        int id = e.getBlock().getTypeId();
        if ((id == 8) || (id == 9)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e)
    {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e)
    {
        Player p = e.getPlayer();
        if ((!p.getGameMode().equals(GameMode.CREATIVE)) &&
                (p.getWorld().getName().equals("lobby"))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent e)
    {
        Entity p = e.getEntity();
        if (p.getWorld().getName().equals("lobby")) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void DisableWeather(WeatherChangeEvent event)
    {
        event.setCancelled(event.toWeatherState());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e)
    {
        Player p = e.getPlayer();
        Location loc = p.getLocation();
        if ((p.getWorld().getName().equals("lobby")) &&
                ((int)loc.getY() <= 10)) {
            p.teleport(new Location(Bukkit.getWorld(this.World), this.X, this.Y, this.Z, this.Yaw, this.Pitch));
        }
    }


    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent e){
        e.setCancelled(true);
    }
}
