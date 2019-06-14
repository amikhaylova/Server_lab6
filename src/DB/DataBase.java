package DB;

import java.sql.*;

public class DataBase {
    Connection connection = null;
    //  Database credentials
    /*static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    static final String USER = "postgres";
    static final String PASS = "rfrrhfjct";*/

    static final String DB_URL = "jdbc:postgresql://pg/studs";
    static final String USER = "s264446";
    static final String PASS = "lly481";

    public Connection connect(){
        System.out.println("Пытыюсь подключиться к PostgreSQL JDBC");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver не найден.");
            e.printStackTrace();

        }

        System.out.println("PostgreSQL JDBC Driver усешно подключен.");

        try {
            connection = DriverManager
                    .getConnection(DB_URL, USER, PASS);

        } catch (SQLException e) {
            System.out.println("Не удалось подключиться.");
            e.printStackTrace();

        }

        if (connection != null) {
            System.out.println("Успешно подключился к базе данных.");
        } else {
            System.out.println("Не удалось подключиться к базе данных.");
        }

        return connection;
    }

    public ResultSet create_tables(){
        ResultSet set = null;

        String [] request_array = {
                "CREATE TABLE IF NOT EXISTS Shorty(shorty_id SERIAL PRIMARY KEY, name VARCHAR, user_login VARCHAR, status_id INT, mass DECIMAL, creation_time TIMESTAMP);",
                "CREATE TABLE IF NOT EXISTS Money(shorty_id INT PRIMARY KEY, amount INT, currency_id INT);",
                "CREATE TABLE IF NOT EXISTS Currency(currency_id INT PRIMARY KEY, currency VARCHAR);",
                "CREATE TABLE IF NOT EXISTS SocialStatus(status_id INT PRIMARY KEY, status VARCHAR);",
                "CREATE TABLE IF NOT EXISTS Clothes(shorty_id INT  PRIMARY KEY, colour_id INT, type_id INT);",
                "CREATE TABLE IF NOT EXISTS Colour(colour_id INT PRIMARY KEY, colour VARCHAR);",
                "CREATE TABLE IF NOT EXISTS ClothesType(type_id INT PRIMARY KEY, type VARCHAR);",
                "CREATE TABLE IF NOT EXISTS Coords(shorty_id INT PRIMARY KEY, x INT, y INT);",
                "CREATE TABLE IF NOT EXISTS ShortyUsers(user_login VARCHAR PRIMARY KEY, password VARCHAR);",
                "INSERT INTO Currency VALUES (0, 'сантик') ON CONFLICT DO NOTHING;",
                "INSERT INTO Currency VALUES (1, 'рубль') ON CONFLICT DO NOTHING;",
                "INSERT INTO Currency VALUES (2, 'доллар') ON CONFLICT DO NOTHING;",
                "INSERT INTO SocialStatus VALUES (0, 'тунеядец') ON CONFLICT DO NOTHING;",
                "INSERT INTO SocialStatus VALUES (1, 'полицейский') ON CONFLICT DO NOTHING;",
                "INSERT INTO SocialStatus VALUES (2, 'зэк') ON CONFLICT DO NOTHING;",
                "INSERT INTO SocialStatus VALUES (3, 'художник') ON CONFLICT DO NOTHING;",
                "INSERT INTO SocialStatus VALUES (4, 'доктор') ON CONFLICT DO NOTHING;",
                "INSERT INTO SocialStatus VALUES (5, 'втшник') ON CONFLICT DO NOTHING;",
                "INSERT INTO SocialStatus VALUES (6, 'неопределившийся') ON CONFLICT DO NOTHING;",
                "INSERT INTO Colour VALUES (0, 'голубой') ON CONFLICT DO NOTHING;",
                "INSERT INTO Colour VALUES (1, 'зеленый') ON CONFLICT DO NOTHING;",
                "INSERT INTO Colour VALUES (2, 'желтый') ON CONFLICT DO NOTHING;",
                "INSERT INTO Colour VALUES (3, 'красный') ON CONFLICT DO NOTHING;",
                "INSERT INTO Colour VALUES (4, 'черный') ON CONFLICT DO NOTHING;",
                "INSERT INTO Colour VALUES (5, 'белый') ON CONFLICT DO NOTHING;",
                "INSERT INTO Colour VALUES (6, 'леопардовый') ON CONFLICT DO NOTHING;",
                "INSERT INTO Colour VALUES (7, 'нет цвета') ON CONFLICT DO NOTHING;",
                "INSERT INTO ClothesType VALUES (0, 'шляпа') ON CONFLICT DO NOTHING;",
                "INSERT INTO ClothesType VALUES (1, 'лосины') ON CONFLICT DO NOTHING;",
                "INSERT INTO ClothesType VALUES (2, 'футболка') ON CONFLICT DO NOTHING;",
                "INSERT INTO ClothesType VALUES (3, 'джинсы') ON CONFLICT DO NOTHING;",
                "INSERT INTO ClothesType VALUES (4, 'носки') ON CONFLICT DO NOTHING;",
                "INSERT INTO ClothesType VALUES (5, 'свитшот') ON CONFLICT DO NOTHING;",
                "INSERT INTO ClothesType VALUES (6, 'юбка') ON CONFLICT DO NOTHING;",
                "INSERT INTO ClothesType VALUES (7, 'нет одежды') ON CONFLICT DO NOTHING;",
        };



        String command2 = "SELECT * FROM ShortyUsers;";

        Statement statement1 = null;
        Statement statement2 = null;


        try{
            statement1 = connection.createStatement();

            statement2 = connection.createStatement();

            for (int i = 0; i< request_array.length; i++){
                statement1.executeUpdate(request_array[i]);
            }

            //set[0] = statement1.executeQuery(command1);
            set = statement2.executeQuery(command2);


            System.out.println("Таблицы созданы и заполнены.");

        }catch(SQLException e){
            System.out.println("Возникла ошибка: " + e.getMessage());
        }

        return set;

    }
}
