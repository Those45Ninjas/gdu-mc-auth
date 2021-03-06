package com.those45ninjas.gduAuth.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

public class User {

    // The minecraft UUID of the user. In the database this is stored as BINARY(16)
    // and must be converted using mysql's BIN_TO_UUID and UUID_TO_BIN functions.
    public UUID uuid;

    // The minecraft username.
    public String minecraftName;

    // The user's mixer ID and username.
    public long mixerID;
    public String mixerName;

    // The last time a successfull connection was made.
    public Timestamp lastLogin;

    // The time this user was added to the database.
    public Timestamp created;

    // Create a new user with a username and UUID
    public User(String mcName, UUID uuid)
    {
        this.uuid = uuid;
        minecraftName = mcName;
    }

    // Create a new user object from nothing.
    public User()
    {
        // status = Status.ERROR;
    }
    // Gets a user from the database, of none exist with the specified UUID, null is returned.
    public static User GetUser(UUID uuid, Connection connection) throws SQLException
    {
        // Get the user from the database (converting the binarry UUID)
        PreparedStatement statement = connection.prepareStatement("SELECT BIN_TO_UUID(UUID) as UUID, mixerID, minecraftName, mixerName, lastLogin, created FROM users where UUID = UUID_TO_BIN(?)");
        statement.setString(1, uuid.toString());
        
        ResultSet resultSet = statement.executeQuery();

        // If we don't have any matches retrun null.
        if(!resultSet.next())
            return null;

        // We have a match. Add the details to a new user object.
        User user = new User();
        user.uuid = UUID.fromString(resultSet.getString("UUID"));
        user.mixerID = resultSet.getInt("mixerID");
        user.minecraftName = resultSet.getString("minecraftName");
        user.mixerName = resultSet.getString("mixerName");
        user.lastLogin = resultSet.getTimestamp("lastLogin");
        user.created = resultSet.getTimestamp("created");

        // We are done here, finish up by retrunning the user object.
        return user;
    }
    public static void AddNewUser(String username, UUID uniqueID, Connection connection) throws SQLException
    {
        String sql = "INSERT INTO users " +
        "(UUID, minecraftName) " +
        "VALUES " +
        "(UUID_TO_BIN(?),?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, uniqueID.toString());
        statement.setString(2, username);

        statement.execute();
    }

    public void Update(Connection connection) throws SQLException
    {
        String sql = "UPDATE users " +
        "SET minecraftName = ?, " +
        "mixerId = ?, " +
        "mixerName = ? " +
        "WHERE UUID = UUID_TO_BIN(?)";

        PreparedStatement stm = connection.prepareStatement(sql);
        stm.setString(1, minecraftName);
        stm.setLong(2, mixerID);
        stm.setString(3, mixerName);
        stm.setString(4, uuid.toString());

        stm.executeUpdate();
    }
    public void UpdateLastLogin(Connection connection) throws SQLException
    {
        PreparedStatement stm = connection.prepareStatement("UPDATE users SET lastLogin = NOW() WHERE UUID = UUID_TO_BIN(?)");
        stm.setString(1, uuid.toString());

        stm.executeUpdate();
    }
}