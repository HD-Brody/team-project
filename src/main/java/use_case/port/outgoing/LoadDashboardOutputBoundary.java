package use_case.port.outgoing;

import use_case.dto.DashboardOutputData;

public interface LoadDashboardOutputBoundary {
    void presentDashboard(DashboardOutputData outputData);
    void presentError(String errorMessage);
}