package interface_adapter.task_list;

import interface_adapter.ViewModel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class TaskListViewModel extends ViewModel<TaskListState> {
    public static final String TITLE_LABEL = "Task List";
    
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public TaskListViewModel() {
        super("task_list");
        setState(new TaskListState());
    }

    @Override
    public void firePropertyChange() {
        support.firePropertyChange("state", null, this.getState());
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
