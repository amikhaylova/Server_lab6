package Classes;

import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class TCPServer {

    final static ShortyManager manager = new ShortyManager();

    class Handler implements Runnable {
        SocketChannel client;

        public Handler(SocketChannel client) {
            this.client = client;
        }

        @Override
        public void run() {
            try (DataOutputStream out = new DataOutputStream(Channels.newOutputStream(client));
                 DataInputStream in = new DataInputStream(Channels.newInputStream(client));
                 ObjectInputStream ois = new ObjectInputStream(in);) {





                while (client.isConnected()) {

                    String command = "";

                    try {
                        command = in.readUTF();
                        command = in.readUTF();
                        command = in.readUTF();
                        command = in.readUTF();
                        command = in.readUTF();
                        command = in.readUTF();
                    } catch (EOFException e) {
                        System.out.println("Клиент вышел в окно, прощай, клиент!");
                        File file;
                        if (System.getenv("FILE_PATH")!=null){
                            file = new File(System.getenv("FILE_PATH"));
                        } else{
                            file = new File("defaultFile.json");
                        }
                        synchronized (manager){
                            manager.saveCollection(file);
                            System.out.println("Текущее состояние коллекции сохранено в файл.");
                        }
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
                                answerForClient = manager.remove_first();
                                break;
                            case ("save"):
                                File file;
                                if (System.getenv("FILE_PATH") != null) {
                                    file = new File(System.getenv("FILE_PATH"));
                                }else {
                                    file = new File("defaultFile.json");
                                }
                                answerForClient = manager.saveCollection(file);
                                break;
                            case ("add"):
                                if (shorty != null){
                                    answerForClient = manager.add(shorty);
                                } else{
                                    answerForClient = ("Действие не выполнено, так как был передан null.");
                                }
                                break;
                            case ("load"):
                                answerForClient = manager.load();
                                break;
                            case ("remove"):
                                if (shorty != null){
                                    answerForClient = manager.remove(shorty);
                                }else {
                                    answerForClient = ("Действие не выполнено, так как был передан null.");
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
                                    answerForClient = manager.add_if_min(shorty);
                                }else{
                                    answerForClient = ("Действие не выполнено, так как был передан null.");
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
                if (System.getenv("FILE_PATH") != null) {
                    File file = new File(System.getenv("FILE_PATH"));
                    synchronized (TCPServer.manager) {
                        TCPServer.manager.saveCollection(file);
                        System.out.println("Коллекция сохранена.");
                    }
                } else {
                    File file = new File("defaultFile.json");
                    synchronized (TCPServer.manager) {
                        TCPServer.manager.saveCollection(file);
                    }
                    if (file.canWrite()) {
                        System.out.println("Колекция сохранена в файл defaultFile.json.");
                    } else {
                        System.out.println("Коллекция не сохранена");
                    }
                }
            }   catch (ClassNotFoundException e) {
                System.out.println("Произошла ошибка: " + e.getMessage() + ".");
                //e.printStackTrace();
            }

            /*} catch (IOException e) {
                System.out.println("Произошла ошибка: " + e.getMessage() + ".");
                //e.printStackTrace();
            }*/
        }
    }

    public void start() {

        ServerSocketChannel server;

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