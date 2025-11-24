package interface_adapter.syllabus_upload;

import interface_adapter.ViewModel;

public class SyllabusUploadViewModel extends ViewModel<SyllabusUploadState> {
    
    public SyllabusUploadViewModel() {
        super("syllabus upload");
        setState(new SyllabusUploadState());
    }
}