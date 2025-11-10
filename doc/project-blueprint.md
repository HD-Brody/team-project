# Project Blueprint

# Team Name

M.A.R.B.L.E.

# 1\. Project Domain

Syllabus Assistant

An assistant that helps you organize your school schedule based on your course syllabus.

# 2\. User Stories

* As a user, I want to upload or paste my course syllabus so that I can save time organizing course work  
* As a user, I want to view and edit a list of tasks and due dates so that I can see all my course tasks.  
* As a user, I want to use a grade calculator based on course weightings so that I can predict a grade to aim for on each assessment (task)  
* As a user, I want suggestions on what tasks I should prioritize so that I can work on them first.  
* As a user, I want to save and load my course data between sessions.  
* As a user, I want to export my tasks as a calendar file so that I can view the data on another calendar app.  
* As a user, I want to login or create my account. 

# 3\. Use Cases

# MVP Use Cases

* **Use Case 1:** Upload Syllabus  
  * **Lead:**  Brody  
  * **Main Flow:**  
    * User uploads a pdf of their syllabus  
    * System parses and stores the file.  
    * The tasks (assessments and tests) will be listed to the user along with their deadline and weight.     
  * **Alternative Flows (if applicable):**  
    * If the file is not in the right format, the system shows an error message. 

* **Use Case 2:** View and Edit Tasks and Due Dates  
  * **Lead:** Rayan  
  * **Main Flow:**  
    * User selects the task to be edited  
    * User enters the new task and new due date   
    * User receives a success message from the system and sees the modified tasks and due dates  
  * **Alternative Flows (if applicable):**  
    * If the new due date or new task is empty, keep them the same

* **Use Case 3:** Weighed grade calculator  
  * **Lead:** Mike  
  * **Main Flow:**  
    * User sets a desired grade  
    * User enters the anticipated grades on some of the tasks  
    * User clicks the “calculate” button, triggering the system to calculate the target grade on each assignment based on the weight in the syllabus as well as the user’s preference on assignment types (i.e. user anticipates scoring higher on assignments compared to tests)  
    * User sees the target grades as well as previous grades  
  * **Alternative Flows (if applicable):**  
    * If the desired grade is empty, then the system shows a warn message  
    * For courses with multiple grading schemes due to missed assignments there is an option to manually set and add assessments and their weights. 

* **Use Case 4:** Export tasks as a calendar file  
  * **Lead:** **Andy Chen**  
  * **Main Flow:**  
    * System takes all the tasks along with their deadline, weight and perhaps user’s comment regarding the task.  
    * User selects their device and the system creates a calendar file consisting of their information for that specific environment.   
    * A download button will appear allowing the user to access the calendar file.  
  * **Alternative Flows (if applicable):**

* **Use Case 5:** Data persistence  
  * **Lead:** Leo  
  * **Main Flow:**  
    * User adds course, syllabus, or task and the Systems saves to local memory or database  
    * When the user closes the app, the tasks will be saved to local memory, and when they open the app, during system initialization, it will acquire the previous sessions.  
        
* **Use Case 6:** User login/create account  
  * **Lead:** Eric  
  * **Main Flow:**  
    * If user selects login; system asks for username and password and checks if credentials match the existing account in the user base. User is logged in to the main page  
    * If user selects create account, system asks for email, username and password. Once input is validated, system saves new credentials to database  
  * **Alternative Flows (if applicable):**  
    * If login credentials are invalid, system displays “Invalid login credentials, please try again”  
    * If username already exists when a user is creating an account, system displays “Username is not Available, please choose another one”

## Additional Use Cases (Future Development)

* **Use Case 7:** Prioritization of tasks  
  * **Lead:** \[Team Member Name\]  
  * **Main Flow:**  
    * System evaluates weights of tests/assignments and due dates  
    * Suggests what tasks to prioritize based on grade impact  
    * Labels tasks as “high priority” on the calendar

# 4\. Proposed Entities for our Domain

* **Entity Name:** **User**  
  * **Instance Variables:**  
    * private final String: userId  
    * private final String: name  
    * private final String: email  
    * private final String: timezone  
* **Entity Name:** **Course**  
  * **Instance Variables:**  
    * private final String: courseId  
    * private final String: userId  
    * private final String: code  
    * private final String: name  
    * private final String: term  
    * private final String: meetingInfo  
    * private final String: instructor  
* **Entity Name: Syllabus**  
  * **Instance Variables:**  
    * private final String: syllabusId  
    * private final String: courseId  
    * private final String: sourceFilePath  
    * private final java.time.Instant: parsedAt  
* **Entity Name: MarkingScheme**  
  * **Instance Variable:**  
    * private final String: schemeId  
    * private final String: courseId  
    * private final List\<MarkingScheme.WeightComponent\>: components  
* **Entity Name: MarkingScheme.WeightComponent**  
  * **Instance Variable:**  
    * private final String: componentId  
    * private final String: name  
    * private final String AssessmentType: type  
    * private final double: weight  
    * private final Integer: count  
* **Entity Name: Assessment**  
  * **Instance Variable:**  
    * private final String: assessmentId  
    * private final String: courseId  
    * private final String: title  
    * private final AssessmentType: type // enum AssessmentType { TEST, ASSIGNMENT, EXAM, QUIZ, PROJECT, OTHER }  
    * private final java.time.Instant: startsAt  
    * private final java.time.Instant: endsAt  
    * private final Long: durationMinutes  
    * private final Double: weight  
    * private final String: schemeComponentId  
    * private final String: location  
    * private final String: notes  
* **Entity Name: Task**  
  * **Instance Variable:**  
    * private final String: taskId  
    * private final String: userId  
    * private final String: courseId  
    * private final String: assessmentId  
    * private final String: title  
    * private final java.time.Instant: dueAt  
    * private final Integer: estimatedEffortMins //Using Integer instead of int because it can be optional (be null instead of 0\)  
    * private final Integer: priority  
    * private final TaskStatus: status // enum TaskStatus { TODO, IN\_PROGRESS, DONE, CANCELLED }  
    * private final String: notes  
* **Entity Name: GradeEntry:**  
  * **Instance Variable:**  
    * private final String: gradeEntryId  
    * private final String: assessmentId  
    * private final Double: pointsEarned  
    * private final Double: pointsPossible  
    * private final Double: percent  
    * private final java.time.Instant: gradedAt  
    * private final String: feedback  
* **Entity Name: ScheduleEvent**  
  * **Instance Variable:**  
    * private final String: eventId  
    * private final String: userId  
    * private final String: title  
    * private final java.time.Instant: startsAt  
    * private final java.time.Instant: endsAt  
    * private final String: location  
    * private final String: notes  
    * private final SourceKind: source // enum SourceKind { ASSESSMENT, TASK }  
    * private final String: sourceId

# 5\. Proposed API/Library

* **API/Library Name:** Gemini  
  * **Documentation Link:** [https://ai.google.dev/gemini-api/docs](https://ai.google.dev/gemini-api/docs)   
  * **How it will be used:**   
    * Parsed PDF text will be sent to the model to extract structured syllabus data (assessments, due dates, weights) via JSON-formatted responses.  
  * **Successfully Called:** Not yet  
* **API/Library Name:** Google Calendar API  
  * **Documentation Link:** [https://developers.google.com/workspace/calendar/api/quickstart/java](https://developers.google.com/workspace/calendar/api/quickstart/java)  
  * **How it will be used:** Directly create/update calendar events from the app instead of manual .ics import, subject to user OAuth consent.  
  * **Successfully Called:** Not yet.  
* **API/Library Name:** Apache PDFBox (local)  
  * Documentation Link: [https://pdfbox.apache.org/](https://pdfbox.apache.org/)  
  * How it will be used:  
    * Extract text and metadata from PDF syllabi locally in Java before AI parsing to save token.  
  * Successfully Called: Not yet.  
* **API/Library Name:** iCal4j(local)  
  * **Documentation Link:** [https://www.ical4j.org/](https://www.ical4j.org/)  
  * **How it will be used:**  
    * Generate standards-compliant `.ics` files for assessments/tasks with reminders that users can import to Google/Apple/Outlook calendars.  
  * **Successfully Called:** Not yet.  
* **API/Library Name:** SQLite JDBC (Xerial)  
  * **Documentation Link:** [https://github.com/xerial/sqlite-jdbc](https://github.com/xerial/sqlite-jdbc)  
  * **How it will be used:**  
    * Local persistent storage for users, courses, assessments, tasks, marking schemes, and parsing job logs via standard JDBC.  
  * **Successfully Called:** Not yet.

