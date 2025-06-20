package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:task_management.db";
    private static Connection connection;
    
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                createTables();
            }
        } catch (SQLException e) {
            String errorMessage = "Koneksi ke database gagal: " + e.getMessage();
       
           JOptionPane.showMessageDialog(
                null,
                errorMessage,
                "Kesalahan Koneksi Database",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
        return connection;
    }

    private static void createTables() {
        try (Statement stmt = connection.createStatement()) {
            // Create Users table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Users (
                    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL
                )
            """);

            // Create Courses table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Courses (
                    course_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    course_name TEXT NOT NULL,
                    user_id INTEGER NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES Users(user_id)
                )
            """);

            // Create AcademicTasks table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS AcademicTasks (
                    task_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    description TEXT,
                    deadline DATE,
                    status TEXT,
                    course_id INTEGER NOT NULL,
                    FOREIGN KEY (course_id) REFERENCES Courses(course_id)
                )
            """);

            // Create PersonalTasks table dengan auto increment mulai dari 1000
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS PersonalTasks (
                    personal_task_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    description TEXT,
                    category TEXT,
                    deadline DATE,
                    status TEXT,
                    user_id INTEGER NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES Users(user_id)
                )
            """);

            // Set auto increment start value untuk PersonalTasks
            stmt.execute("UPDATE sqlite_sequence SET seq = 999 WHERE name = 'PersonalTasks'");

            // Jika belum ada entry di sqlite_sequence, insert manual
            stmt.execute("""
                INSERT OR IGNORE INTO sqlite_sequence (name, seq) 
                VALUES ('PersonalTasks', 999)
            """);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}