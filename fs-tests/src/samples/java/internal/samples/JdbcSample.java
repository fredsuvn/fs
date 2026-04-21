package internal.samples;

import space.sunqian.fs.utils.jdbc.JdbcKit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Sample: JDBC Utilities Usage
 * <p>
 * Purpose: Demonstrate how to use the JDBC utilities provided by fs-core module.
 * <p>
 * Use Cases:
 * <ul>
 *   <li>
 *     JDBC connection management
 *   </li>
 *   <li>
 *     SQL statement execution
 *   </li>
 *   <li>
 *     Result set processing
 *   </li>
 * </ul>
 * <p>
 * Key Classes:
 * <ul>
 *   <li>
 *     {@link JdbcKit}: JDBC utilities for result set processing
 *   </li>
 * </ul>
 */
public class JdbcSample {

    public static void main(String[] args) {
        // Note: This is a demonstration only. You would need to provide actual database connection details.
        // For testing purposes, you can use an in-memory database like H2.

        String url = "jdbc:h2:mem:test";
        String username = "sa";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Connected to database");

            // Create table
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (id INT PRIMARY KEY, name VARCHAR(255))");
                System.out.println("Created table 'users'");

                // Insert data
                statement.executeUpdate("INSERT INTO users (id, name) VALUES (1, 'John'), (2, 'Jane')");
                System.out.println("Inserted data into 'users' table");

                // Query data
                System.out.println("Querying data from 'users' table:");
                try (ResultSet resultSet = statement.executeQuery("SELECT * FROM users")) {
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String name = resultSet.getString("name");
                        System.out.println("User: id=" + id + ", name=" + name);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}