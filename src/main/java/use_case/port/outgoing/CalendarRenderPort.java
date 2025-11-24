package use_case.port.outgoing;

import use_case.dto.CalendarRenderRequest;
import use_case.dto.CalendarRenderResult;

/**
 * Renders schedule events into a portable calendar artifact (ICS, etc.).
 */
public interface CalendarRenderPort {
    CalendarRenderResult render(CalendarRenderRequest request);
}
