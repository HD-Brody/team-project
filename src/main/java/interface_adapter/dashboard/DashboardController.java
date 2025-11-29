package interface_adapter.dashboard;

import use_case.port.incoming.LoadDashboardInputBoundary;

public class DashboardController {
    private final LoadDashboardInputBoundary loadDashboardInteractor;

    public DashboardController(LoadDashboardInputBoundary loadDashboardInteractor) {
        this.loadDashboardInteractor = loadDashboardInteractor;
    }

    public void loadDashboard(String userId) {
        loadDashboardInteractor.execute(userId);
    }

    public void navigateToUploadCourse() {
        // Will be handled by view
    }

    public void navigateToGradeCalculator() {
        // TODO: To be implemented
    }

    public void navigateToCalendarExport() {
        // TODO: To be implemented
    }

    public void navigateToCourseDetails(String courseId) {
        // TODO: To be implemented
    }
}