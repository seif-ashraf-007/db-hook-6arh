package me.seif.dB_Hook_PL.Database;

import me.seif.dB_Hook_PL.models.BotTaskData;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private Connection connection;
    private final Plugin plugin;
    static String getQuery = "SELECT * FROM BotTask WHERE username = ?";

    public DatabaseManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean connect() {
        String host = "";
        String port = "";
        String database = "";
        String user = "";
        String password = "";

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database;

        try {
            connection = DriverManager.getConnection(url, user, password);
            return true;
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not connect to the database: " + e.getMessage());
            return false;
        }
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                plugin.getLogger().severe("Could not close the database connection: " + e.getMessage());
            }
        }
    }

    public boolean isConnected() {
        return connection != null;
    }

    public Connection getConnection() {
        return connection;
    }

    // Method to retrieve all rows for a specific user where the event is "addblock" or "delblock"
    public List<BotTaskData> getAllBotTaskDataForEvents(String username) {
        List<BotTaskData> taskDataList = new ArrayList<>();
        String query = "SELECT event, username, args1, args2 FROM BotTask WHERE username = ? AND (event = 'addblock' OR event = 'delblock')";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    // Retrieve data from the result set
                    String event = resultSet.getString("event");
                    String args1 = resultSet.getString("args1");
                    String args2 = resultSet.getString("args2");

                    // Add each row to the list as a BotTaskData object
                    taskDataList.add(new BotTaskData(event, username, args1, args2));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error while retrieving data from the database: " + e.getMessage());
        }

        return taskDataList;
    }

    // Method to delete a row from the BotTask table
    public boolean deleteBotTaskRow(String args2) {
        String query = "DELETE FROM BotTask WHERE args2 = ? LIMIT 1";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, args2);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0; // Return true if a row was deleted
        } catch (SQLException e) {
            plugin.getLogger().severe("Error while deleting row from the database: " + e.getMessage());
            return false;
        }
    }
}
