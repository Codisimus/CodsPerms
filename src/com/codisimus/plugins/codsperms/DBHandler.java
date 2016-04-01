package com.codisimus.plugins.codsperms;

import com.codisimus.database.util.MySQLConnection;
import com.codisimus.database.util.Query;
import com.codisimus.database.util.QueryBuilder;
import com.codisimus.database.util.QueryBuilder.Where;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Stores Permissions in a MySQL Database
 *
 * @author Codisimus
 */
public class DBHandler implements PermissionStorageHandler {
    private static final String PERMISSION_TABLE = "permissions";
    private static final String PLAYER_UUID_COLUMN = "player_uuid";
    private static final String NODE_KEY_COLUMN = "node";
    private static final String NODE_VALUE_COLUMN = "value";
    private static final String[] PLAYER_KEY_VALUE_COLUMNS = new String[]{PLAYER_UUID_COLUMN, NODE_KEY_COLUMN, NODE_VALUE_COLUMN};
    private static final String[] KEY_VALUE_COLUMNS = new String[]{NODE_KEY_COLUMN, NODE_VALUE_COLUMN};
    private static final Query GET_PLAYER_PERMS_QUERY = QueryBuilder.select(PERMISSION_TABLE, KEY_VALUE_COLUMNS, new Where("player", "?"));
    private static final Query ADD_PERMISSION_QUERY = QueryBuilder.insert(PERMISSION_TABLE, PLAYER_KEY_VALUE_COLUMNS, new String[]{"?", "?", "?"}, true);
    private static final Query DELETE_ALL_PERMISSIONS_QUERY = QueryBuilder.delete(PERMISSION_TABLE, new Where(PLAYER_UUID_COLUMN, "?"));
    
    private final MySQLConnection conn;

    public DBHandler(MySQLConnection conn) {
        this.conn = conn;
    }

    @Override
    public Map<String, List<PermissionNode>> loadPermissions() {
        HashMap<String, List<PermissionNode>> permissionsMap = new HashMap<>();
        try {
            Object[] uuids = new String[Bukkit.getOnlinePlayers().size()];
            int index = 0;
            for (Player player : Bukkit.getOnlinePlayers()) {
                uuids[index++] = player.getUniqueId().toString();
            }
            Query query = QueryBuilder.select(PERMISSION_TABLE, PLAYER_KEY_VALUE_COLUMNS, new Where("player", uuids));
            ResultSet resultSet = conn.select(query);
            while (resultSet.next()) {
                String playerUUID = resultSet.getString(PLAYER_UUID_COLUMN);
                if (!permissionsMap.containsKey(playerUUID)) {
                    permissionsMap.put(playerUUID, new LinkedList<PermissionNode>());
                }
                List<PermissionNode> nodes = permissionsMap.get(playerUUID);
                nodes.add(getPermissionNode(resultSet));
            }
        } catch (SQLException ex) {
            CodsPerms.logger.log(Level.SEVERE, "Could not load permissions from database", ex);
        }
        return permissionsMap;
    }

    @Override
    public List<PermissionNode> loadPermissions(Player player) {
        List<PermissionNode> nodeList = new LinkedList<>();
        try {
            GET_PLAYER_PERMS_QUERY.setQueryValues(player.getUniqueId().toString());
            ResultSet resultSet = conn.select(GET_PLAYER_PERMS_QUERY);
            while (resultSet.next()) {
                nodeList.add(getPermissionNode(resultSet));
            }
        } catch (SQLException ex) {
            CodsPerms.logger.log(Level.SEVERE, "Could not load permissions from database", ex);
        }
        return nodeList;
    }

    @Override
    public void addPermissions(OfflinePlayer oPlayer, List<PermissionNode> nodeList) {
        for (PermissionNode node : nodeList) {
            addPermission(oPlayer, node);
        }
    }

    @Override
    public void addPermission(OfflinePlayer oPlayer, PermissionNode node) {
        ADD_PERMISSION_QUERY.setQueryValues(oPlayer.getUniqueId().toString(), node.getName(), node.getValue());
        conn.asyncQuery(ADD_PERMISSION_QUERY);
    }

    @Override
    public void removeAllPermissions(OfflinePlayer oPlayer) {
        DELETE_ALL_PERMISSIONS_QUERY.setQueryValues(oPlayer.getUniqueId().toString());
        conn.asyncQuery(DELETE_ALL_PERMISSIONS_QUERY);
    }

    @Override
    public void removePermissions(OfflinePlayer oPlayer, List<PermissionNode> nodeList) {
        Object[] nodeStrings = new String[nodeList.size()];
        int index = 0;
        for (PermissionNode node : nodeList) {
            nodeStrings[index++] = node.getName();
        }
        Where where = new Where(PLAYER_UUID_COLUMN, oPlayer.getUniqueId().toString(), NODE_KEY_COLUMN, nodeStrings);
        Query query = QueryBuilder.delete(PERMISSION_TABLE, where);
        conn.asyncQuery(query);
    }

    @Override
    public void removePermission(OfflinePlayer oPlayer, PermissionNode node) {
        Where where = new Where(PLAYER_UUID_COLUMN, oPlayer.getUniqueId().toString(), NODE_KEY_COLUMN, node.getName());
        Query query = QueryBuilder.delete(PERMISSION_TABLE, where);
        conn.asyncQuery(query);
    }

    private static PermissionNode getPermissionNode(ResultSet resultSet) throws SQLException {
        return new PermissionNode(resultSet.getString(NODE_KEY_COLUMN), resultSet.getBoolean(NODE_VALUE_COLUMN));
    }
}
