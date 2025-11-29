package data_access.persistence.sqlite;

import entity.AssessmentType;
import use_case.repository.CourseRepository;
import app.Main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Course implements CourseRepository {

    private final Connection connection = Main.getConnection();;

    /**
     * Core functionalities
     * findByUserId(String userId): Retrieves all the Course a user has.
     * @param userId: the user
     * @return a list of Assessment.
     */
    @Override
    public List<entity.Course> findByUserId(String userId) {
        List<entity.Course> courseList = new ArrayList<>();

        try {
            Statement stmt = connection.createStatement();
            String getCourse = "select * from courses WHERE user_id = '" + userId +
                    "'";
            ResultSet result = stmt.executeQuery(getCourse);
            while (result.next()) {
                String courseId = result.getString("course_id");
                String userIdFromDb = result.getString("user_id");
                String code = result.getString("code");
                String name = result.getString("name");
                String term = result.getString("term");
                String instructor = result.getString("instructor");

                entity.Course course = new entity.Course(
                        courseId,
                        userIdFromDb,
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


    @Override
    public void save(entity.Course course) {
        try {
            Statement stmt = connection.createStatement();
            String saveCourse = "INSERT INTO courses VALUES ('" +
                    course.getCourseId() + "', '" +
                    course.getUserId() + "', '" +
                    course.getCode() + "', '" +
                    course.getName() + "', '" +
                    course.getTerm() + "', '" +
                    course.getInstructor() + "')";
            int x = stmt.executeUpdate(saveCourse);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}