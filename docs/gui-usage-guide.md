# Task Management GUI Usage Guide

## 🎨 Overview

The Swing GUI implementation for **Use Case 2: View and Edit Tasks** provides a visual interface matching your design mockup.

---

## 🚀 Running the Application

### Option 1: Run from IntelliJ
1. Open `src/main/java/view/swing/TaskManagementApp.java`
2. Right-click on the file
3. Select "Run 'TaskManagementApp.main()'"

### Option 2: Run from Command Line
```bash
cd /Users/rayansalim/Documents/GitHub/team-project
javac -d out -sourcepath src/main/java src/main/java/view/swing/TaskManagementApp.java
java -cp out view.swing.TaskManagementApp
```

---

## 🖼️ Features Implemented

### ✅ **Task List View**
- Displays all tasks for a user
- Shows task title and due date
- Color-coded buttons (gray Edit, red Remove)
- Empty state message when no tasks exist
- Scrollable list for many tasks

### ✅ **Edit Task Dialog**
- Modal popup for editing existing tasks
- Fields: Title, Status, Due Date, Effort, Priority, Notes
- Updates task when "Update Task" button clicked
- Validates input (title required, priority 1-5)
- Closes automatically on successful update

### ✅ **Delete Task**
- Confirmation dialog before deletion
- Removes task from list
- Shows success message

### ⏳ **Add Task Dialog** (Partially Implemented)
- Opens modal dialog with form
- Currently shows "Not Implemented" message
- **Blocked on**: Need to add `createTask` method to `TaskController`
- **Requires**: Leo's database implementation

---

## 📋 Current Limitations

### 1. **Task Creation Not Functional**
**Why?** Creating a task requires:
- Direct access to repository (bypassing controller)
- Or adding a `createTask` method to `TaskController`

**Solution Options:**

#### Option A: Add `createTask` to `TaskController`
```java
// In TaskController.java
public void createTask(String userId, String courseId, String title, 
                       Instant dueAt, Integer effort, Integer priority, 
                       TaskStatus status, String notes) {
    String taskId = UUID.randomUUID().toString();
    Task task = new Task(taskId, userId, courseId, null, title, 
                         dueAt, effort, priority, status, notes);
    taskRepository.save(task);
}
```

#### Option B: Wait for Brody's `SyllabusUploadService`
When users upload syllabi, Brody will automatically create tasks from assessments.
Manual task creation might be a "nice to have" rather than required for MVP.

### 2. **Uses In-Memory Data**
- Currently stores tasks in RAM only
- Data is lost when application closes
- **Fix**: Replace `InMemoryTaskRepository` with Leo's `SqliteTaskRepositoryAdapter`

### 3. **Single Course View**
- Currently hardcoded to "CSC236"
- **Enhancement**: Add course selector dropdown or separate view per course

---

## 🧪 Testing the GUI

### Test Scenario 1: View Tasks
1. Run `TaskManagementApp`
2. You should see 3 sample tasks (Term Test III, Assignment 4, Final Exam Prep)
3. Verify dates are formatted correctly (e.g., "Oct 25, 2025")

### Test Scenario 2: Edit Task
1. Click "Edit" on any task
2. Change the title (e.g., "Term Test III - UPDATED")
3. Change status to "IN_PROGRESS"
4. Click "Update Task"
5. Verify task list refreshes with new title and status

### Test Scenario 3: Delete Task
1. Click "Remove" on any task
2. Confirm deletion in popup dialog
3. Verify task disappears from list
4. Verify success message appears

### Test Scenario 4: Add Task (Expected to Fail)
1. Click "+ Add Task"
2. Fill in form fields
3. Click "Add Task"
4. **Expected**: "Not Implemented" message appears
5. **Reason**: Blocked on repository access

---

## 🔌 Integration with Backend

### Current Architecture
```
TaskManagementApp
    ↓
TaskListView → TaskController → TaskEditingService → InMemoryTaskRepository
```

### Production Architecture (After Leo's Work)
```
TaskManagementApp
    ↓
TaskListView → TaskController → TaskEditingService → SqliteTaskRepositoryAdapter → SQLite DB
```

### How to Wire Up Real Database

In `TaskManagementApp.java`, replace:
```java
// Current (testing)
TaskRepository repository = createTestRepository();
```

With:
```java
// Production (when Leo is done)
Connection connection = DatabaseConnectionFactory.getConnection();
TaskRepository repository = new SqliteTaskRepositoryAdapter(connection);
```

---

## 🎯 Next Steps

### For You (Rayan):
1. ✅ **GUI Implementation** - COMPLETE!
2. ⏳ **Add `createTask` method** to `TaskController` (if needed for MVP)
3. ⏳ **Test with real database** once Leo finishes

### For Leo:
1. ⏳ Implement `SqliteTaskRepositoryAdapter`
2. ⏳ Provide database connection factory
3. ⏳ Create sample data loader

### For Team:
1. ⏳ Integrate with Brody's syllabus upload (creates initial tasks)
2. ⏳ Test end-to-end: Upload syllabus → View tasks → Edit tasks → Calculate grades

---

## 🐛 Known Issues

None currently! All implemented features work as expected.

---

## 💡 Enhancement Ideas (Post-MVP)

1. **Task Filtering**
   - Filter by status (TODO, IN_PROGRESS, DONE)
   - Filter by priority (High, Medium, Low)
   - Filter by due date (This Week, This Month, Overdue)

2. **Task Sorting**
   - Sort by due date (earliest first)
   - Sort by priority (highest first)
   - Sort by status

3. **Visual Improvements**
   - Color-code tasks by priority
   - Highlight overdue tasks in red
   - Show progress bar (X/Y tasks completed)
   - Icons for different task types

4. **Bulk Operations**
   - Select multiple tasks
   - Delete multiple tasks at once
   - Mark multiple as complete

5. **Course Selector**
   - Dropdown to switch between courses
   - "All Courses" view

---

## 📞 Questions?

If you encounter issues or need clarification:
1. Check linter errors in IntelliJ
2. Verify all imports are resolved
3. Ensure Java 11+ is being used
4. Check console output for error messages

**Ready to demo!** 🎉

