package interface_adapter.calendar_export;

import data_access.persistence.in_memory.InMemorySessionInfoDataAccessObject;
import entity.Course;
import use_case.dto.CalendarExportRequest;
import use_case.service.CalendarExportService;
import use_case.service.PreviewType;
import use_case.repository.CourseRepository;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarExportController implements view.CalendarExportView.Listener {
    private final CalendarExportService calendarExportService;
    private final CourseRepository courseRepository;
    private final InMemorySessionInfoDataAccessObject sessionDB;
    private final CalendarExportPresenter presenter;
    private view.CalendarExportView view;

    public CalendarExportController(CalendarExportService calendarExportService,
                                    CourseRepository courseRepository,
                                    InMemorySessionInfoDataAccessObject sessionDB,
                                    CalendarExportPresenter presenter) {
        this.calendarExportService = calendarExportService;
        this.courseRepository = courseRepository;
        this.sessionDB = sessionDB;
        this.presenter = presenter;
    }

    public void setView(view.CalendarExportView view) {
        this.view = view;
    }

    @Override
    public void onPreviewRequested(String courseId, PreviewType previewType) {
        try {
            String userId = getUserId();
            List<String> courseIds = getCourseIds(userId, courseId);
            String timezone = ZoneId.systemDefault().getId();
            
            CalendarExportRequest request = new CalendarExportRequest(
                userId,
                timezone,
                courseIds,
                null,
                null,
                null,
                "schedule"
            );
            
            List<String> previewLines = calendarExportService.generatePreviewTexts(request, previewType);
            presenter.presentPreview(previewLines);
            if (view != null) {
                view.setPreviewLines(previewLines);
            }
        } catch (Exception e) {
            presenter.presentError("Failed to generate preview: " + e.getMessage());
        }
    }

    @Override
    public void onExportRequested(String courseId, PreviewType previewType) {
        try {
            String userId = getUserId();
            List<String> courseIds = getCourseIds(userId, courseId);
            String timezone = ZoneId.systemDefault().getId();
            
            CalendarExportRequest request = new CalendarExportRequest(
                userId,
                timezone,
                courseIds,
                null,
                null,
                null,
                "schedule"
            );
            
            calendarExportService.exportCalendar(request);
        } catch (Exception e) {
            presenter.presentError("Failed to export calendar: " + e.getMessage());
        }
    }

    public List<String> loadCourses() {
        String userId = getUserId();
        List<Course> courses = courseRepository.findByUserId(userId);
        return courses.stream()
            .map(course -> course.getCode() + " - " + course.getName())
            .collect(Collectors.toList());
    }

    private String getUserId() {
        entity.Session session = sessionDB.getSession();
        if (session != null && session.getUserID() != null && !session.getUserID().isEmpty()) {
            return session.getUserID();
        }
        return "defaultUser";
    }

    private List<String> getCourseIds(String userId, String selectedCourse) {
        if (selectedCourse == null || selectedCourse.isEmpty()) {
            return List.of();
        }
        
        // Extract course code from "CODE - Name" format
        String courseCode = selectedCourse.split(" - ")[0];
        
        List<Course> courses = courseRepository.findByUserId(userId);
        return courses.stream()
            .filter(c -> c.getCode().equals(courseCode))
            .map(Course::getCourseId)
            .collect(Collectors.toList());
    }
}
