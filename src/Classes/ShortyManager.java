package Classes;

import Enums.ClothesTypes;
import Enums.Colour;
import Enums.Currency;
import Enums.SocialStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ShortyManager {
    private Vector<Shorty> shortyVector;
    private Date date = new Date();

    public ShortyManager() {
        shortyVector = new Vector<Shorty>();
        load();
    }

    /**
     * To print some information about the collection to stdout
     */
    public String info() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("Тип коллекции: %s\n", Shorty.class.getName()))
                .append(String.format("Дата инициализации: %s\n", date))
                .append(String.format("Количество элементов в коллекции: %d", shortyVector.size()));
        return(stringBuilder.toString());
    }

    public String help(){
        String help = "Вы можете использовать одну из следующих команд для управления коллекцией объектов класса Shorty:\n" +
                "remove {element}: удалить элемент из коллекции по его значению\n" +
                "show: вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                "add_if_min {element}: добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции\n" +
                "remove_first: удалить первый элемент из коллекции\n" +
                "load: перечитать коллекцию из файла (если в файле есть те эелементы, окторых нет в коллекции, то они будут добавлены)\n" +
                "info: вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                "add {element}: добавить новый элемент в коллекцию\n" +
                "quit или q: завершить работу приложения\n" +
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
                "status: художник\n" +
                "coords: {\n" +
                "x: 5.0, \n" +
                "y: 6.0},\n"+
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
    public String load() {

        StringBuilder data = new StringBuilder();
        try {
            Scanner scanner = new Scanner(new File(System.getenv("FILE_PATH")));
            while (scanner.hasNextLine()) {
                data.append(scanner.nextLine());
            }
            JSONArray shorties = new JSONArray(data.toString());

            for (Object shorty : shorties) { //while (iterator.hasNext())
                try {
                    Shorty shortyForVector = jsonToShorty((JSONObject) shorty);
                    /*if (!shortyVector.contains(shortyForVector)) {
                        shortyVector.add(shortyForVector);
                    }*/

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
        return ("Коллекция обновлена. Если в файле были какие-то новые элементы, которых не было в коллекции, то они добавлены.");
    }

    /**
     * transforms JSONObject to instance of Classes.Shorty class
     *
     * @param shorty - JSONObject representation of shorty
     * @return Classes.Shorty instance
     * @throws JSONException
     */
    private Shorty jsonToShorty(JSONObject shorty) throws JSONException {
        Shorty shortyForVector = new Shorty();

         if (shorty.has("name")) {
            shortyForVector.setName(shorty.getString("name"));
        }

        if (shorty.has("timeOfCreation")) {
            shortyForVector.setTimeOfCreation(shorty.getLong("timeOfCreation"));
        }

        if (shorty.has("status")){
            shortyForVector.setStatus(SocialStatus.get(shorty.getString("status")));
        }

        if (shorty.has("budget")) {
            JSONObject budget = (JSONObject) shorty.get("budget");
            if (budget.has("amount")) {
                shortyForVector.getBudget().setAmount(budget.getInt("amount"));
            }
            if (budget.has("currency")) {
                shortyForVector.getBudget().setCurrency(Currency.get(budget.getString("currency")));
            }
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

        if (shorty.has("coords")) {
            JSONObject coords = (JSONObject) shorty.get("coords");
            if (coords.has("x") && coords.has("y")) {
                shortyForVector.setCoords(new Coords((Integer) coords.get("x"), (Integer) coords.get("y")));
            }
        }

        return shortyForVector;
    }

    /**
     * To add new element in the collection
     *
     * @param shorty string description of Classes.Shorty instance
     */
    public String add(Shorty shorty) {
        try {

            if (!shortyVector.contains(shorty)) {
                shortyVector.add(shorty);
                return ("Новый коротышка добавлен в коллекцию.");
            } else {
                return "Новый коротышка не добавлен в коллекцию.";
            }
        } catch (JSONException e) {
            return ("Возникла ошибка: " + e.getMessage() +  ". Для перегрузки коллекции из файла введите объект в корректном формате.");
        }

    }

    /**
     * To remove the first element in the collection
     */
    public String remove_first() {
        if (shortyVector.size() > 0) {
            shortyVector.remove(0);
            return("Первый элемент коллекции удален.");
        } else {
            return("Невозможно удалить первый элемент, так как коллекция пуста.");
        }
    }

    /**
     * To remove the element from the collection using its' json string description
     *
     * @param shorty - string description of Classes.Shorty instance
     */
    public String remove(Shorty shorty) {
        try {
            if (shortyVector.remove(shorty)) {
                return("Коротышка удален из коллекции.");
            } else {
                return("Коротышка не может быть удален из коллекции, так как его там нет.");
            }
        } catch (JSONException e) {
            return("Возникла ошибка: " + e.getMessage() + ". Для удаления объекта из коллекции введите его в корректном формате.");
        }
    }

    /**
     * To add new element to the collection if its' value is less then the value of the least element in the collection
     *
     * @param shorty - JSON text description of Classes.Shorty instance
     */
    public String add_if_min(Shorty shorty) {
        try {

            if (shortyVector.size() > 0) {
                Shorty minShorty = Collections.min(shortyVector);
                if (shorty.compareTo(minShorty) < 0) {
                    shortyVector.add(shorty);
                    return("Новый коротышка добавлен в коллекцию");
                } else {
                    return("Новый коротышка не добавлен в коллекцию, " +
                            "так как его значение больше, чем у наименьшего элемента этой коллекции.");
                }
            } else {
                shortyVector.add(shorty);
                return("Новый коротышка добавлен в коллекцию");
            }

        } catch (JSONException e) {
            return("Возникла ошибка: " + e.getMessage() + ". Для добавления объекта в коллекцию введите его в корректном формате.");
        }
    }

    public String saveCollection(File file) {
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
                    return("Возникла ошибка: " + e.getMessage() + ".");
                }

            }
        } else {
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write("");
                fileWriter.flush();
                return ("Текущее состояние коллекции сохранено в файл.");
            } catch (IOException e) {
                return("Возникла ошибка: " + e.getMessage() + ".");
            }
        }
        return ("Текущее состояние коллекции сохранено в файл.");
    }
}
