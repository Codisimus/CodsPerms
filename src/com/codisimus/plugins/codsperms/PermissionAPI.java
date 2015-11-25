package com.codisimus.plugins.codsperms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

/**
 * Provides functions for handling permissions
 *
 * @author Codisimus
 */
public class PermissionAPI {
    private static PermissionStorageHandler permHandler;
    static final HashMap<OfflinePlayer, PermissionAttachment> permissions = new HashMap<>();

    /**
     * Sets the PermissionStorageHandler to be used
     *
     * @param handler The PermissionStorageHandler to handle permissions
     */
    public static void setPermissionStorageHandler(PermissionStorageHandler handler) {
        permHandler = handler;
    }

    /**
     * Returns the set PermissionStorageHandler
     *
     * @return The set PermissionStorageHandler
     */
    static PermissionStorageHandler getPermissionStorageHandler() {
        return permHandler;
    }

    /**
     * Unloads Permissions for all Players
     */
    public static void unloadPermissions() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            unloadPermissions(player);
        }
        permissions.clear();
    }

    /**
     * Unloads Permissions for the given Player
     *
     * @param player The given Player
     */
    public static void unloadPermissions(Player player) {
        if (permissions.containsKey(player)) {
            player.removeAttachment(permissions.remove(player));
        }
    }

    /**
     * Unloads and reloads all Permissions
     */
    public static void reloadPermissions() {
        unloadPermissions();
        Map<String, List<PermissionNode>> permissionsMap = permHandler.loadPermissions();
        for (Map.Entry<String, List<PermissionNode>> entry : permissionsMap.entrySet()) {
            Player player = Bukkit.getPlayer(UUID.fromString(entry.getKey()));
            if (player != null) {
                PermissionAPI.setPermissions(player, entry.getValue());
            }
        }
    }

    /**
     * Unloads and reloads Permissions for the given Player
     *
     * @param player The given Player 
     */
    public static void reloadPermissions(Player player) {
        List<PermissionNode> nodeList = permHandler.loadPermissions(player);
        setPermissions(player, nodeList);
    }

    /**
     * Adds a List of Permissions for the given Player
     *
     * @param oPlayer The OfflinePlayer to give Permissions to
     * @param nodeList The List of PermissionNodes to give
     */
    public static void addPermissions(OfflinePlayer oPlayer, List<PermissionNode> nodeList) {
        if (oPlayer.isOnline()) {
            for (PermissionNode node : nodeList) {
                permissions.get(oPlayer).setPermission(node.getName(), node.getValue());
            }
        }
        permHandler.addPermissions(oPlayer, nodeList);
    }

    /**
     * Adds a PermissionNode to the given Player
     *
     * @param oPlayer The OfflinePlayer to give the Permission to
     * @param node The PermissionNode to give
     */
    public static void addPermission(OfflinePlayer oPlayer, PermissionNode node) {
        if (oPlayer.isOnline()) {
            permissions.get(oPlayer).setPermission(node.getName(), node.getValue());
        }
        permHandler.addPermission(oPlayer, node);
    }

    /**
     * Removes all Permissions for the given Player
     *
     * @param oPlayer The given Player
     */
    public static void removeAllPermissions(OfflinePlayer oPlayer) {
        if (oPlayer.isOnline()) {
            unloadPermissions(oPlayer.getPlayer());
        }
        permHandler.removeAllPermissions(oPlayer);
    }

    /**
     * Removes a List of Permissions for the given Player
     *
     * @param oPlayer The OfflinePlayer to remove Permissions for
     * @param nodeList The List of PermissionNodes to remove
     */
    public static void removePermissions(OfflinePlayer oPlayer, List<PermissionNode> nodeList) {
        if (oPlayer.isOnline()) {
            for (PermissionNode node : nodeList) {
                permissions.get(oPlayer).unsetPermission(node.getName());
            }
        }
        permHandler.removePermissions(oPlayer, nodeList);
    }

    /**
     * Removes a PermissionNode for the given Player
     *
     * @param oPlayer The OfflinePlayer to remove the Permission for
     * @param node The PermissionNode to remove
     */
    public static void removePermission(OfflinePlayer oPlayer, PermissionNode node) {
        if (oPlayer.isOnline()) {
            permissions.get(oPlayer).unsetPermission(node.getName());
        }
        permHandler.removePermission(oPlayer, node);
    }

    /**
     * Sets the current Permissions for the given Player
     *
     * @param player The given Player
     * @param nodeList The List of PermissionNodes to assign to the Player
     */
    private static void setPermissions(Player player, List<PermissionNode> nodeList) {
        unloadPermissions(player);
        PermissionAttachment attachment = player.addAttachment(CodsPerms.plugin);
        for (PermissionNode node : nodeList) {
            attachment.setPermission(node.getName(), node.getValue());
        }
        permissions.put(player, attachment);
    }
}
