/**
 * Swing GUI implementation for Task Management (Use Case 2).
 * 
 * <h3>Components:</h3>
 * <ul>
 *   <li>{@link view.swing.TaskManagementApp} - Main application launcher</li>
 *   <li>{@link view.swing.TaskListView} - Main window showing task list</li>
 *   <li>{@link view.swing.TaskItemPanel} - Individual task display component</li>
 *   <li>{@link view.swing.TaskFormDialog} - Add/Edit task modal dialog</li>
 * </ul>
 * 
 * <h3>Running the Application:</h3>
 * <pre>
 * java view.swing.TaskManagementApp
 * </pre>
 * 
 * <h3>Architecture:</h3>
 * This package is the outermost layer (Frameworks & Drivers) in Clean Architecture.
 * It depends on the Interface Adapter layer (TaskController) but has no knowledge
 * of use cases or entities directly.
 * 
 * <h3>Current Status:</h3>
 * - ✅ View, Edit, Delete functionality implemented
 * - ⏳ Add/Create functionality pending (requires repository access)
 * - ⏳ Uses in-memory repository for testing until Leo provides database layer
 */
package view.swing;

