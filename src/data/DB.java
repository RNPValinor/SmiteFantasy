package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Rob on 08/03/2015.
 */
public class DB {

    private Connection connection = null;

    private static DB instance = null;

    private DB() {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://127.0.0.1:5432/smite", "postgres", "somerhill"
            );
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(2);
        }

    }

    public static DB getInstance() {
        if (instance == null) {
            instance = new DB();
        } return instance;
    }

    public boolean inDatabase(String name) {
        Statement stmt = null;
        boolean found = false;
        try {
            stmt = connection.createStatement();
            String sql = "SELECT COUNT(*) AS count FROM players WHERE name='" + name + "'";
            ResultSet result = stmt.executeQuery(sql);
            while (result.next()) {
                if (result.getInt("count") > 0) {
                    found = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {}
        }

        return found;
    }

    public void add(Player p) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            String sql = "INSERT INTO players (name, position, team, score, price) ";
            sql += "VALUES ('" + p.getName() + "', " + p.getPosition().ordinal() + ", " + p.getTeam().ordinal() + ", ";
            sql += "'" + p.getScores().toString() + "', '" + p.getValue().toString() + "')";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
            } catch (SQLException e) {}
        }
    }

    public void update(Player p) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            String sql = "UPDATE players SET score='" + p.getScores().toString() + "', price='" + p.getValue().toString() + "' ";
            sql += "WHERE name='" + p.getName() + "'";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
            } catch (SQLException e) {}
        }
    }

    public Player load(String name) {
        Statement stmt = null;
        Player p = new Player();
        try {
            stmt = connection.createStatement();
            String sql = "SELECT * FROM players WHERE name='" + name + "'";
            ResultSet result = stmt.executeQuery(sql);
            while (result.next()) {
                p.setName(name);
                p.setTeam(Team.values()[result.getInt("team")]);
                p.setPosition(Position.values()[result.getInt("position")]);
                String scores = result.getString("score");
                scores = scores.substring(1, scores.length() - 1);
                p.setScores(convListDub(Arrays.asList(scores.split(","))));
                String values = result.getString("price");
                values = values.substring(1, values.length() - 1);
                p.setValues(convListInt(Arrays.asList(values.split(","))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
            } catch (SQLException e) {}
        }
        return p;
    }

    public List<Player> loadAllPlayers() {
        Statement stmt = null;
        List<Player> players = new LinkedList<Player>();
        try {
            stmt = connection.createStatement();
            String sql = "SELECT * FROM players ORDER BY position";
            ResultSet result = stmt.executeQuery(sql);
            while (result.next()) {
                Player p = new Player();
                p.setName(result.getString("name"));
                p.setTeam(Team.values()[result.getInt("team")]);
                p.setPosition(Position.values()[result.getInt("position")]);
                String scores = result.getString("score");
                scores = scores.substring(1, scores.length() - 1);
                p.setScores(convListDub(Arrays.asList(scores.split(","))));
                String values = result.getString("price");
                values = values.substring(1, values.length() - 1);
                p.setValues(convListInt(Arrays.asList(values.split(","))));

                players.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
            } catch (SQLException e) {}
        }
        return players;
    }

    private List<Integer> convListInt(List<String> l) {
        List<Integer> newList = new ArrayList<Integer>();
        for (String s : l) {
            s = s.trim();
            newList.add(Integer.parseInt(s));
        }
        return newList;
    }

    private List<Integer> convListDub(List<String> l) {
        List<Integer> newList = new ArrayList<Integer>();
        for (String s : l) {
            s = s.trim();
            if (!s.isEmpty()) {
                newList.add((int) Math.round(Double.parseDouble(s)));
            }
        }
        return newList;
    }

}
