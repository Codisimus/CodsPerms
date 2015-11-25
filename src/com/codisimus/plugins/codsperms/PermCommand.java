package com.codisimus.plugins.codsperms;

import com.codisimus.plugins.codsperms.CommandHandler.CodCommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/**
 * Executes Player Commands
 *
 * @author Codisimus
 */
public class PermCommand {

    @CodCommand(
        command = "give",
        weight = 1,
        aliases = {"add", "addNode", "node"},
        usage = {
            "§2<command> §f<§6Player§f> <§6Node§f> [§2true§f|§2false§f] =§b Give a Player the specified permmission node"
        }
    )
    public boolean addNode(CommandSender sender, OfflinePlayer player, String node) {
        PermissionAPI.addPermission(player, new PermissionNode(node, true));
        sender.sendMessage("§6" + player.getName() + "§5 now has node §6" + node);
        return true;
    }
    @CodCommand(command = "give", weight = 1.1)
    public boolean addNode(CommandSender sender, OfflinePlayer player, String node, boolean value) {
        PermissionAPI.addPermission(player, new PermissionNode(node, value));
        sender.sendMessage("§6" + player.getName() + "§5 now has node §6" + node + ":" + value);
        return true;
    }

    @CodCommand(
        command = "take",
        weight = 2,
        aliases = {"remove", "removeNode"},
        usage = {
            "§2<command> §f<§6Player§f>  <§6Node§f> =§b Take the specified permmission node from a Player"
        }
    )
    public boolean removeNode(CommandSender sender, OfflinePlayer player, String node) {
        PermissionAPI.removePermission(player, new PermissionNode(node));
        sender.sendMessage("§6" + player.getName() + "§5 no longer has node §6" + node);
        return true;
    }
}