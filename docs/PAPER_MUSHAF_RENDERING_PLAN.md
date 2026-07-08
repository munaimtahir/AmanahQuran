# Paper Mushaf Rendering Plan

This plan details the design, line-by-line page structure, and layout parameters for rendering the Holy Quran in a traditional physical-style format.

## 1. Objectives
* Achieve a look and feel comparable to high-quality printed Mushafs (cream-colored textured background, dual gold borders, unified line lengths).
* Maintain exact vertical alignment and layout spacing constraints across all device screens without clipping or overflowing.
* Isolate styling from the raw display text characters to preserve Quran text integrity.

## 2. Line-by-Line Page Structure vs. Flow Layout

| Aspect | Line-by-Line Structure (Selected) | Automatic Flow Layout (Rejected) |
|--------|----------------------------------|----------------------------------|
| **Description** | Texts are split into canonical lines directly in the database (e.g. 15 lines per page) and rendered via independent composables. | Ayahs are combined into a single wrapping paragraph with line breaks computed dynamically by the text layout engine. |
| **Outlines** | Fully mirrors printed Mushaf line configurations (stop signs and words end precisely at the margin). | Visual line endings vary based on screen width, wrapping unpredictably and violating Quranic visual structure. |
| **Scaling** | Font sizes scale linearly with the available page height to ensure exactly 15 lines fit without vertical scrolling. | Dynamic wrapping causes text flow to spill off-screen or leave massive blank areas. |
| **RTL Override** | Easy to enforce per line. | Often leads to bidirectional shaping bugs across long text streams. |

## 3. Render Settings and Layout Constraints
- **Vertical Centering**: Lines are placed inside a `Column` with `verticalArrangement = Arrangement.Center`. This distributes spacing evenly on taller screens, maintaining the "floating page block" aesthetics.
- **Base Font Size**: Scaled dynamically starting from `19.sp` and modified by the `fontScale` parameter (`19.sp * fontScale`).
- **Margins & Borders**:
  - Outer padding: `8.dp` horizontal, `6.dp` vertical.
  - Outer border: Gold stroke `2.dp`.
  - Inner border: Gold stroke `0.5.dp` with opacity `0.5` inside a `1.dp` margin.
  - Page body padding: `8.dp` (AmanahSpacing.sm).
