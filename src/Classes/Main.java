package Classes;

import java.io.File;
import java.util.Scanner;

public class Main {
    static int port = -1;
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (args.length != 1){
            System.out.println("Пожалуйста, введите порт в виде целого положительного числа.");
            while(port < 0){
                try{
                    port = Integer.parseInt(sc.nextLine());
                    if (port < 0 ){
                        System.out.println("Port введен в неверном формате. Пожалуйста, введите положительное целое число.");
                    }
                }catch (NumberFormatException e){
                    System.out.println("Port введен в неверном формате. Пожалуйста, введите положительное целое число.");
                }
            }

        }else{

                try{
                    port = Integer.parseInt(args[0]);
                    if (port < 0 ){
                        System.out.println("Port введен в неверном формате. Пожалуйста, введите положительное целое число.");
                    }
                }catch (NumberFormatException e){
                    System.out.println("Port введен в неверном формате. Пожалуйста, введите положительное целое число.");
                }
            while(port < 0){
                try{
                    port = Integer.parseInt(sc.nextLine());
                    if (port < 0 ){
                        System.out.println("Port введен в неверном формате. Пожалуйста, введите положительное целое число.");
                    }
                }catch (NumberFormatException e){
                    System.out.println("Port введен в неверном формате. Пожалуйста, введите положительное целое число.");
                }
            }
        }

        sc.close();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if (System.getenv("FILE_PATH") != null) {
                    File file = new File(System.getenv("FILE_PATH"));

                    synchronized (TCPServer.manager) {
                        TCPServer.manager.saveCollection(file);
                    }
                    if (file.canWrite()) {
                        System.out.println("Колекция сохранена.");
                    } else {
                        System.out.println("Коллекция не сохранена");
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
            }
        });
        System.out.println("Я запустился!!!");
        TCPServer server = new TCPServer();
        server.start();
    }
}
