package interface_adapter.dashboard;

import interface_adapter.ViewManagerModel;
import interface_adapter.syllabus_upload.SyllabusUploadState;
import interface_adapter.syllabus_upload.SyllabusUploadViewModel;
import use_case.dto.DashboardOutputData;
import use_case.dto.SyllabusUploadOutputData;
import use_case.port.outgoing.LoadDashboardOutputBoundary;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardPresenter implements LoadDashboardOutputBoundary {
    private final DashboardViewModel dashboardViewModel;
    private final ViewManagerModel viewManagerModel;
    private final SyllabusUploadViewModel syllabusUploadViewModel;

    public DashboardPresenter(ViewManagerModel viewManagerModel, 
                             DashboardViewModel dashboardViewModel,
                             SyllabusUploadViewModel syllabusUploadViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.dashboardViewModel = dashboardViewModel;
        this.syllabusUploadViewModel = syllabusUploadViewModel;
    }

    @Override
    public void presentDashboard(DashboardOutputData outputData) {
        DashboardState state = dashboardViewModel.getState();
        
        // Convert output data to display data
        List<DashboardState.CourseDisplayData> courses = outputData.getCourses().stream()
            .map(courseData -> new DashboardState.CourseDisplayData(
                courseData.getCourseId(),
                courseData.getCourseCode(),
                courseData.getCourseName(),
                courseData.getUpcomingAssessments().stream()
                    .map(assessment -> new DashboardState.AssessmentDisplayData(
                        assessment.getTitle(),
                        assessment.getDueDate(),
                        assessment.getType(),
                        assessment.getWeight()
                    ))
                    .collect(Collectors.toList())
            ))
            .collect(Collectors.toList());
        
        state.setCourses(courses);
        state.setError(null);
        dashboardViewModel.setState(state);
        dashboardViewModel.firePropertyChange();
    }

    public void presentSyllabusUploadSuccess(SyllabusUploadOutputData outputData) {
        SyllabusUploadState state = syllabusUploadViewModel.getState();
        syllabusUploadViewModel.setState(state);
        syllabusUploadViewModel.firePropertyChange();
        
        // Navigate to dashboard
        viewManagerModel.setState("dashboard");
        viewManagerModel.firePropertyChange();
    }

    @Override
    public void presentError(String errorMessage) {
        DashboardState state = dashboardViewModel.getState();
        state.setError(errorMessage);
        state.setCourses(new ArrayList<>());
        dashboardViewModel.setState(state);
        dashboardViewModel.firePropertyChange();
    }
}