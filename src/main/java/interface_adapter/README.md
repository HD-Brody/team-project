# interface_adapter

Controllers, presenters, and outbound adapters that translate between frameworks and `use_case` ports.

## Subpackages
- `inbound/` — entrypoints such as HTTP controllers or CLIs, mapping requests to `use_case` DTOs.
- `outbound/` — presenters/gateways that adapt `use_case` responses to external protocols (calendar, AI, storage).

## Guidelines
- Only depend on `use_case` interfaces and DTOs plus `entity` types.
- Keep framework-specific annotations/configuration in this layer.
- Do not embed business rules here; delegate to the relevant `use_case` service.
