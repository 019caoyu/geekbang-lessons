package org.geektimes.projects.user.web.listener;

import org.geektimes.projects.user.repository.DatabaseUserRepository;
import org.geektimes.projects.user.sql.DBConnectionManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@WebListener
public class DBConnectionInitializerListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Driver driver = DriverManager.getDriver("jdbc:derby:/db/user-platform;create=true");
            Connection connection = driver.connect("jdbc:derby:/db/user-platform;create=true", new Properties());

            DBConnectionManager dbConnectionManager = new DBConnectionManager();
            dbConnectionManager.setConnection(connection);

            DatabaseUserRepository databaseUserRepository = new DatabaseUserRepository(dbConnectionManager);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
