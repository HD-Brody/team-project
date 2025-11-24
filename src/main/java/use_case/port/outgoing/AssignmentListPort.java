package use_case.port.outgoing;

import entity.Assessment;
import java.util.List;

public interface AssignmentListPort {
    List<Assessment> allAssignments(String userID, String courseID);
}
