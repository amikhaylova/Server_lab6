package ShortyClasses;

import Enums.ClothesTypes;
import Enums.Colour;
import Enums.Currency;
import Enums.SocialStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

public class ShortyManager {
    public Vector<Shorty> shortyVector;
    private Date date = new Date();

    public ShortyManager(Connection connection) {
        shortyVector = new Vector<Shorty>();
        load(connection);
    }

    /**
     * To print some information about the collection to stdout
     */
    public String info() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("Тип коллекции: %s\n", Shorty.class.getName()))
                .append(String.format("Дата инициализации: %s\n", date))
                .append(String.format("Количество элементов в коллекции: %d", shortyVector.size()));
        return (stringBuilder.toString());
    }

    public String help() {
        String help = "Вы можете использовать одну из следующих команд для управления коллекцией объектов класса Shorty:\n" +
                "remove {element}: удалить элемент из коллекции по его значению\n" +
                "show: вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                "add_if_min {element}: добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции\n" +
                "remove_first: удалить первый элемент из коллекции\n" +
                "info: вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                "add {element}: добавить новый элемент в коллекцию\n" +
                "quit: завершить работу приложения\n" +
                "Формат задания объектов в командах: json\n" +
                "Пример формата задания объекта" +
                "(при задании объекта необходимо записывать все ключи и значения в ОДНУ строку):\n" +
                "{name: Серж,\n" +
                "look: {\n" +
                "colour: леопардовый,\n" +
                "type: лосины},\n" +
                "budget: {\n" +
                "amount: 150,\n" +
                "currency: сантик},\n" +
                "status: художник,\n" +
                "coords: {\n" +
                "x: 5.0, \n" +
                "y: 6.0}\n" +
                "}";
        return help;
    }

    /**
     * To print text description of all collection's elements to stdout
     */
    public String show() {
        StringBuilder stringBuilder = new StringBuilder();

        Comparator<Shorty> comparator = (o1, o2) -> o1.getName().compareTo(o2.getName());

        //Сортируем коллекцию по имени для передачи клиенту в отсортированном виде
        List<Shorty> sortedShortyList = shortyVector.stream().sorted(comparator).collect(Collectors.toList());

        for (Shorty s : sortedShortyList) {
            stringBuilder.append(String.format("%s\n", s.toString()));
        }
        return (stringBuilder.toString());
    }

    public void sort() {
        Collections.sort(shortyVector);
    }

    /**
     * to reread the collection from the file (FILE_PATH)
     */
    public void load(Connection connection) {

        try {
            int shorty_id;
            String name = "";
            String login = "";
            Double mass = 0.0;
            LocalDateTime creation_time = LocalDateTime.now();
            int amount = 0;
            Currency currency = Currency.Santiky;
            SocialStatus status = SocialStatus.None;
            Colour colour = Colour.None;
            ClothesTypes type = ClothesTypes.None;
            double x = 0.0;
            double y = 0.0;

            String command2 = "SELECT shorty_id FROM Shorty;";

            String command3 = "SELECT " +
                    "Shorty.name,Shorty.user_login, Shorty.mass, Shorty.creation_time, SocialStatus.status " +
                    "FROM Shorty, SocialStatus " +
                    "WHERE Shorty.shorty_id = ? AND Shorty.status_id = SocialStatus.status_id;";

            String command4 = "SELECT " +
                    "Money.amount, Currency.currency " +
                    "FROM Money, Currency " +
                    "WHERE Money.shorty_id = ? AND Money.currency_id = Currency.currency_id;";

            String command5 = "SELECT " +
                    "Colour.colour, ClothesType.type " +
                    "FROM Clothes, Colour, ClothesType " +
                    "WHERE Clothes.shorty_id = ? AND Clothes.colour_id = Colour.colour_id AND Clothes.type_id = ClothesType.type_id;";

            String command6 = "SELECT " +
                    "Coords.x, Coords.y " +
                    "From Coords " +
                    "WHERE shorty_id = ?;";

            Statement st = connection.createStatement();
            PreparedStatement prep1 = connection.prepareStatement(command3);
            PreparedStatement prep2 = connection.prepareStatement(command4);
            PreparedStatement prep3 = connection.prepareStatement(command5);
            PreparedStatement prep4 = connection.prepareStatement(command6);

            ResultSet set = st.executeQuery(command2);

            while (set.next()) {
                int id = set.getInt(1);
                shorty_id = id;
                prep1.setInt(1, id);
                prep2.setInt(1, id);
                prep3.setInt(1, id);
                prep4.setInt(1, id);
                ResultSet set1 = prep1.executeQuery();
                ResultSet set2 = prep2.executeQuery();
                ResultSet set3 = prep3.executeQuery();
                ResultSet set4 = prep4.executeQuery();
                if (set1.next()) {
                    name = set1.getString(1);
                    login = set1.getString(2);
                    mass = set1.getDouble(3);
                    creation_time = set1.getTimestamp(4).toLocalDateTime();
                    status = SocialStatus.get(set1.getString(5));
                }
                if (set2.next()) {
                    amount = set2.getInt(1);
                    currency = Currency.get(set2.getString(2));
                }
                if (set3.next()) {
                    colour = Colour.get(set3.getString(1));
                    type = ClothesTypes.get(set3.getString(2));
                }
                if (set4.next()) {
                    x = set4.getDouble(1);
                    y = set4.getDouble(2);
                }

                Money money = new Money(currency, amount);
                Clothes clothes = new Clothes(colour, type);
                Coords coords = new Coords(x, y);

                Shorty shortyForVector = new Shorty();

                shortyForVector.setMass(mass);
                shortyForVector.setBudget(money);
                shortyForVector.setCoords(coords);
                shortyForVector.setLook(clothes);
                shortyForVector.setName(name);
                shortyForVector.setStatus(status);
                shortyForVector.setTimeOfCreation(creation_time);
                shortyForVector.setUser_login(login);
                shortyForVector.setShorty_id(shorty_id);

                if (!shortyVector.contains(shortyForVector)) {
                    shortyVector.add(shortyForVector);
                }
            }


            System.out.println("Коротышки из БД загружены в коллекцию.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

        /*StringBuilder data = new StringBuilder();
        try {
            Scanner scanner = new Scanner(new File(System.getenv("FILE_PATH")));
            while (scanner.hasNextLine()) {
                data.append(scanner.nextLine());
            }
            JSONArray shorties = new JSONArray(data.toString());

            for (Object shorty : shorties) { //while (iterator.hasNext())
                try {
                    Shorty shortyForVector = jsonToShorty((JSONObject) shorty);
                    if (!shortyVector.contains(shortyForVector)) {
                        shortyVector.add(shortyForVector);
                    }

                    if (shortyVector.stream().noneMatch(x -> x.equals(shortyForVector))){
                        shortyVector.add(shortyForVector);
                    }

                } catch (ClassCastException | IllegalArgumentException e) {
                    System.err.println("Все плохо. " + e.getMessage());
                }
            }


        } catch (NullPointerException|FileNotFoundException e) {
            return ("Файл не найден. Коллекция будет сохранена в файл по умолчанию.");
        }catch (JSONException e) {
            return ("Проблемы с JSON. " + e.getMessage());
        }
        return ("Коллекция обновлена. Если в файле были какие-то новые элементы, которых не было в коллекции, то они добавлены.");*/


        /**
         * transforms JSONObject to instance of ShortyClasses.Shorty class
         *
         * @param shorty - JSONObject representation of shorty
         * @return ShortyClasses.Shorty instance
         * @throws JSONException
         */


        private Shorty jsonToShorty (JSONObject shorty) throws JSONException {
            Shorty shortyForVector = new Shorty();
            if (shorty.has("name")) {
                shortyForVector.setName(shorty.getString("name"));
            }

        /*if (shorty.has("timeOfCreation")) {
            shortyForVector.setTimeOfCreation(shorty.getLong("timeOfCreation"));
        }
*/
            if (shorty.has("status")) {
                shortyForVector.setStatus(SocialStatus.get(shorty.getString("status")));
            }

            try {
                if (shorty.has("budget")) {
                    JSONObject budget = (JSONObject) shorty.get("budget");
                    if (budget.has("amount")) {
                        shortyForVector.getBudget().setAmount(budget.getInt("amount"));
                    }
                    if (budget.has("currency")) {
                        shortyForVector.getBudget().setCurrency(Currency.get(budget.getString("currency")));
                    }
                }
            } catch (ClassCastException e) {
                System.out.println("Произошла ошибка: " + e.getMessage() + ". Было установлено значение по умолчанию.");
            }


            if (shorty.has("look")) {
                JSONObject look = (JSONObject) shorty.get("look");
                if (look.has("colour")) {
                    shortyForVector.getLook().setColour(Colour.get((String) look.get("colour")));
                }
                if (look.has("type")) {
                    shortyForVector.getLook().setType(ClothesTypes.get((String) look.get("type")));
                }
            }

            try {
                if (shorty.has("coords")) {
                    JSONObject coords = (JSONObject) shorty.get("coords");
                    if (coords.has("x") && coords.has("y")) {
                        shortyForVector.setCoords(new Coords((Integer) coords.get("x"), (Integer) coords.get("y")));
                    }
                }
            } catch (ClassCastException e) {
                System.out.println("Произошла ошибка: " + e.getMessage() + ". Было установлено значение по умолчанию.");
            }

            return shortyForVector;
        }

        /**
         * To add new element in the collection
         *
         * @param shorty string description of ShortyClasses.Shorty instance
         */
        public String add (Shorty shorty){
            try {

                if (!shortyVector.contains(shorty)) {
                    shortyVector.add(shorty);
                    return ("Новый коротышка добавлен в коллекцию.");
                } else {
                    return "Новый коротышка не добавлен в коллекцию, так как такой коротышка уже существует.";
                }
            } catch (JSONException e) {
                return ("Возникла ошибка: " + e.getMessage() + ". Для перегрузки коллекции из файла введите объект в корректном формате.");
            }

        }

        public Shorty if_contains (Shorty shorty){
            for (Shorty sh: shortyVector){
                if (sh.equals(shorty)) return sh;
            }
            return null;
        }

        /**
         * To remove the first element in the collection
         */
        public int remove_first (String login){
            if ((shortyVector.size() > 0) && (shortyVector.get(0).getUser_login().equals(login))) {
                int id;
                id = shortyVector.get(0).getShorty_id();
                shortyVector.remove(0);
                return (id);
            } else if (!(shortyVector.size() > 0)){
                return (-10);
            }else {
                return (-7);
            }
        }

        /**
         * To remove the element from the collection using its' json string description
         *
         * @param shorty - string description of ShortyClasses.Shorty instance
         */
        public int remove (Shorty shorty, String login){
            try {
                int id = shorty.getShorty_id();
                if (!shorty.getUser_login().equals(login)) {
                    return -7;
                } else if (shortyVector.remove(shorty)) {
                    return (id);
                } else {
                    return (-10);
                }
            } catch (JSONException e) {
                return (-10);
            }
        }

        /**
         * To add new element to the collection if its' value is less then the value of the least element in the collection
         *
         * @param shorty - JSON text description of ShortyClasses.Shorty instance
         */
        public int add_if_min (Shorty shorty){
            /*try {*/

            if (shortyVector.size() > 0) {
                Shorty minShorty = Collections.min(shortyVector);
                if (shorty.compareTo(minShorty) < 0) {
                    //shortyVector.add(shorty);
                    return (1);
                } else {
                    return (0);
                }
            } else {
                //shortyVector.add(shorty);
                return (1);
            }

        /*} catch (JSONException e) {
            return(0);
        }*/
        }

        public String saveCollection (File file){
            JSONArray shortyArray = new JSONArray();
            if (!shortyVector.isEmpty()) {
                for (Shorty shorty : shortyVector) {
                    JSONObject shortyObject = new JSONObject();

                    JSONObject shortyBudget = new JSONObject();
                    shortyBudget.put("currency", shorty.getBudget().getCurrency().getLocalizedName());
                    shortyBudget.put("amount", shorty.getBudget().getAmount());

                    JSONObject shortyLook = new JSONObject();
                    shortyLook.put("colour", shorty.getLook().getColour().getLocalizedName());
                    shortyLook.put("type", shorty.getLook().getType().getLocalizedName());

                    JSONObject shortyCoords = new JSONObject();
                    shortyCoords.put("x", shorty.getCoords().getX());
                    shortyCoords.put("y", shorty.getCoords().getY());

                    shortyObject.put("name", shorty.getName());
                    shortyObject.put("budget", shortyBudget);
                    shortyObject.put("status", shorty.getStatus().getLocalizedName());
                    shortyObject.put("look", shortyLook);
                    shortyObject.put("coords", shortyCoords);
                    shortyObject.put("mass", shorty.getMass());
                    shortyObject.put("timeOfCreation", shorty.getTimeOfCreation());


                    shortyArray.put(shortyObject);


                    try (FileWriter fileWriter = new FileWriter(file)) {//System.getenv("FILE_PATH"))) {
                        fileWriter.write(shortyArray.toString(4));
                        fileWriter.flush();

                    } catch (IOException e) {
                        return ("Возникла ошибка: " + e.getMessage() + ".");
                    }

                }
            } else {
                try (FileWriter fileWriter = new FileWriter(file)) {
                    fileWriter.write("");
                    fileWriter.flush();
                    return ("Текущее состояние коллекции сохранено в файл.");
                } catch (IOException e) {
                    return ("Возникла ошибка: " + e.getMessage() + ".");
                }
            }
            return ("Текущее состояние коллекции сохранено в файл.");
        }
    }

