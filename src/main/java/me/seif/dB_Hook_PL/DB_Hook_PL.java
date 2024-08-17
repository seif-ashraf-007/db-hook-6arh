package me.seif.dB_Hook_PL;

import me.seif.dB_Hook_PL.Database.DatabaseManager;
import me.seif.dB_Hook_PL.logger.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;
import me.seif.dB_Hook_PL.listeners.PlayerJoinListener;

public final class DB_Hook_PL extends JavaPlugin {

    private DatabaseManager databaseManager;
    private PluginLogger pluginLogger;

    @Override
    public void onEnable() {

        // Initialize the plugin logger
        pluginLogger = new PluginLogger(this);
        pluginLogger.log("Plugin is starting up...");

        // Plugin startup logic
        databaseManager = new DatabaseManager(this);

        if (databaseManager.connect()) {
            pluginLogger.log("Database connection established successfully.");

            // Register the PlayerJoinListener
            getServer().getPluginManager().registerEvents(new PlayerJoinListener(databaseManager, pluginLogger), this);

        } else {
            pluginLogger.error("Failed to establish a database connection.");
        }
    }

    @Override
    public void onDisable() {
        pluginLogger.log("Plugin is shutting down...");

        // Plugin shutdown logic
        if (databaseManager != null && databaseManager.isConnected()) {
            databaseManager.disconnect();
            pluginLogger.log("Database connection closed.");
        }

        // Close the log file
        pluginLogger.close();
    }

    public PluginLogger getPluginLogger() {
        return pluginLogger;
    }
}
