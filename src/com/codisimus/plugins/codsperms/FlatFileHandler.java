package com.codisimus.plugins.codsperms;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * Stores Permissions in a YAML file within the plugin's data folder
 *
 * @author Codisimus
 */
public class FlatFileHandler implements PermissionStorageHandler {
    private final File yamlFile;

    public FlatFileHandler(File yamlFile) {
        this.yamlFile = yamlFile;
    }

    @Override
    public Map<String, List<PermissionNode>> loadPermissions() {
        HashMap<String, List<PermissionNode>> permissionsMap = new HashMap<>();
        YamlConfiguration yaml = loadYAML();
        for (Player player : Bukkit.getOnlinePlayers()) {
            List<PermissionNode> nodeList = loadPermissions(player, yaml);
            permissionsMap.put(player.getUniqueId().toString(), nodeList);
        }
        return permissionsMap;
    }

    @Override
    public List<PermissionNode> loadPermissions(Player player) {
        return loadPermissions(player, loadYAML());
    }

    private static List<PermissionNode> loadPermissions(Player player, YamlConfiguration yaml) {
        List<PermissionNode> nodeList = new LinkedList<>();
        ConfigurationSection section = getConfigurationSection(player, yaml);
        for (String key : section.getKeys(false)) {
            nodeList.add(new PermissionNode(key, section.getBoolean(key)));
        }
        return nodeList;
    }

    @Override
    public void addPermissions(OfflinePlayer oPlayer, List<PermissionNode> nodeList) {
        YamlConfiguration yaml = loadYAML();
        ConfigurationSection section = getConfigurationSection(oPlayer, yaml);
        for (PermissionNode node : nodeList) {
            section.set(node.getName(), node.getValue());
        }
        saveYAML(yaml);
    }

    @Override
    public void addPermission(OfflinePlayer oPlayer, PermissionNode node) {
        YamlConfiguration yaml = loadYAML();
        ConfigurationSection section = getConfigurationSection(oPlayer, yaml);
        section.set(node.getName(), node.getValue());
        saveYAML(yaml);
    }

    @Override
    public void removeAllPermissions(OfflinePlayer oPlayer) {
        YamlConfiguration yaml = loadYAML();
        ConfigurationSection section = yaml.getConfigurationSection(oPlayer.getUniqueId().toString());
        if (section != null) {
            yaml.set(oPlayer.getUniqueId().toString(), null);
        }
        saveYAML(yaml);
    }

    @Override
    public void removePermissions(OfflinePlayer oPlayer, List<PermissionNode> nodeList) {
        YamlConfiguration yaml = loadYAML();
        ConfigurationSection section = getConfigurationSection(oPlayer, yaml);
        for (PermissionNode node : nodeList) {
            section.set(node.getName(), null);
        }
        if (section.getKeys(false).isEmpty()) {
            yaml.set(oPlayer.getUniqueId().toString(), null);
        }
        saveYAML(yaml);
    }

    @Override
    public void removePermission(OfflinePlayer oPlayer, PermissionNode node) {
        YamlConfiguration yaml = loadYAML();
        ConfigurationSection section = getConfigurationSection(oPlayer, yaml);
        section.set(node.getName(), null);
        if (section.getKeys(false).isEmpty()) {
            yaml.set(oPlayer.getUniqueId().toString(), null);
        }
        saveYAML(yaml);
    }

    private YamlConfiguration loadYAML() {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.options().pathSeparator('/');
        if (yamlFile.exists()) {
            try {
                yaml.load(yamlFile);
            } catch (IOException | InvalidConfigurationException ex) {
                CodsPerms.logger.log(Level.SEVERE, "Failed to load " + yamlFile.getName(), ex);
            }
        }
        return yaml;
    }

    private void saveYAML(YamlConfiguration yaml) {
        try {
            yaml.save(yamlFile);
        } catch (IOException ex) {
            CodsPerms.logger.log(Level.SEVERE, "Failed to save permissions to {0}", yamlFile.getPath());
        }
    }

    private static ConfigurationSection getConfigurationSection(OfflinePlayer oPlayer, YamlConfiguration yaml) {
        String uuid = oPlayer.getUniqueId().toString();
        ConfigurationSection section = yaml.getConfigurationSection(uuid);
        if (section == null) {
            section = yaml.createSection(uuid);
        }
        return section;
    }
}
