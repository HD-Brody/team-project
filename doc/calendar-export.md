# Calendar Export Feature Notes

## Purpose
- Implements Use Case 4 from `doc/project-blueprint.md` — exporting user tasks and events to a portable calendar file (.ics).
- Keeps the feature isolated from persistence and other use cases while exposing the contracts (`ScheduledTaskQueryPort`, `ScheduleEventQueryPort`, `CalendarRenderPort`) future adapters must satisfy.
- Uses iCal4j (docs via `/ical4j/ical4j-user-guide`) to generate standards-compliant iCalendar artifacts.

## Key Components
- **DTOs:** `CalendarExportRequest`, `CalendarExportResponse`, plus persistence snapshots (`ScheduledTaskSnapshot`, `ScheduleEventSnapshot`) and renderer DTOs (`CalendarRenderRequest`, `CalendarRenderResult`).
- **Ports:**  
  - Incoming: `CalendarExportUseCase`  
  - Outgoing: `ScheduledTaskQueryPort`, `ScheduleEventQueryPort`, `CalendarRenderPort`
- **Service:** `use_case/service/CalendarExportService` orchestrates validation, data aggregation, and rendering delegation.
- **Adapters:**  
  - `interface_adapter/outbound/calendar/IcsCalendarRenderer` → renders ICS bytes via iCal4j’s `Calendar`, `VEvent`, and `CalendarOutputter`.  
  - `interface_adapter/outbound/calendar/InMemoryCalendarExportGateway` → lightweight adapter that satisfies the query ports without persistence for demos/tests.

## Wiring Example (In-Memory)
```java
ScheduledTaskSnapshot milestone = new ScheduledTaskSnapshot(
        "task-42", "user-1", "CSC207", "Milestone",
        Instant.parse("2026-02-14T23:59:00Z"), 120, 15.0,
        "Online", "Submit PDF");

ScheduleEventSnapshot lecture = new ScheduleEventSnapshot(
        "event-99", "user-1", "Guest Lecture",
        Instant.parse("2026-02-12T18:00:00Z"),
        Instant.parse("2026-02-12T19:00:00Z"), "BA 1160",
        "Attendance optional", SourceKind.ASSESSMENT, "assess-7");

InMemoryCalendarExportGateway gateway = new InMemoryCalendarExportGateway()
        .addTask(milestone)
        .addScheduleEvent(lecture);

CalendarExportService service = new CalendarExportService(
        gateway, gateway, new IcsCalendarRenderer());

CalendarExportRequest request = new CalendarExportRequest(
        "user-1", "America/Toronto", List.of("CSC207"),
        null, null, List.of(), "winter-term");

CalendarExportResponse response = service.exportCalendar(request);
Files.write(Path.of(response.getFilename()), response.getPayload());
```

## Testing
- `CalendarExportServiceTest` exercises validation paths, task/event aggregation, and renderer delegation with fakes.
- `IcsCalendarRendererTest` generates a sample ICS payload and parses it back with `CalendarBuilder` to verify emitted properties, following the examples referenced in `/ical4j/ical4j-user-guide`.
- `IcsCalendarRendererTest.writesFixtureFileForManualInspection` also writes the rendered bytes to `src/test/resources/fixtures/ics-calendar-renderer-output.ics`; after running `mvn -q -Dtest=IcsCalendarRendererTest test` you can open that file with any calendar client to inspect the output.
- Run `mvn test` (JUnit 5 + Surefire 3.2.5) to execute the suite.

## Next Steps
- Implement real adapters for `ScheduledTaskQueryPort`/`ScheduleEventQueryPort` once persistence is ready.
- Add controllers/presenters in `interface_adapter/inbound` that build `CalendarExportRequest` from UI inputs.
- Explore optional Google Calendar integration using the same `CalendarRenderPort` abstraction.
