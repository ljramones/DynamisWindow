# DynamisWindow Agent Policy

## Scope Boundaries
- `window-api` owns contracts and value types only; it must not depend on LWJGL or native APIs.
- `window-glfw` owns platform window lifecycle, GLFW event pumping, and optional Vulkan surface creation.
- `window-test` owns deterministic in-memory fakes for tests.

## Explicit Non-Ownership
- No renderer or render-graph behavior (belongs to LightEngine).
- No GPU resource lifetime orchestration (belongs to DynamisGPU).
- No UI widgets or layout systems (belongs to DynamisUI).
- No action/axis bindings or input mapping logic (belongs to DynamisInput).
