package DB;

import ShortyClasses.Shorty;

import java.sql.*;

public class DataBaseManager {

    Connection connection;
    Statement statement;

    public DataBaseManager (Connection connection) {
        this.connection = connection;
    }

    public synchronized int add(Shorty shorty) throws SQLException{
        int id = -1;

        String add_to_Shorty = "INSERT into Shorty (name, user_login, status_id, mass, creation_time)  VALUES (?, ?, ?, ?, ?) RETURNING shorty_id";
        PreparedStatement prep  = connection.prepareStatement(add_to_Shorty);

        prep.setString(1, shorty.getName());
        prep.setString(2, shorty.getUser_login());
        prep.setInt(3, shorty.getStatus().ordinal());
        prep.setDouble(4, shorty.getMass());
        prep.setTimestamp(5, Timestamp.valueOf(shorty.getTimeOfCreation()));

        ResultSet set = prep.executeQuery();

        if (set.next()){
            id = set.getInt(1);
        }

        String add_to_Money = "INSERT into Money(shorty_id, amount, currency_id)  VALUES (?, ?, ?)";
        PreparedStatement prep1  = connection.prepareStatement(add_to_Money);

        prep1.setInt(1, id);
        prep1.setInt(2, shorty.getBudget().getAmount());
        prep1.setInt(3, shorty.getBudget().getCurrency().ordinal());

        prep1.executeUpdate();



        String add_to_Clothes = "INSERT into Clothes(shorty_id, colour_id, type_id)  VALUES (?, ?, ?)";
        PreparedStatement prep2  = connection.prepareStatement(add_to_Clothes);

        prep2.setInt(1, id);
        prep2.setInt(2, shorty.getLook().getColour().ordinal());
        prep2.setInt(3, shorty.getLook().getType().ordinal());

        prep2.executeUpdate();

        String add_to_Coords = "INSERT into Coords(shorty_id, x, y)  VALUES (?, ?, ?)";
        PreparedStatement prep3  = connection.prepareStatement(add_to_Coords);


        prep3.setInt(1, id);
        prep3.setDouble(2, shorty.getCoords().getX());
        prep3.setDouble(3, shorty.getCoords().getY());

        prep3.executeUpdate();

        prep.closeOnCompletion();
        prep1.closeOnCompletion();
        prep2.closeOnCompletion();
        prep3.closeOnCompletion();

        return id;
    }

    public synchronized void remove (int id) throws SQLException{

        String delete_from_Shorty = "DELETE from Shorty WHERE shorty_id = ?;";
        PreparedStatement prep  = connection.prepareStatement(delete_from_Shorty);
        prep.setInt(1,id);
        prep.executeUpdate();

        String delete_from_Money = "DELETE from Money WHERE shorty_id = ?;";
        PreparedStatement prep1  = connection.prepareStatement(delete_from_Money);
        prep1.setInt(1,id);
        prep1.executeUpdate();

        String delete_from_Clothes = "DELETE from Clothes WHERE shorty_id = ?;";
        PreparedStatement prep2  = connection.prepareStatement(delete_from_Clothes);
        prep2.setInt(1,id);
        prep2.executeUpdate();

        String delete_from_Coords = "DELETE from Coords WHERE shorty_id = ?;";
        PreparedStatement prep3  = connection.prepareStatement(delete_from_Coords);
        prep3.setInt(1,id);
        prep3.executeUpdate();

        prep.closeOnCompletion();
        prep1.closeOnCompletion();
        prep2.closeOnCompletion();
        prep3.closeOnCompletion();
    }

    public synchronized void add_user (String login, String password) throws SQLException{
        String add_to_Shorty_users = "INSERT into ShortyUsers(user_login, password)  VALUES (?, ?)";
        PreparedStatement prep  = connection.prepareStatement(add_to_Shorty_users);

        prep.setString(1, login);
        prep.setString(2, password);

        prep.executeUpdate();

        prep.closeOnCompletion();
    }

    public synchronized void add_color (String login, String color) throws SQLException{
        String add_to_Shorty_colors = "INSERT into ShortyColors(user_login, color)  VALUES (?, ?)";
        PreparedStatement prep  = connection.prepareStatement(add_to_Shorty_colors);

        prep.setString(1, login);
        prep.setString(2, color);

        prep.executeUpdate();

        prep.closeOnCompletion();
    }

}
