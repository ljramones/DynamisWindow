# DynamisWindow Architecture Review

Date: 2026-03-10  
Scope: Deep boundary ratification for `DynamisWindow` (review/documentation only)

## 1. Repo Overview

Observed modules:

- `window-api`
- `window-glfw`
- `window-test`

Observed implementation shape:

- `window-api` defines platform/window contracts: window lifecycle, window/surface handles, window events, raw input events, and render-surface lifecycle interfaces.
- `window-glfw` provides concrete backend implementation for GLFW-based window creation, event pumping, callback translation, buffer swap, and optional Vulkan surface handle creation.
- `window-test` provides deterministic fake window + fake window system for CI-safe upstream tests.

Dependency signals from poms/code:

- `window-api` has no LWJGL/native dependency.
- `window-glfw` depends on LWJGL (`lwjgl`, `lwjgl-glfw`, `lwjgl-vulkan`) and `window-api`.
- `window-test` depends on `window-api` and does not pull runtime engine feature dependencies.
- No direct dependencies on `DynamisInput`, `DynamisUI`, or `DynamisLightEngine` modules.

## 2. Strict Ownership Statement

### What DynamisWindow should own

- Native/platform window lifecycle abstraction and concrete backend implementations.
- Platform/window state contracts: size/framebuffer size, focus, close, resizable/window config.
- Raw input event capture/exposure from platform callbacks (keys, mouse buttons, cursor movement, scroll).
- Render-surface integration boundary (window surface handles for OpenGL/Vulkan interop), without owning render policy.
- Deterministic fake window harnesses for integration tests.

### What is appropriate for a window/platform subsystem

- Window creation/destruction and event polling.
- Translation of backend-native callback payloads into normalized **raw** event contracts (`InputEvent`, `WindowEvent`).
- Surface handle creation/destruction lifecycle contracts as platform glue.

### What DynamisWindow must never own

- Input normalization/action mapping/context resolution (belongs to `DynamisInput`).
- UI presentation/runtime policy (belongs to `DynamisUI`).
- Render planning/frame-graph policy (belongs to `DynamisLightEngine`).
- GPU resource lifetime orchestration (belongs to `DynamisGPU`).
- World/session/gameplay/scripting control policy.

## 3. Dependency Rules

### Allowed dependencies for DynamisWindow

- Core Java/platform/native backend libraries required for concrete window backends.
- `window-api` as contract substrate for backend implementations.

### Forbidden dependencies for DynamisWindow

- Input semantic/action frameworks (`ActionId`, `InputMap`, context stacks) from `DynamisInput`.
- UI widget/layout/runtime packages from `DynamisUI`.
- Render-policy/planner concerns from `DynamisLightEngine`.
- World/session/scripting/content policy layers.

### Who may depend on DynamisWindow

- `DynamisInput` (raw input event ingestion).
- `DynamisUI` runtime glue (window sizing/focus and platform event integration through higher adapters).
- Render backends and runtime integration layers that need platform surface/window handles.

### Boundary requirements

- `DynamisWindow` remains upstream raw-event authority.
- `DynamisInput` starts at raw-event normalization/action mapping.
- `DynamisUI` consumes translated/normalized signals above input boundary.
- `DynamisLightEngine` may consume window/surface lifecycle as platform substrate only, not as policy host inside Window.

## 4. Public vs Internal Boundary

### Canonical public surface (recommended)

- `window-api` contracts:
  - `WindowSystem`, `Window`, `WindowConfig`
  - `InputEvent`, `WindowEvent`, `WindowEvents`
  - `RenderSurfaceLifecycle`, `SurfaceHandle`, `SurfaceType`
  - `WindowHandle`, `WindowSize`, `BackendHint`

- `window-test` fake contracts intended for test integration (`FakeWindowSystem`, `FakeWindow`).

### Internal/implementation surface (should remain internal)

- GLFW callback mapping details and backend resource management in `window-glfw`.
- Backend-specific threading/initialization/shutdown logic (`GlfwWindowSystem` internals).

### Boundary concern

- `Window` currently includes `getVulkanSurfaceHandle()` while `RenderSurfaceLifecycle` also exposes surface creation. This is workable, but dual paths can blur the clean surface-lifecycle contract if expanded.

## 5. Policy Leakage / Overlap Findings

## Major clean boundaries confirmed

- Strong explicit non-ownership statement in `AGENTS.md` aligns with code.
- Raw input is exposed as raw events only; no action/context semantics are present.
- No UI policy logic appears in window modules.
- No render planner/frame-graph policy appears in window modules.
- Fake window harness cleanly supports upstream deterministic input tests.

## Policy leakage / overlap identified

- **DynamisInput overlap risk (low):** ensure `InputEvent` remains raw-device oriented and does not grow semantic gameplay action fields.
- **DynamisLightEngine overlap risk (low-to-moderate):** Vulkan/OpenGL surface conveniences in `Window` can drift toward backend/render policy if expanded beyond platform glue.
- **DynamisUI overlap risk (low):** UI-facing size/focus events are appropriate, but UI event routing/layout policy must remain outside Window.

## 6. Ratification Result

**Judgment: ratified with constraints**

Why:

- The repo cleanly behaves as platform/window/raw-event authority with clear module split.
- It stays upstream of Input/UI/LightEngine policy ownership.
- Constraints are mainly to prevent future semantic creep in raw event contracts and render-policy creep in surface helpers.

## 7. Recommended Next Step

1. Keep strict one-way boundary:
   - Window captures raw platform events and surface lifecycle.
   - Input normalizes.
   - UI/World/Scripting consume normalized results above Input.
2. In future integration review, tighten Window ↔ LightEngine contract around surface lifecycle only (no render-policy responsibilities).
3. Next repo to review: **DynamisCollision** (or `DynamisPhysics` if you want to ratify physical substrate boundaries first).

---

This document is a boundary-ratification review artifact. It does not perform refactors in this pass.
