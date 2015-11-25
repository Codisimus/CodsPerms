package com.codisimus.plugins.codsperms;

import java.util.List;
import java.util.Map;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * A PermissionStorageHandler handles the storage of permission nodes for each Player
 * The PermissionStorageHandler defines how permissions are saved and loaded
 *
 * @author Codisimus
 */
public interface PermissionStorageHandler {
    /**
     * Loads Permissions for each online Player
     *
     * @return The Map of Player UUIDs and their loaded Permissions
     */
    abstract Map<String, List<PermissionNode>> loadPermissions();

    /**
     * Loads Permissions for the given Player
     *
     * @param player The given Player
     * @return The List of Permissions which were loaded
     */
    abstract List<PermissionNode> loadPermissions(Player player);

    /**
     * Adds a List of Permissions for the given Player
     *
     * @param oPlayer The OfflinePlayer to give Permissions to
     * @param nodeList The List of PermissionNodes to give
     */
    abstract void addPermissions(OfflinePlayer oPlayer, List<PermissionNode> nodeList);

    /**
     * Adds a PermissionNode to the given Player
     *
     * @param oPlayer The OfflinePlayer to give the Permission to
     * @param node The PermissionNode to give
     */
    abstract void addPermission(OfflinePlayer oPlayer, PermissionNode node);

    /**
     * Removes all Permissions for the given Player
     *
     * @param oPlayer The given Player
     */
    abstract void removeAllPermissions(OfflinePlayer oPlayer);

    /**
     * Removes a List of Permissions for the given Player
     *
     * @param oPlayer The OfflinePlayer to remove Permissions for
     * @param nodeList The List of PermissionNodes to remove
     */
    abstract void removePermissions(OfflinePlayer oPlayer, List<PermissionNode> nodeList);

    /**
     * Removes a PermissionNode for the given Player
     *
     * @param oPlayer The OfflinePlayer to remove the Permission for
     * @param node The PermissionNode to remove
     */
    abstract void removePermission(OfflinePlayer oPlayer, PermissionNode node);
}
