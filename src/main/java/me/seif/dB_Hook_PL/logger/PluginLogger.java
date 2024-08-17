package me.seif.dB_Hook_PL.logger;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PluginLogger {

    private final Plugin plugin;
    private PrintWriter logWriter;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final DateTimeFormatter fileNameFormatter = DateTimeFormatter.ofPattern("MM-yyyy"); // Formatter for "month-year" format
    private String currentMonthYear;  // To track the current month and year

    public PluginLogger(Plugin plugin) {
        this.plugin = plugin;
        setupLogFile();
    }

    // Setup the log file with the format "dbhook_MM-yyyy.log"
    private void setupLogFile() {
        try {
            // Ensure the plugin's data folder exists
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                if (dataFolder.mkdirs()) {
                    plugin.getLogger().info("Created plugin data folder: " + dataFolder.getPath());
                } else {
                    plugin.getLogger().severe("Failed to create plugin data folder: " + dataFolder.getPath());
                    return;
                }
            }

            // Generate the file name based on the current month and year
            currentMonthYear = LocalDateTime.now().format(fileNameFormatter);
            File logFile = new File(dataFolder, "dbhook_" + currentMonthYear + ".log");

            // Set up the log file
            logWriter = new PrintWriter(new FileWriter(logFile, true), true);  // Append to the log file

            // Log the creation of the new log file
            String logCreationMessage = "Created new log file: dbhook_" + currentMonthYear + ".log";
            plugin.getLogger().info(logCreationMessage);  // Log to the console
            log(logCreationMessage);  // Log to the new file

        } catch (IOException e) {
            plugin.getLogger().severe("Failed to set up the log file: " + e.getMessage());
        }
    }

    // Check if the log file needs to be updated (e.g., new month)
    private void checkAndUpdateLogFile() {
        String newMonthYear = LocalDateTime.now().format(fileNameFormatter);
        if (!newMonthYear.equals(currentMonthYear)) {
            // Month has changed, so close the current log file and create a new one
            close();
            currentMonthYear = newMonthYear;
            setupLogFile();
        }
    }

    // Log a message
    public void log(String message) {
        checkAndUpdateLogFile();  // Ensure log file is up to date
        String timestamp = LocalDateTime.now().format(dateFormatter);
        String logMessage = "[" + timestamp + "] " + message;

        // Log to the console
        plugin.getLogger().info(logMessage);

        // Log to the file
        if (logWriter != null) {
            logWriter.println(logMessage);
        }
    }

    // Log a warning message
    public void warn(String message) {
        checkAndUpdateLogFile();  // Ensure log file is up to date
        String timestamp = LocalDateTime.now().format(dateFormatter);
        String logMessage = "[" + timestamp + "] WARNING: " + message;

        // Log to the console
        plugin.getLogger().warning(logMessage);

        // Log to the file
        if (logWriter != null) {
            logWriter.println(logMessage);
        }
    }

    // Log an error message
    public void error(String message) {
        checkAndUpdateLogFile();  // Ensure log file is up to date
        String timestamp = LocalDateTime.now().format(dateFormatter);
        String logMessage = "[" + timestamp + "] ERROR: " + message;

        // Log to the console
        plugin.getLogger().severe(logMessage);

        // Log to the file
        if (logWriter != null) {
            logWriter.println(logMessage);
        }
    }

    // Close the log file
    public void close() {
        if (logWriter != null) {
            logWriter.close();
        }
    }
}