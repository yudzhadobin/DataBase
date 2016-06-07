import java.sql.*;

public class Main {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String name = "Yura";
        String password = "Cv72vbfg";
        Class.forName("org.postgresql.Driver");

        connection = DriverManager.getConnection(url, name, password);
        Statement statement = null;
        statement = connection.createStatement();
        getUniversitiesNames(statement);
        getCumpuses(statement);
        getDomitory(statement);
        getFaculty(statement);
        getEmploee(statement);
        getUniversityWithSpeciality(statement);
        statement.close();
        connection.close();
    }

    private static void getUniversitiesNames(Statement statement) throws SQLException {
        System.out.println("Универы");
        ResultSet resultSet = statement.executeQuery("Select Name from University");
        while (resultSet.next()) {
            System.out.println(resultSet.getString(1));
        }
    }

    private static void getCumpuses(Statement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery("Select Branches.TOWN from University right join Branches on University.ID = Branches.UNIVERSITY_ID\n" +
                "Where University.NAME = 'ВШЭ'");
        while (resultSet.next()) {
            System.out.println(resultSet.getString(1));
        }
    }

    private static void getDomitory(Statement statement) throws SQLException {
        System.out.println("Общага");
        ResultSet resultSet = statement.executeQuery("Select Dormitory.* from Dormitory left join Branches on Dormitory.BRANCH_ID = Branches.ID left join University on University.ID = Branches.UNIVERSITY_ID\n" +
                "Where University.NAME = 'ВШЭ' and Branches.TOWN = 'Москва'");
        while (resultSet.next()) {
            System.out.println("Адресс " + resultSet.getString("adress")
                    + "\t мест: " + resultSet.getInt("place_count"));
        }
    }

    private static void getFaculty(Statement statement) throws SQLException {
        System.out.println("Факультеты");
        ResultSet resultSet = statement.executeQuery("Select Faculty.* from Faculty left join Branches on Faculty.BRANCH_ID = Branches.ID left join University on University.ID = Branches.UNIVERSITY_ID\n" +
                "Where University.NAME = 'ВШЭ' and Branches.TOWN = 'Москва'");
        while (resultSet.next()) {
            System.out.println(resultSet.getString("name"));
        }
    }

    private static void getEmploee(Statement statement) throws SQLException {
        System.out.println("Сотрудники");
        ResultSet resultSet = statement.executeQuery("Select Emploee.Name \n" +
                "  from EMPLOEE left join EMPLOEEFACULTY on EMPLOEE.ID = EMPLOEEFACULTY.EMPLOEE_ID \n" +
                "  left join Faculty on EMPLOEEFACULTY.Faculty_id = Faculty.id\n" +
                "  left join Branches on Faculty.BRANCH_ID = Branches.ID \n" +
                "  left join University on University.ID = Branches.UNIVERSITY_ID\n" +
                "Where University.NAME = 'ВШЭ' and Branches.TOWN = 'Москва' and Faculty.NAME = 'ФКН'");
        while (resultSet.next()) {
            System.out.println(resultSet.getString("name"));
        }
    }

    private static void getUniversityWithSpeciality(Statement statement) throws SQLException {
        System.out.println("Сотрудники");
        ResultSet resultSet = statement.executeQuery("Select University.Name, Branches.Town, Faculty.Name as Faculty  from University right join Branches on Branches.UNIVERSITY_ID = University.ID\n" +
                "  right join  Faculty on Faculty.BRANCH_ID = Branches.ID \n" +
                "  right join Speciality on Speciality.FACULTY_ID = Faculty.Id \n" +
                "  left join SpecialityType on SPECIALITY.SPECIALITY_TYPE_ID = SpecialityType.ID\n" +
                "Where SpecialityType.NAME = 'Программная Инженерия'");
        while (resultSet.next()) {
            System.out.println(resultSet.getString("name") + "\t" + resultSet.getString("town") + " \t" + resultSet.getString("Faculty"));
        }
    }
}
