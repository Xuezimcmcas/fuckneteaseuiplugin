package com.neteasemc.tpaaui;

import com.neteasemc.spigotmaster.SpigotMaster;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TpaaUI extends JavaPlugin {
    private SpigotMaster spigotMaster;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getCommand("opentpa").setExecutor((sender, command, label, args) -> {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                spigotMaster.notifyToClient(player, "tpaUI", "tpaUISS", "OpenTpaUI", new HashMap<>());
                return true;
            }
            return false;
        });

        spigotMaster = (SpigotMaster) getServer().getPluginManager().getPlugin("SpigotMaster");
        if (spigotMaster != null) {
            spigotMaster.listenForEvent("tpaUI", "tpaUISS", "RequestPlayerList", this::onRequestPlayerList);
            spigotMaster.listenForEvent("tpaUI", "tpaUISS", "PlayerTpaRequest", this::onPlayerTpaRequest);
        } else {
            getLogger().severe("没找到SpigotMaster插件，TpaaUI将不会工作.");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void onRequestPlayerList(Player player, Map<String, Object> data) {
        List<String> onlinePlayerNames = new ArrayList<>();
        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
            onlinePlayerNames.add(onlinePlayer.getName());
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("playerList", onlinePlayerNames);
        spigotMaster.notifyToClient(player, "tpaUI", "tpaUICS", "UpdatePlayerList", payload);
    }

    private void onPlayerTpaRequest(Player player, Map<String, Object> data) {
        String targetPlayerName = (String) data.get("playerName");
        if (targetPlayerName != null && !targetPlayerName.isEmpty()) {
            player.performCommand("tpa " + targetPlayerName);
        }
    }
}
