# Performance Results

## What was measured

- `adb shell am start -W -n org.amanahquran.app/.MainActivity` reported `WaitTime: 1027` ms on a cold start and `WaitTime: 104` ms when the task was already warm.
- `adb logcat -d` did not show an app-owned `AMANAH_PERF_READER` trace stream during this pass.
- Manual interaction confirmed responsive behavior while opening Search `2:255`, Page 540, script switching, page bookmark add/remove, and Trust Center navigation.

## What was not measured

- A controlled cold-start timing series
- Reader open timing after warm database cache
- Search `2:255` timing
- Script-switch timing
- Continue Reading timing after restart

## Current classification

- Performance state: UNKNOWN

## Reason

- The available start timings are encouraging but not a controlled benchmark.
- Reader/search/page-switch timing was observed qualitatively during capture, but not profiled as a repeatable series.
- No optimization was made without a measured regression.
- No app-owned performance trace lines were emitted, so the evidence remains observational rather than benchmarked.
