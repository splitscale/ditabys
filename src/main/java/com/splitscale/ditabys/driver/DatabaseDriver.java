package com.splitscale.ditabys.driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.splitscale.ditabys.config.DBconfig;

public class DatabaseDriver {

  private DBconfig config;

  public DatabaseDriver(DBconfig config) {
    this.config = config;
  }

  public Connection getConnection() throws SQLException {
    // assign db parameters
    String url = config.getUrl();
    String user = config.getUsername();
    String password = config.getPassword();

    System.out.println("Using url: " + url);

    // create a connection to the database
    return DriverManager.getConnection(url, user, password);
  }

}
