package com.splitscale.ditabys.repositories;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.splitscale.ditabys.driver.DatabaseDriver;
import com.splitscale.ditabys.driver.StoreDbDriver;
import com.splitscale.fordastore.core.container.Container;
import com.splitscale.fordastore.core.repositories.ContainerRepository;

public class ContainerRepositoryInteractor implements ContainerRepository {
  DatabaseDriver db;

  public ContainerRepositoryInteractor() {
    this.db = new StoreDbDriver();
  }

  @Override
  public Container add(String containerName, String uid) throws IOException {
    final String query = "INSERT INTO container (container_id, container_title, user_id) VALUES (null,?,UUID_TO_BIN(?))";

    Container container = new Container();
    container.setUid(uid);
    container.setName(containerName);

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      pstmt.setString(1, containerName);
      pstmt.setString(2, uid);

      pstmt.executeUpdate();

      ResultSet rs = pstmt.getGeneratedKeys();

      if (rs.next()) {
        long id = rs.getLong(1);

        container.setContainerID(id);
      }

      conn.close();
      return container;
    } catch (SQLException e) {
      throw new IOException("Could not add a new container to database" + e.getMessage());
    }
  }

  @Override
  public void delete(Long containerId) throws IOException {
    String query = "DELETE FROM container WHERE container_id = ?";

    try {
      Connection conn = db.getConnection();

      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setLong(1, containerId);

      pstmt.executeUpdate();

      conn.close();

    } catch (SQLException e) {
      throw new IOException("Could not delete container" + e.getMessage());
    }
  }

  @Override
  public Container getByContainerID(long containerId) throws IOException {
    final String query = "Select * From container WHERE container_id = ?";
    Container container = new Container();

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setLong(1, containerId);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        container.setContainerID(rs.getLong("container_id"));
        container.setName(rs.getString("container_title"));
      }
      conn.close();
      return container;
    } catch (SQLException e) {
      throw new IOException("Could not find this container ID");
    }
  }

  @Override
  public Container getByUid(String userId) throws IOException {
    final String query = "Select * From user WHERE user_id = UUID_TO_BIN(?)";
    Container container = new Container();

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, userId);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        container.setUid(rs.getString("user_id"));
      }

      conn.close();
      return container;

    } catch (SQLException e) {
      throw new IOException("Could not find this UID");
    }
  }

  @Override
  public List<Container> getListByName(String containerTitle) throws IOException {
    final String query = "SELECT * FROM container WHERE container_title LIKE ?";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, "%" + containerTitle + "%");
      ResultSet rs = pstmt.executeQuery();
      List<Container> containers = new ArrayList<>();

      while (rs.next()) {
        Container container = new Container();
        container.setName(rs.getString("container_title"));
        containers.add(container);
      }
      conn.close();
      return containers;
    } catch (SQLException e) {
      throw new IOException("Could not find any list of this container title");
    }
  }

  @Override
  public List<Container> getListByUid(String uid) throws IOException {
    final String query = "SELECT container_id, BIN_TO_UUID(user_id) as user_id, container_title FROM container WHERE user_id = UUID_TO_BIN(?)";

    try {
      Connection conn = db.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, uid);

      ResultSet rs = pstmt.executeQuery();

      List<Container> containers = new ArrayList<>();

      while (rs.next()) {
        Container container = new Container();

        container.setContainerID(rs.getLong("container_id"));
        container.setName(rs.getString("container_title"));
        container.setUid(rs.getString("user_id"));

        containers.add(container);
      }

      conn.close();
      return containers;
    } catch (SQLException e) {
      throw new IOException("Could not get a list of this UID: " + e.getMessage());
    }
  }

  @Override
  public void update(String containerName, Long containerId) throws IOException {
    final String query = "UPDATE container SET container_title = ? WHERE container_id = ?";

    try {
      Connection conn = db.getConnection();

      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, containerName);
      pstmt.setLong(2, containerId);
      pstmt.executeUpdate();

      conn.close();
    } catch (SQLException e) {
      throw new IOException("Unable to update Container");
    }
  }
}
