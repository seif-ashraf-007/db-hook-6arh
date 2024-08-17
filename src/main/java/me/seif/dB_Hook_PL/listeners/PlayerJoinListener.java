package me.seif.dB_Hook_PL.listeners;

import me.seif.dB_Hook_PL.Database.DatabaseManager;
import me.seif.dB_Hook_PL.logger.PluginLogger;
import me.seif.dB_Hook_PL.models.BotTaskData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class PlayerJoinListener implements Listener {

    private final DatabaseManager databaseManager;
    private final PluginLogger logger;
    private final String[] REQUIRED_PERMISSIONS = {"group.vip", "group.vip-p", "group.vip-pp"};

    public PlayerJoinListener(DatabaseManager databaseManager, PluginLogger logger) {
        this.databaseManager = databaseManager;
        this.logger = logger;
    }

    // Method to check if a player has any of the required permissions
    private boolean hasAnyRequiredPermission(Player player) {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (player.hasPermission(permission)) {
                logger.log("Player " + player.getName() + " has permission: " + permission);
                return true;  // The player has one of the required permissions
            }
        }
        return false;  // The player does not have any of the required permissions
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        String playerName = player.getName();

        // Log player join event
        logger.log("Player joined: " + playerName + ", Starting operations ...");

        // Check if the player has any of the required permissions
        if (!hasAnyRequiredPermission(player)) {
            return;
        }


        // Retrieve all BotTask data for the player
        logger.log("Checking database for player: " + playerName);
        List<BotTaskData> taskDataList = databaseManager.getAllBotTaskDataForEvents(playerName);

        // Check if there are any tasks to process
        if (!taskDataList.isEmpty()) {
            logger.log("Found " + taskDataList.size() + " task/s for player: " + playerName);
            // Process each task
            for (BotTaskData botTaskData : taskDataList) {
                handleEvent(botTaskData);

                player.sendMessage(ChatColor.GREEN + "[Minecraft VIP] " +
                        ChatColor.GOLD + "You have been given " +
                        ChatColor.YELLOW + botTaskData.getArgs1() +
                        ChatColor.GOLD + " claims!");

                // After running the command, delete the row from the database using the args2 value
                boolean success = databaseManager.deleteBotTaskRow(botTaskData.getArgs2());
                if (success) {
                    logger.log("Successfully deleted the row with args2/ID: " + botTaskData.getArgs2() + " for username: " + playerName + " and event: " + botTaskData.getEvent());
                } else {
                    logger.error("Failed to delete the row with args2/ID: " + botTaskData.getArgs2() + " for username: " + playerName + " and event: " + botTaskData.getEvent());
                }
            }
        }
    }

    // Method to handle the event logic
    private void handleEvent(BotTaskData botTaskData) {
        String event = botTaskData.getEvent();
        String username = botTaskData.getUsername();
        String args1 = botTaskData.getArgs1();

        logger.log("Handling event: " + event + " for player: " + username);

        boolean argsRange = Integer.valueOf(args1) > 0 && Integer.valueOf(args1) <= 10000;
        switch (event) {
            case "addblock":
                if (argsRange) {
                    runCommand("rp addblock " + username + " " + Integer.valueOf(args1));
                }
                break;
            case "delblock":
                if (argsRange) {
                    runCommand("rp delblock " + username + " " + Integer.valueOf(args1));
                }
                break;
            default:
                // logging unknown events for debugging purposes
                logger.warn("Unknown event: " + event + " for user: " + username);
                break;
        }
    }

    private void runCommand(String command) {
        logger.log("Running command: " + command);
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}