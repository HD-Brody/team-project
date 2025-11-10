# use_case

Application layer coordinating domain logic and encapsulating use cases.

## Subpackages
- `service/` — orchestrators implementing the use-case interfaces.
- `port/incoming` — API contracts exposed to controllers.
- `port/outgoing` — gateways that `data_access` implementations fulfill.
- `repository/` — domain-centric persistence abstractions.
- `dto/` — request/response shapes exchanged with adapters.
- `util/` — shared utilities such as `TimeProvider` to keep services testable.

## Guidelines
- Depend only on `entity` (for domain models) and locally defined ports/DTOs.
- Keep methods deterministic; inject time, I/O, or randomness via ports/utilities.
- Ensure every `service` has matching tests mirroring business rules.
