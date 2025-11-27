# Task Management GUI Usage Guide

## 🎨 Overview

The Swing GUI implementation for **Use Case 2: View and Edit Tasks** provides a visual interface matching your design mockup.

---

## 🚀 Running the Application

### Option 1: Run from IntelliJ
1. Open `src/main/java/view/swing/TaskManagementApp.java`
2. Right-click on the file
3. Select "Run 'TaskManagementApp.main()'"

### Option 2: Run from Command Line (Using Maven)
```bash
# Compile the project
mvn clean compile

# Run the application
mvn exec:java -Dexec.mainClass="view.swing.TaskManagementApp"
```

**Note:** Maven automatically handles all dependencies and compilation. No need to use `javac` directly.

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

### ✅ **Add Task Dialog**
- Modal popup for creating new tasks
- Fields: Title, Status, Due Date, Effort, Priority, Notes
- Creates task when "Add Task" button clicked
- Validates input (title required, priority 1-5)
- Closes automatically on successful creation

---

## 📋 Current Limitations

### 1. **Uses In-Memory Data**
- Currently stores tasks in RAM for testing
- Data is lost when application closes
- Will be replaced with SQLite database integration

### 2. **Single Course View**
- Currently displays tasks for "CSC236" only
- Multi-course support planned for future enhancement

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

### Test Scenario 4: Add Task
1. Click "Add Task"
2. Fill in form fields (Title is required)
3. Click "Add Task"
4. Verify task appears in list
5. Verify success message appears

---

## 🔌 Architecture

```
View Layer: TaskListView, TaskFormDialog
    ↓
Controller Layer: TaskController
    ↓
Use Case Layer: TaskEditingService
    ↓
Repository Layer: TaskRepository (interface)
    ↓ (implements)
InMemoryTaskRepository (current) → SQLiteTaskRepositoryAdapter (future)

---

## 📞 Questions?

If you encounter issues or need clarification:
1. Check linter errors in IntelliJ
2. Verify all imports are resolved
3. Ensure Java 15+ is being used (required for multiline string block support)
4. Check console output for error messages

**Ready to demo!** 🎉

