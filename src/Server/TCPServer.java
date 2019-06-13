package Server;

import DB.DataBaseManager;
import DB.MailSender;
import DB.PasswordGenerator;
import DB.UsersManager;
import Main.Main;
import ShortyClasses.*;

import javax.mail.MessagingException;
import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TCPServer {

    Connection connection;
    ShortyManager manager;
    UsersManager usersManager;
    public static DataBaseManager dataBaseManager;
    ResultSet set = null;


    public TCPServer(ResultSet set, Connection connection){
        this.set = set;
        this.connection = connection;
    }

    class Handler implements Runnable {
        SocketChannel client;
        String user_login;
        String email;

        public Handler(SocketChannel client) {
            this.client = client;
        }

        String read(DataInputStream in) throws IOException{
            String string;
            string = in.readUTF();
            string = in.readUTF();
            string = in.readUTF();
            string = in.readUTF();
            string = in.readUTF();
            string = in.readUTF();
            return string;
        }

        String authorize(DataInputStream in, DataOutputStream out) throws IOException{
            String login = "";
            String password = "";
            String action = "";
            boolean correct = false;

            Map<String, String> map = new HashMap<>();
            map.put("login1","pas1");
            map.put("login2","pas2");

            PasswordGenerator passwordGenerator = new PasswordGenerator.PasswordGeneratorBuilder()
                    .useDigits(true)
                    .useLower(true)
                    .useUpper(true)
                    .build();

            MailSender sender = new MailSender();

            while (!correct){
                    action = read(in);
                    if (action.equals("registration")){
                        login = read(in);
                        email = read(in);
                        if (!usersManager.ifContainLogin(login)){
                            password = passwordGenerator.generate(7);
                            try{
                                sender.send("Пароль для входа в очень полезное приложение", "Ваш пароль: "+ password, email);
                                usersManager.addNewUser(login,password);
                                System.out.println("Новое значение добавлено в мап.");
                                out.writeUTF("Регистрация прошла успешно");
                            }catch (SQLException e) {
                                System.out.println("Произошла ошибка при добалении пользователя: " + e.getMessage());
                            }catch(MessagingException e){
                                System.out.println("Произошла ошибка при отправке сообщения: " + e.getMessage());
                                try {
                                    usersManager.addNewUser(login,password);
                                } catch (SQLException ex) {
                                    System.out.println("Произошла ошибка при добалении пользователя: " + e.getMessage());
                                }
                                System.out.println("Новое значение добавлено в мап.");
                                out.writeUTF("Что-то пошло не так с регистрацией и мы не смогли отправить вам письмо. Но вот ваш пароль: " + password);

                            }

                        }else{
                            out.writeUTF("Данный логин занят");
                        }
                    }else if (action.equals("authorization")){
                        login = read(in);
                        if(usersManager.ifContainLogin(login)){
                            out.writeUTF("1");
                            password = read(in);
                            if(usersManager.checkPassword(login, password)){
                                out.writeUTF("Авторизация прошла успешно");
                                correct = true;
                            }else{
                                out.writeUTF("Введенный пароль неверен");
                            }
                        }else{
                            out.writeUTF("Данного логина не существует.");
                        }

                    }

            }

            return (login);
        }



        @Override
        public void run() {
            try (DataOutputStream out = new DataOutputStream(Channels.newOutputStream(client));
                 DataInputStream in = new DataInputStream(Channels.newInputStream(client));
                 ObjectInputStream ois = new ObjectInputStream(in);) {

                user_login = authorize(in, out);

                while (client.isConnected()) {

                    String command = "";

                    try {
                        command = read(in);
                        
                    } catch (EOFException e) {
                        System.out.println("Клиент вышел в окно, прощай, клиент!");
                       /* File file;
                        if (System.getenv("FILE_PATH")!=null){
                            file = new File(System.getenv("FILE_PATH"));
                        } else{
                            file = new File("defaultFile.json");
                        }
                        synchronized (manager){
                            manager.saveCollection(file);
                            System.out.println("Текущее состояние коллекции сохранено в файл.");
                        }*/
                        break;
                    }



                    System.out.println("Клиент просит выполнить команду " + command.trim());

                    Shorty shorty = null;

                    if (command.trim().equals("add") || command.trim().equals("add_if_min") || command.trim().equals("remove")) {

                        shorty = (Shorty) ois.readObject();
                        //System.out.println(shorty);
                    }

                    String answerForClient = "Отсутствие ответа - тоже ответ.";

                    synchronized (manager) {

                        switch (command.trim()) {
                            case ("remove_first"):
                                int id = manager.remove_first(user_login);
                                if (id>0) {
                                    dataBaseManager.remove(id);
                                    answerForClient = "Коротышка удален из базы данных";
                                }else if (id == -10){
                                    answerForClient = "Коротышка не удален из базы данных, потому что там пусто.";
                                }else{
                                    answerForClient = "У вас недостаточно прав для удаления коротышки.";
                                }
                                break;
                            case ("add"):
                                if (shorty != null){
                                        if (manager.if_contains(shorty) == null){
                                            shorty.setUser_login(user_login);
                                            shorty.setShorty_id(dataBaseManager.add(shorty));
                                            manager.add(shorty);
                                            answerForClient = "Коротышка добавлен в базу данных.";
                                        }else{
                                            answerForClient = "Коротышка не добавлен в базу данных, так как данный коротышка уже существует.";
                                        }

                                } else{
                                    answerForClient = ("Действие не выполнено, так как был передан неверный формат команды.");
                                }
                                break;
                            /*case ("load"):
                                answerForClient = manager.load();
                                break;*/
                            case ("remove"):
                                if (shorty != null) {
                                    shorty = manager.if_contains(shorty);
                                    if( shorty != null){
                                        int id1 = manager.remove(shorty, user_login);
                                        if (id1 == -7) {
                                            answerForClient = "Недостаточно прав для удаления данного коротышки.";
                                        } else if (id1 == -10) {
                                            answerForClient = "Коротышка не был удален, так как его нет в базе данных.";
                                        } else {
                                            dataBaseManager.remove(id1);
                                            answerForClient = "Коротышка удален из базы данных.";
                                        }
                                    }else{
                                        answerForClient = "Команда не выполнена, так как такого коротышки нет в коллекции.";
                                    }
                                }else{
                                    answerForClient = "Команда не выполнена, так как был задан неверный формат.";
                                }

                                break;
                            case ("info"):
                                answerForClient = manager.info();
                                break;
                            case ("show"):
                                answerForClient = manager.show();
                                break;
                            case ("add_if_min"):
                                if (shorty != null){
                                    if(manager.if_contains(shorty) == null){
                                        if (manager.add_if_min(shorty)>0){
                                            shorty.setUser_login(user_login);
                                            shorty.setShorty_id(dataBaseManager.add(shorty));
                                            manager.add(shorty);
                                            answerForClient = "Коротышка добавлен в базу данных.";
                                        }else{
                                            answerForClient = "Коротышка не добавлен в базу данных, так как он не наименьший.";
                                        }
                                    }else{
                                        answerForClient = "Коротышка не добавлен в базу данных, так как он уже существует в базе данных.";
                                    }
                                    } else{
                                    answerForClient = ("Действие не выполнено, так как был передан неверный формат команды.");
                                }
                                break;
                            case ("help"):
                                answerForClient = manager.help();
                                break;
                            case ("quit"):
                                answerForClient = "гыгы, ты все равно это не получишь";
                                break;
                        }
                    }

                    try{
                        out.writeUTF(answerForClient);
                    }catch(IOException e){
                        System.out.println("Произошла ошибка: " + e.getMessage() + ".");
                    }
                }

            } catch (EOFException e) {
                System.out.println("Произошла ошибка: " + e.getMessage() + ".");
                //e.printStackTrace();

            } catch (IOException e) {
                System.out.println("Клиент отключился от сервера: " + e.getMessage() + ".");
               /* if (System.getenv("FILE_PATH") != null) {
                    File file = new File(System.getenv("FILE_PATH"));
                    synchronized (manager) {
                        manager.saveCollection(file);
                        System.out.println("Коллекция сохранена.");
                    }
                } else {
                    File file = new File("defaultFile.json");
                    synchronized (manager) {
                        manager.saveCollection(file);
                    }
                    if (file.canWrite()) {
                        System.out.println("Колекция сохранена в файл defaultFile.json.");
                    } else {
                        System.out.println("Коллекция не сохранена");
                    }
                }*/
            }   catch (ClassNotFoundException e) {
                System.out.println("Произошла ошибка: " + e.getMessage() + ".");
                //e.printStackTrace();
            } catch (SQLException e){
                e.printStackTrace();
                System.out.println("Произошла ошибка: " + e.getMessage() + "." + e.getSQLState());
            }
        }
    }

    public void start() {

        ServerSocketChannel server;

        this.manager = new ShortyManager(connection);
        this.usersManager = new UsersManager(set, connection);
        this.dataBaseManager = new DataBaseManager(connection);

        try {
            server = ServerSocketChannel.open();
            server.bind(new InetSocketAddress(Main.port));
            while (true) {
                SocketChannel client = server.accept();
                System.out.println("Соединение с клиентом установлено.");
                Thread thread = new Thread(new Handler(client));
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка: " + e.getMessage() + ".");
            //e.printStackTrace();
        }
    }
}