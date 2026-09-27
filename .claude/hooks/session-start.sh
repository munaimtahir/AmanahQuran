#!/bin/bash
# SessionStart hook for Claude Code on the web: installs the Android SDK so
# the Gradle unit tests, lint and content validation gates can run.
set -euo pipefail

if [ "${CLAUDE_CODE_REMOTE:-}" != "true" ]; then
  exit 0
fi

SDK_ROOT="${ANDROID_SDK_ROOT:-$HOME/android-sdk}"
CMDLINE_TOOLS_ZIP="commandlinetools-linux-13114758_latest.zip"
SDKMANAGER="$SDK_ROOT/cmdline-tools/latest/bin/sdkmanager"

if [ ! -x "$SDKMANAGER" ]; then
  tmp="$(mktemp -d)"
  curl -fsSL "https://dl.google.com/android/repository/$CMDLINE_TOOLS_ZIP" -o "$tmp/tools.zip"
  unzip -q "$tmp/tools.zip" -d "$tmp"
  mkdir -p "$SDK_ROOT/cmdline-tools"
  rm -rf "$SDK_ROOT/cmdline-tools/latest"
  mv "$tmp/cmdline-tools" "$SDK_ROOT/cmdline-tools/latest"
  rm -rf "$tmp"
fi

# compileSdk / targetSdk = 36 (apps/android/app/build.gradle.kts).
if [ ! -d "$SDK_ROOT/platforms/android-36" ] || [ ! -d "$SDK_ROOT/build-tools/36.0.0" ]; then
  yes | "$SDKMANAGER" --sdk_root="$SDK_ROOT" --licenses >/dev/null || true
  "$SDKMANAGER" --sdk_root="$SDK_ROOT" "platform-tools" "platforms;android-36" "build-tools;36.0.0" >/dev/null
fi

ANDROID_DIR="$CLAUDE_PROJECT_DIR/apps/android"
echo "sdk.dir=$SDK_ROOT" > "$ANDROID_DIR/local.properties"

if [ -n "${CLAUDE_ENV_FILE:-}" ]; then
  echo "export ANDROID_HOME=\"$SDK_ROOT\"" >> "$CLAUDE_ENV_FILE"
  echo "export ANDROID_SDK_ROOT=\"$SDK_ROOT\"" >> "$CLAUDE_ENV_FILE"
fi

# Warm the Gradle wrapper and dependency cache so the first test run is fast.
cd "$ANDROID_DIR"
./gradlew --no-daemon --quiet :app:dependencies --configuration debugCompileClasspath >/dev/null
