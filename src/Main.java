import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;

public class Main {

    static QueryManager queryManager;
    static Connection connection;
    static Statement statement;
    static ArrayList<String> actionsList;
    public static void main(String[] args){
        if (!initiateConnection())
            return;
        actionsList = new ArrayList<>();
        actionsList.add("Вывести список всех университетов");
        actionsList.add("Вывести список всех филиалов университета");
        actionsList.add("Вывести список всех общежитий филиала университета");
        actionsList.add("Вывести списко всех факультетов филиала университета");
        actionsList.add("Вывести список всех кто преподаёт на факультете филиала");
        actionsList.add("Вывести список всех факультетов, где преподаётся специальность");
        mainIterationWithUser();
        closeConnection();
    }

    private static void mainIterationWithUser() {
        while (true) {
            int answer = askQuestionFromList("Выберите действие: ", actionsList);
            if (answer == -1) {
                if (askBinaryQuestion("Вы желаете продолжить?")) {
                    continue;
                }
                else{
                    return;
                }
            }
            else {
                try {
                    switch (answer) {
                        case 0:
                            say(queryManager.getAllUniversitiesNames());
                            break;
                        case 1:
                            say(queryManager.getCumpusesOfUniversity(
                                    askString("Введите название университета")));
                            break;
                        case 2:
                            say(queryManager.getDomitoryOfUniversityInTown(
                                    askString("Введите название университета"),
                                    askString("Введите название города (филиала)")));
                            break;
                        case 3:
                            say(queryManager.getFacultyOfUniversityInTown(
                                    askString("Введите название университета"),
                                    askString("Введите название города (филиала)")));
                            break;
                        case 4:
                            say(queryManager.getEmploeeFromFacultyOfBranch(
                                    askString("Введите название университета"),
                                    askString("Введите название города (филиала)"),
                                    askString("Введине название факультета")));
                            break;
                        case 5:
                            say(queryManager.getUniversityWithSpeciality(
                                    askString("Введите название специальности")));
                            break;
                        default:
                            say("Данные запрос ещё не реализован. Обратитесь позднее");
                            break;
                    }
                }
                catch (SQLException ex){
                    say("Произошла какая то ошибка в базе данных");
                }
            }
            if (!askBinaryQuestion("Вы желаете продолжить?")) {
                return;
            }
        }
    }

    private static boolean initiateConnection(){
        while (true) {
            try {
                String url = "jdbc:oracle:thin:@localhost:1521:xe";
                String name = "Yura";
                String password = "Cv72vbfg";
                Class.forName("org.postgresql.Driver");

                connection = DriverManager.getConnection(url, name, password);
                statement = connection.createStatement();
                queryManager = new QueryManager(statement);
                return true;
            } catch (ClassNotFoundException | SQLException e) {
                say("Проблема с подключением к базе данных.");
                if (askBinaryQuestion("Попробовать снова?")){
                    continue;
                }
                else{
                    return false;
                }
            }
        }
    }

    private static String askString(String question) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        say(question);
        try {
            return br.readLine();
        } catch (IOException e) {
            return "";
        }
    }

    private static int askQuestionFromList(String question, ArrayList<String> options){
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int numberRepeat = 3;
        int nubmerOptions = options.size();
        while (numberRepeat >0) {
            say(question);
            say(options, true);
            try {
                try {
                    int answer = Integer.parseInt(br.readLine());
                    if (answer >= 0 && answer < nubmerOptions){
                        return answer;
                    }
                    else{
                        say("Необходимо ввести число из диапазона [0," + nubmerOptions +")");
                    }
                }catch(NumberFormatException nfe) {
                    say("Введено некорректное число");
                }
            } catch (IOException e) {
                say("Произошла ошибка про вводе. Пожалуйста, повторите.");
            }
            numberRepeat--;
        }
        return -1;
    }

    private static boolean askBinaryQuestion(String question) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int numberRepeat = 3;
        while (numberRepeat >0) {
            say(question + " (д/н)");
            try {
                String answer = br.readLine();
                if (answer.toLowerCase().matches("д"))
                    return true;
                else if (answer.toLowerCase().matches("н"))
                    return false;
                say("необходимо ввести д или н");
            } catch (IOException e) {
                return false;
            }
            numberRepeat--;
        }
        say("Превышено число попыток. Результат принимается за отрицательный.");
        return false;
    }

    private static void say(String speech){
        System.out.println(speech);
    }
    private static void say(ArrayList<String> list, boolean withNumbers){
        int i = 0;
        for (String str:list) {
            if (withNumbers) {
                say(Integer.toString(i) + ". " + str);
                i++;
            }
            else {
                say(str);
            }
        }
    }
    private static void say(ArrayList<String> list){
        list.forEach(Main::say);
    }


    private static void closeConnection(){
        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
