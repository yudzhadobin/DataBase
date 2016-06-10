import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Evgeny on 10.06.16.
 */
public class QueryManager {

    Statement statement;
    QueryManager(Statement statement){
        this.statement = statement;
    }

    @FunctionalInterface
    interface WhatToTake{
        String take(ResultSet set) throws SQLException;
    }

    ArrayList<String> makeStringList(ResultSet resultSet, WhatToTake taker) throws SQLException {
        ArrayList<String> result = new ArrayList<>();
        while (resultSet.next()) {
            result.add(taker.take(resultSet));
        }
        return result;
    }

    ArrayList<String> getAllUniversitiesNames() throws SQLException {
        ResultSet resultSet = statement.executeQuery("Select Name from University");
        return makeStringList(resultSet, set -> resultSet.getString(1));
    }

    ArrayList<String> getCumpusesOfUniversity(String universityName) throws SQLException {
        ResultSet resultSet = statement.executeQuery("Select Branches.TOWN from University right join Branches on University.ID = Branches.UNIVERSITY_ID\n" +
                "Where University.NAME = '"+universityName+"'");
        return makeStringList(resultSet, set -> resultSet.getString(1));
    }

    ArrayList<String> getDomitoryOfUniversityInTown(String universityName, String townName) throws SQLException {
        ResultSet resultSet = statement.executeQuery("Select Dormitory.* from Dormitory left join Branches on Dormitory.BRANCH_ID = Branches.ID left join University on University.ID = Branches.UNIVERSITY_ID\n" +
                "Where University.NAME = '" +universityName + "' and Branches.TOWN = '" + townName + "'");
        return makeStringList(resultSet, set -> "Адресс " + resultSet.getString("adress")
                + "\t мест: " + resultSet.getInt("place_count"));
    }

    ArrayList<String> getFacultyOfUniversityInTown(String universityName, String townName) throws SQLException {
        ResultSet resultSet = statement.executeQuery("Select Faculty.* from Faculty left join Branches on Faculty.BRANCH_ID = Branches.ID left join University on University.ID = Branches.UNIVERSITY_ID\n" +
                "Where University.NAME = '" +universityName + "' and Branches.TOWN = '" + townName + "'");
        return makeStringList(resultSet, set -> resultSet.getString("name"));
    }

    ArrayList<String> getEmploeeFromFacultyOfBranch(String universityName, String townName, String facultyName) throws SQLException {
        ResultSet resultSet = statement.executeQuery("Select Emploee.Name \n" +
                "  from EMPLOEE left join EMPLOEEFACULTY on EMPLOEE.ID = EMPLOEEFACULTY.EMPLOEE_ID \n" +
                "  left join Faculty on EMPLOEEFACULTY.Faculty_id = Faculty.id\n" +
                "  left join Branches on Faculty.BRANCH_ID = Branches.ID \n" +
                "  left join University on University.ID = Branches.UNIVERSITY_ID\n" +
                "Where University.NAME = '" +universityName + "' and Branches.TOWN = '" + townName + "' and Faculty.NAME = '" + facultyName + "'");
        // ВШЭ Москва ФКН
        return makeStringList(resultSet, set -> resultSet.getString("name"));
    }

    ArrayList<String> getUniversityWithSpeciality(String speciality) throws SQLException {
        ResultSet resultSet = statement.executeQuery("Select University.Name, Branches.Town, Faculty.Name as Faculty  from University right join Branches on Branches.UNIVERSITY_ID = University.ID\n" +
                "  right join  Faculty on Faculty.BRANCH_ID = Branches.ID \n" +
                "  right join Speciality on Speciality.FACULTY_ID = Faculty.Id \n" +
                "  left join SpecialityType on SPECIALITY.SPECIALITY_TYPE_ID = SpecialityType.ID\n" +
                "Where SpecialityType.NAME = '" + speciality + "'");
        return makeStringList(resultSet, set -> resultSet.getString("name") + "\t" + resultSet.getString("town") + " \t" + resultSet.getString("Faculty"));
    }
}
