package DB;

import Server.TCPServer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UsersManager {

    private Map<String, String> users = new HashMap<>();
    public static Map<String, String> colors = new HashMap<>();
    Connection connection;

    public UsersManager (ResultSet set1,ResultSet set2, Connection connection){
        this.connection = connection;

        String login;
        String password;
        String color;

        try{
            while(set1.next()){
                login = set1.getString(1);
                password = set1.getString(2);
                users.put(login,password);
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }

        try{
            while(set2.next()){
                login = set2.getString(1);
                color = set2.getString(2);
                colors.put(login,color);
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public synchronized boolean ifContainLogin (String login){
        if (users.containsKey(login)){
            return true;
        }else return false;
    }

    public synchronized void addNewUser  (String login, String password) throws SQLException{
        password = to_SHA1(password);
        users.put(login,password);
        TCPServer.dataBaseManager.add_user(login, password);
    }
    public synchronized void addNewColor (String login, String color) throws SQLException{
        colors.put(login,color);
        TCPServer.dataBaseManager.add_color(login, color);
    }

    public synchronized boolean checkPassword (String login, String password){
        password = to_SHA1(password);
        if (password.equals(users.get(login))){
            return true;
        } else return false;
    }

    public String to_SHA1 (String password) {

        MessageDigest mDigest = null;
        try {
            mDigest = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] result = mDigest.digest(password.getBytes());
        StringBuffer ps = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            ps.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return ps.toString();
    }

}
