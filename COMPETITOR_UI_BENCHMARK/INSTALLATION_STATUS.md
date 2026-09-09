# INSTALLATION STATUS — BENCHMARK APPLICATIONS

**Audit Session**: 2026-09-08 / 2026-09-09  
**Target Environment**: Android 14 (API 34 / UpsideDownCake), ABI `x86_64`, Display 1080x2400 (420 dpi), Gesture Navigation.  
**Active Device**: `PlayStore_Benchmark` (`emulator-5556`) with Mesa Intel Iris Xe Hardware GPU Acceleration (`-gpu host`).  
**Baseline Device**: `QuranBenchmark_API36` (`emulator-5554`, Android 16 API 36).

---

## Installation Summary Matrix

| # | Application | Package ID | Source Preference Gate | Status | Live Emulator Verification | Direct Audit Evidence Captured |
| -: | :--- | :--- | :--- | :---: | :--- | :--- |
| 1 | **Quran for Android** | `com.quran.labs.androidquran` | Official Developer GitHub Release (Pref #2) | **PASS** | Fully installed and verified on API 36 & API 34. | 25 on-device screenshots, 4 verified MP4 workflow videos, full accessibility audit. |
| 2 | **Al Quran (Tafsir & by Word)** | `com.greentech.quran` | Google Play Store (Pref #1) | **PASS** | Installed via authenticated Play Store on `PlayStore_Benchmark`. Verified native x86_64 execution. | 27 live screenshots, 1 31s workflow video (`greentech_reading_workflow.mp4`), font/script switcher (`Noorehuda` IndoPak), OLED dark theme, offline airplane mode test. |
| 3 | **Quran by Quranly** | `com.quranly.app` | Google Play Store (Pref #1) | **PASS** | Installed via authenticated Play Store on `PlayStore_Benchmark`. Verified native x86_64 execution. | 22 live screenshots, 1 32s workflow video (`quranly_reading_workflow.mp4`), habit onboarding audit, discovery of mandatory login/signup wall blocking Quran access, offline blockage test. |
| 4 | **Tarteel: AI Quran** | `com.mmmoussa.iqra` | Google Play Store (Pref #1) | **PASS** | Installed via authenticated Play Store on `PlayStore_Benchmark`. Verified native x86_64 execution. | 13 live screenshots, 1 35s workflow video (`tarteel_reading_workflow.mp4`), AI recitation onboarding, premium mistake detection prompt, mandatory account barrier, offline launch hang test. |
| 5 | **Aasan Tarjuma-e-Quran** | `com.atq.quranemajeedapp.org.atq` | Google Play Store (Pref #1) | **PASS** | Installed via authenticated Play Store on `PlayStore_Benchmark`. Verified native x86_64 execution. | 18 live screenshots, 1 33s workflow video (`aasan_tarjuma_reading_workflow.mp4`), Mufti Taqi Usmani Urdu Nastaliq typography, word-by-word gloss, continuous Mushaf mode, font zoom test, offline reading test. |
| 6 | **The Glorious Quran** | `com.tgq.irfanulquran` | Google Play Store (Pref #1) | **PASS** | Installed via authenticated Play Store on `PlayStore_Benchmark`. Verified native x86_64 execution. | 7 live on-device screenshots + 48 Play Store screenshots, 1 32s workflow video (`glorious_quran_reading_workflow.mp4`), Dr. Tahir-ul-Qadri Irfan-ul-Quran English/Urdu dual-language reader, offline test. |
| 7 | **Quran Majeed** | `com.pakdata.QuranMajeed` | Google Play Store (Pref #1) | **INCOMPATIBLE_ARCH** | Play Store reports "Your device isn't compatible with this version" on x86_64 emulator image due to strict ARM-only native library packaging. | 30 high-res store screenshots, complete metadata scrape, 183 mined user reviews, ad-fatigue & permission analysis. |
| 8 | **Muslim Pro** *(Control)* | `com.bitsmedia.android.rakata` | Google Play Store (Pref #1) | **INCOMPATIBLE_ARCH** | Play Store reports "Your device isn't compatible with this version" on x86_64 emulator image due to hardware/ABI restrictions. | 25 high-res store screenshots, complete metadata scrape, 194 mined user reviews, commercial super-app anti-pattern analysis. |

---

## Safety & Compliance Rule Adherence

1. **Zero Credential Exposure**: The user directly authenticated their own test account in Google Play Store on the emulator UI; zero credentials or tokens were handled, passed, or logged by the automated assistant.
2. **Zero Third-Party APK Mirrors**: No untrusted APK mirror portals (APKPure, APKMirror, Aptoide) were used. All packages were sourced strictly from official Google Play servers or verified developer GitHub release tags.
3. **Hardware GPU Performance**: Switched from software SwiftShader to host Mesa Intel Iris Xe GPU acceleration (`-gpu host`), eliminating color buffer exhaustion and enabling 60fps gesture fluid rendering.
4. **Quran Text Integrity Guardrail**: No Quran source files, translations, or Amanah Quran repository source codes were touched or modified. `git status` remains 100% clean.
