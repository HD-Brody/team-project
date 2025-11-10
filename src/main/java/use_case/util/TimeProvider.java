package use_case.util;

import java.time.Instant;

/**
 * Supplies time references to keep domain logic deterministic and testable.
 */
public interface TimeProvider {
    Instant now();
}
