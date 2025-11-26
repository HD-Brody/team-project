package use_case.service;

import entity.Assessment;
import entity.Course;
import use_case.dto.DashboardOutputData;
import use_case.port.incoming.LoadDashboardInputBoundary;
import use_case.port.outgoing.LoadDashboardOutputBoundary;
import use_case.repository.AssessmentRepository;
import use_case.repository.CourseRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LoadDashboardInteractor implements LoadDashboardInputBoundary {
    private final CourseRepository courseRepository;
    private final AssessmentRepository assessmentRepository;
    private final LoadDashboardOutputBoundary outputBoundary;

    public LoadDashboardInteractor(CourseRepository courseRepository,
                                  AssessmentRepository assessmentRepository,
                                  LoadDashboardOutputBoundary outputBoundary) {
        this.courseRepository = Objects.requireNonNull(courseRepository);
        this.assessmentRepository = Objects.requireNonNull(assessmentRepository);
        this.outputBoundary = Objects.requireNonNull(outputBoundary);
    }

    @Override
    public void execute(String userId) {
        try {
            // Fetch all courses for the user
            List<Course> courses = courseRepository.findByUserId(userId);
            
            // For each course, get upcoming assessments
            List<DashboardOutputData.CourseData> courseDataList = courses.stream()
                .map(course -> {
                    List<Assessment> assessments = assessmentRepository.findByCourseId(course.getCourseId());
                    
                    // Filter and format upcoming assessments
                    List<DashboardOutputData.AssessmentData> upcomingAssessments = assessments.stream()
                        .filter(this::isUpcoming)
                        .map(assessment -> new DashboardOutputData.AssessmentData(
                            assessment.getTitle(),
                            formatDate(assessment.getEndsAt())
                        ))
                        .collect(Collectors.toList());
                    
                    return new DashboardOutputData.CourseData(
                        course.getCourseId(),
                        course.getCode(),
                        course.getName(),
                        upcomingAssessments
                    );
                })
                .collect(Collectors.toList());
            
            outputBoundary.presentDashboard(new DashboardOutputData(courseDataList));
            
        } catch (Exception e) {
            outputBoundary.presentError("Failed to load dashboard: " + e.getMessage());
        }
    }

    private boolean isUpcoming(Assessment assessment) {
        if (assessment.getEndsAt() == null || assessment.getEndsAt().isEmpty()) {
            return false;
        }
        // Simple check: if has a due date, consider it upcoming
        // TODO: Parse date and check if it's in the future
        return true;
    }

    private String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) {
            return "TBD";
        }
        try {
            // Parse ISO date and format for display
            LocalDate date = LocalDate.parse(isoDate.substring(0, 10));
            return date.format(DateTimeFormatter.ofPattern("MMM d, yy"));
        } catch (Exception e) {
            return isoDate; // Return as-is if parsing fails
        }
    }
}