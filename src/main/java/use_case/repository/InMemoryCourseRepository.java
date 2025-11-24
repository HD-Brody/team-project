package use_case.repository;

import entity.Course;
import use_case.repository.CourseRepository;

import java.util.*;

public class InMemoryCourseRepository implements CourseRepository {
    private final Map<String, Course> courses = new HashMap<>();

    @Override
    public List<Course> findByUserID(String userID) {
        List<Course> result = new ArrayList<>();
        for (Course course : courses.values()) {
            if (course.getUserId().equals(userID)) {
                result.add(course);
            }
        }
        return result;
    }

    @Override
    public void save(Course course) {
        courses.put(course.getCourseId(), course);
        System.out.println("Saved course: " + course.getName() + 
                           " (" + course.getCode() + ")" +
                           " - Term: " + course.getTerm() +
                           ", Instructor: " + course.getInstructor());
    }

}