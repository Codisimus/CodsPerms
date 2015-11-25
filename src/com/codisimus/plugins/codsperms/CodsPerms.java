package com.codisimus.plugins.codsperms;

import com.codisimus.database.util.DatabaseAPI;
import com.codisimus.database.util.MySQLConnection;
import java.io.File;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * CodsPerms is a light weight Permissions alternative
 * Storing as flat files or in a database are both supported
 * The included API allows for custom storage options as well
 *
 * @author Codisimus
 */
public class CodsPerms extends JavaPlugin implements Listener {
    static JavaPlugin plugin;
    static Logger logger;

    public static void main(String[] args) {
        //Do Nothing - For debugging within NetBeans IDE
    }

    @Override
    public void onEnable() {
        plugin = this;
        logger = getLogger();
        Bukkit.getPluginManager().registerEvents(this, this);

        PermissionStorageHandler permissionHandler = null;
        if (Bukkit.getPluginManager().isPluginEnabled("DatabaseAPI")) {
            MySQLConnection conn = DatabaseAPI.getMySQLConnection(this);
            if (conn == null) {
                logger.info("Please configure database connection information for CodsPerms in order to save permissions via DatabaseAPI");
            } else {
                permissionHandler = new DBHandler(conn);
                logger.info("Saving permissions using DatabaseAPI");
            }
        }
        if (permissionHandler == null) {
            permissionHandler = new FlatFileHandler(new File(this.getDataFolder(), "permissions.yml"));
            logger.info("Saving permissions in YAML format");
        }
        PermissionAPI.setPermissionStorageHandler(permissionHandler);
        permissionHandler.loadPermissions();

        new CommandHandler(this, "perm").registerCommands(PermCommand.class);
    }

    @Override
    public void onDisable() {
        PermissionAPI.unloadPermissions();
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onPlayerLogin(PlayerLoginEvent event) {
        PermissionAPI.reloadPermissions(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PermissionAPI.unloadPermissions(event.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        PermissionAPI.unloadPermissions(event.getPlayer());
    }
}
