package data_access.persistence.sqlite;

import entity.AssessmentType;
import use_case.repository.CourseRepository;
import view.cli.Main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Course implements CourseRepository {

    private final Connection connection = Main.getConnection();;

    /**
     * Core functionalities
     * findByCourseID(String userID): Retrieves all the Course a user has.
     * @param courseID: the course
     * @return a list of Assessment.
     */
    @Override
    public List<entity.Course> findByUserID(String userID) {
        List<entity.Course> courseList = new ArrayList<>();

        try {
            Statement stmt = connection.createStatement();
            String getCourse = "select * from courses WHERE user_id = '" + userID +
                    "'";
            ResultSet result = stmt.executeQuery(getCourse);
            while (result.next()) {
                String courseId = result.getString("course_id"); // Use a different var name
                String userId = result.getString("user_id");
                String code = result.getString("code");
                String name = result.getString("name");
                String term = result.getString("term");
                String instructor = result.getString("instructor");

                entity.Course course = new entity.Course(
                        courseId,
                        userID,
                        code,
                        name,
                        term,
                        instructor
                );
                courseList.add(course);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return courseList;
    }



    public void save(entity.Course course) {
        try {
            Statement stmt = connection.createStatement();
            String saveCourse = "INSERT INTO courses VALUES ('" +
                    course.getCourseId() + "', '" +
                    course.getUserId() + "', '" +
                    course.getCode() + "', " +
                    course.getName() + ", '" +
                    course.getTerm() + "', '" +
                    course.getInstructor() + "')";
            int x = stmt.executeUpdate(saveCourse);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}