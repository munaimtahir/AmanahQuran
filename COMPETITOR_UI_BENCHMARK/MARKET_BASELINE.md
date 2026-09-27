# MARKET BASELINE — COMPETITIVE QURAN UX BENCHMARK

> **Audit Date & Timestamp**: 2026-09-08 23:43:34 UTC
> **Environment**: Linux x86_64, Android SDK 36 (API 36 Baklava), Google Play Store Scraper API v1.2.7
> **Scope**: 7 Tier-A Mandatory Quran Applications + 1 Commercial Control Application (Muslim Pro)

---

## 1. Executive Summary Table

| App Name | Package ID | Rating | Reviews | Installs | Monetization | Account Req? | IndoPak | Uthmani | Offline Claim | Streaks/Habits | Ad Trackers |
| :--- | :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **Al Quran (Tafsir & by Word)** | `com.greentech.quran` | 4.89 ⭐ | 6,114 | 10,000,000+ | 100% Free / Waqf | No (Optional fo... | ✅ | ✅ | ✅ | ✅ | 🛡️ Clean |
| **Quran for Android** | `com.quran.labs.androidquran` | 4.76 ⭐ | 17,146 | 50,000,000+ | 100% Free / Waqf | No (Zero accoun... | ✅ | ✅ | ✅ | ❌ | 🛡️ Clean |
| **The Glorious Quran** | `com.tgq.irfanulquran` | 4.79 ⭐ | 18 | 100,000+ | 100% Free / Waqf | No (Direct offl... | ✅ | ✅ | ✅ | ❌ | 🛡️ Clean |
| **Aasan Tarjuma-e-Quran** | `com.atq.quranemajeedapp.org.atq` | 4.82 ⭐ | 39 | 1,000,000+ | 100% Free / Waqf | No... | ✅ | ✅ | ✅ | ❌ | 🛡️ Clean |
| **Tarteel: AI Quran Memorization** | `com.mmmoussa.iqra` | 4.65 ⭐ | 1,732 | 10,000,000+ | IAP/Sub | Yes / Recommend... | ✅ | ✅ | ⚠️ | ✅ | ⚠️ Freemium |
| **Quran by Quranly** | `com.quranly.app` | 4.49 ⭐ | 383 | 1,000,000+ | IAP/Sub | Yes (Sign-in re... | ✅ | ✅ | ✅ | ✅ | ⚠️ Freemium |
| **Quran Majeed – القران الكريم** | `com.pakdata.QuranMajeed` | 4.75 ⭐ | 6,228 | 10,000,000+ | Ads + IAP/Sub | Optional (Cloud... | ✅ | ✅ | ✅ | ✅ | 🚨 Ads |
| **Muslim Pro: Quran Athan Prayer** | `com.bitsmedia.android.muslimpro` | 4.1 ⭐ | 34,517 | 100,000,000+ | Ads + IAP/Sub | Optional/Forced... | ✅ | ✅ | ⚠️ | ✅ | 🚨 Ads |

---

## 2. Detailed Application Profiles

### Al Quran (Tafsir & by Word)
- **Developer**: `Greentech Apps Foundation`
- **Package ID**: `com.greentech.quran`
- **Current Rating**: **4.89 / 5.0** (6,114 reviews)
- **Install Band**: **10,000,000+**
- **Last Updated**: `2026-07-21` (Version: `1.35.8`)
- **Monetization**: Free: `True` | Contains Ads: `False` | Offers In-App Purchases: `False`
- **Account Requirement**: No (Optional for cloud sync)
- **Internet Dependency**: Partial (Downloads translations/tafsir/audio on-demand)
- **Offline Reading Capability**: Yes (Full offline text reading after download)
- **Script Support**: IndoPak: `Yes (IndoPak script supported)` | Uthmani: `Yes (Uthmani Hafs supported)`
- **Content Support**: Urdu: `Yes (Multiple Urdu translations & tafseer)` | English: `Yes (Multiple English translations & tafseer)` | Translations: `Yes (70+ languages, multiple English/Urdu)`
- **Reader Modes**: Mushaf Mode: `Yes (Mushaf view supported)` | Continuous Mode: `Yes (Continuous Ayah scroll list)`
- **Bookmarks & Collections**: Bookmarks: `Yes (Folders, Pins, Tags, Library)` | Collections: `Yes (Custom library collections & pins)`
- **Search Architecture**: Yes (Arabic, translation, voice, root word)
- **Habit & Analytics**: Streaks: `Yes (Reading streaks and planner)` | Reminders: `Yes (Reading habit reminders)` | Analytics: `Yes (Reading goals, time spent, pages read)`
- **Ergonomics & Controls**: Auto-Scroll: `Yes (Auto-scroll during reading/audio)` | Themes: `Yes (Multiple color schemes, dark/light)` | Font Controls: `Yes (Independent Arabic & Translation slider)`
- **Privacy & Data Safety**: Data collected: Device IDs, App interactions, Crash logs. Zero ad trackers claimed, but analytics SDK present.

```text
Store Summary: Start your journey to understand the Quran with translation, tafsir, audio, more
```

---

### Quran for Android
- **Developer**: `quran.com`
- **Package ID**: `com.quran.labs.androidquran`
- **Current Rating**: **4.76 / 5.0** (17,146 reviews)
- **Install Band**: **50,000,000+**
- **Last Updated**: `2026-02-25` (Version: `Varies with device`)
- **Monetization**: Free: `True` | Contains Ads: `False` | Offers In-App Purchases: `False`
- **Account Requirement**: No (Zero account requirement)
- **Internet Dependency**: Initial download required for Mushaf pages & translations; full offline afterwards
- **Offline Reading Capability**: Yes (100% offline once pages and db downloaded)
- **Script Support**: IndoPak: `Yes (Madani 15-line, 16-line IndoPak downloadable)` | Uthmani: `Yes (Madani Uthmani default)`
- **Content Support**: Urdu: `Yes (Downloadable Urdu translations)` | English: `Yes (Saheeh, Pickthall, etc. downloadable)` | Translations: `Yes (30+ translations via downloadable SQLite)`
- **Reader Modes**: Mushaf Mode: `Yes (Native high-res Mushaf page view)` | Continuous Mode: `Yes (Translation view is continuous list)`
- **Bookmarks & Collections**: Bookmarks: `Yes (Page and Ayah bookmarks, tags)` | Collections: `Yes (Custom tagged bookmarks)`
- **Search Architecture**: Yes (Requires translation SQLite; Arabic search supported)
- **Habit & Analytics**: Streaks: `No (No streaks or gamification)` | Reminders: `No` | Analytics: `No`
- **Ergonomics & Controls**: Auto-Scroll: `No (Page-by-page swipe or audio auto-paging)` | Themes: `Yes (Classic, Night mode, Sepia/Brown overlay)` | Font Controls: `Yes (Font sizing for translation mode)`
- **Privacy & Data Safety**: Open source (GPLv2/Apache 2.0). Zero ads. Zero third-party tracker SDKs. Crashlytics optional.

```text
Store Summary: A beautiful Quran application for Android.
```

---

### The Glorious Quran
- **Developer**: `MINHAJ PUBLICATIONS`
- **Package ID**: `com.tgq.irfanulquran`
- **Current Rating**: **4.79 / 5.0** (18 reviews)
- **Install Band**: **100,000+**
- **Last Updated**: `2026-09-02` (Version: `4.1`)
- **Monetization**: Free: `True` | Contains Ads: `False` | Offers In-App Purchases: `False`
- **Account Requirement**: No (Direct offline reader)
- **Internet Dependency**: Low (Quran text bundled; audio/updates network-dependent)
- **Offline Reading Capability**: Yes (Full offline reading)
- **Script Support**: IndoPak: `Yes (IndoPak Nastaliq / South Asian tradition supported)` | Uthmani: `Yes (Arabic Uthmani text)`
- **Content Support**: Urdu: `Yes (Irfan-ul-Quran by Shaykh-ul-Islam Dr. Muhammad Tahir-ul-Qadri)` | English: `Yes (The Manifest Quran by Dr. Muhammad Tahir-ul-Qadri)` | Translations: `Yes (Irfan-ul-Quran Urdu & Manifest Quran English)`
- **Reader Modes**: Mushaf Mode: `Yes (Mushaf page presentation)` | Continuous Mode: `Yes (Continuous verse-by-verse translation)`
- **Bookmarks & Collections**: Bookmarks: `Yes (Surah, Ayah, Page bookmarks)` | Collections: `No (Flat bookmark list)`
- **Search Architecture**: Yes (Search in Arabic, Urdu, English text)
- **Habit & Analytics**: Streaks: `No (Sacred traditional reader, zero gamification)` | Reminders: `No` | Analytics: `No`
- **Ergonomics & Controls**: Auto-Scroll: `Yes (Auto-scroll capability)` | Themes: `Yes (Light, Dark, Sepia themes)` | Font Controls: `Yes (Arabic font size, Urdu font size, English font size)`
- **Privacy & Data Safety**: Minhaj Publications. Zero ads. No commercial monetization. Sadaqah/Waqf orientation.

```text
Store Summary: Quran, Qibla, Salah, IQVoice, Mushaf, translations and audio
```

---

### Aasan Tarjuma-e-Quran
- **Developer**: `UsmanPervez`
- **Package ID**: `com.atq.quranemajeedapp.org.atq`
- **Current Rating**: **4.82 / 5.0** (39 reviews)
- **Install Band**: **1,000,000+**
- **Last Updated**: `2026-08-27` (Version: `8.2`)
- **Monetization**: Free: `True` | Contains Ads: `False` | Offers In-App Purchases: `False`
- **Account Requirement**: No
- **Internet Dependency**: Low (Bundled text, optional audio streaming)
- **Offline Reading Capability**: Yes (Completely offline Quran text and translation)
- **Script Support**: IndoPak: `Yes (Optimized IndoPak Urdu Nastaliq typography)` | Uthmani: `Yes (Arabic Uthmani script option)`
- **Content Support**: Urdu: `Yes (Primary focus: Clear Urdu commentary and translation)` | English: `No (Urdu-first focus)` | Translations: `Yes (Aasan Tarjuma-e-Quran by Mufti Taqi Usmani)`
- **Reader Modes**: Mushaf Mode: `Yes (Traditional 15/16 line Mushaf style)` | Continuous Mode: `Yes (Ayah-by-ayah with inline Urdu translation)`
- **Bookmarks & Collections**: Bookmarks: `Yes (Ayah bookmarks and last read)` | Collections: `No`
- **Search Architecture**: Yes (Urdu keyword search & Ayah jump)
- **Habit & Analytics**: Streaks: `No (Zero gamification)` | Reminders: `No` | Analytics: `No`
- **Ergonomics & Controls**: Auto-Scroll: `Yes (Variable speed autoscroll)` | Themes: `Yes (Day, Night, Sepia, Eye-protection)` | Font Controls: `Yes (Dedicated Urdu Nastaliq font sizing)`
- **Privacy & Data Safety**: Islamic non-profit foundation. Zero commercial advertising. Privacy-focused.

```text
Store Summary: Urdu Translation and Tafseer of Quran by Mufti Muhammad Taqi Usmani
```

---

### Tarteel: AI Quran Memorization
- **Developer**: `Tarteel Inc.`
- **Package ID**: `com.mmmoussa.iqra`
- **Current Rating**: **4.65 / 5.0** (1,732 reviews)
- **Install Band**: **10,000,000+**
- **Last Updated**: `2026-08-27` (Version: `5.81.2`)
- **Monetization**: Free: `True` | Contains Ads: `False` | Offers In-App Purchases: `True`
- **Account Requirement**: Yes / Recommended (Freemium account for tracking and AI sync)
- **Internet Dependency**: High (AI voice recognition, server processing, cloud sync)
- **Offline Reading Capability**: Partial (Offline reading cached; AI features require internet)
- **Script Support**: IndoPak: `Yes (IndoPak script mode available)` | Uthmani: `Yes (Standard Madani Uthmani)`
- **Content Support**: Urdu: `Yes (Urdu translation available)` | English: `Yes (Multiple English translations)` | Translations: `Yes (Multiple English, French, Urdu translations)`
- **Reader Modes**: Mushaf Mode: `Yes (Modern Mushaf reader mode)` | Continuous Mode: `Yes (Continuous listening stream)`
- **Bookmarks & Collections**: Bookmarks: `Yes (Favorites, pinned verses, memorization lists)` | Collections: `Yes (Hifz memorization sets, mistranscription review)`
- **Search Architecture**: Yes (Voice AI search, verse reference search, text search)
- **Habit & Analytics**: Streaks: `Yes (Daily streak, goal tracker, activity heatmap)` | Reminders: `Yes (Push notifications for daily hifz/reading goals)` | Analytics: `Yes (Extensive: words recited, minutes listened, accuracy rate)`
- **Ergonomics & Controls**: Auto-Scroll: `Yes (Voice-following autoscroll as you recite)` | Themes: `Yes (Modern dark, OLED black, light, sepia)` | Font Controls: `Yes (Modern slider controls, dynamic type)`
- **Privacy & Data Safety**: Commercial freemium SaaS. Voice audio uploaded/processed. Analytics SDKs (Mixpanel, Sentry, Firebase). Subscription paywalls.

```text
Store Summary: AI Quran Companion. Real-time feedback for memorization & recitation.
```

---

### Quran by Quranly
- **Developer**: `Quranly`
- **Package ID**: `com.quranly.app`
- **Current Rating**: **4.49 / 5.0** (383 reviews)
- **Install Band**: **1,000,000+**
- **Last Updated**: `2026-09-04` (Version: `3.0.74`)
- **Monetization**: Free: `True` | Contains Ads: `False` | Offers In-App Purchases: `True`
- **Account Requirement**: Yes (Sign-in required to maintain streaks and cloud habit stats)
- **Internet Dependency**: Moderate (Syncs habits, downloads content, streak servers)
- **Offline Reading Capability**: Yes (Cached offline reading sessions)
- **Script Support**: IndoPak: `Yes (South Asian script option)` | Uthmani: `Yes (Uthmani default)`
- **Content Support**: Urdu: `Limited (Primarily English habit-focused audience)` | English: `Yes (Modern simplified English translation)` | Translations: `Yes (Clean English translations)`
- **Reader Modes**: Mushaf Mode: `Yes (Card-based Mushaf view)` | Continuous Mode: `Yes (Reading session flow)`
- **Bookmarks & Collections**: Bookmarks: `Yes (Reflections, bookmarks)` | Collections: `Yes (Habit challenges, routine playlists)`
- **Search Architecture**: Yes (Basic verse reference and Surah index search)
- **Habit & Analytics**: Streaks: `Yes (Core product architecture: Streaks, freezes, habit loops)` | Reminders: `Yes (Aggressive habit nudges, daily reminders)` | Analytics: `Yes (Comprehensive habit metrics: daily pages, time read, streak calendar)`
- **Ergonomics & Controls**: Auto-Scroll: `No` | Themes: `Yes (Minimalist pastel, dark, light themes)` | Font Controls: `Yes (Clean modern typography sliders)`
- **Privacy & Data Safety**: Commercial subscription app. Heavy gamification. Firebase, Adjust/Branch attribution, analytics. Subscription paywalls ($/mo).

```text
Store Summary: The smartest way to develop the habit of reading the Quran.
```

---

### Quran Majeed – القران الكريم
- **Developer**: `Pakdata`
- **Package ID**: `com.pakdata.QuranMajeed`
- **Current Rating**: **4.75 / 5.0** (6,228 reviews)
- **Install Band**: **10,000,000+**
- **Last Updated**: `2026-08-21` (Version: `Varies with device`)
- **Monetization**: Free: `True` | Contains Ads: `True` | Offers In-App Purchases: `True`
- **Account Requirement**: Optional (Cloud backup requires account)
- **Internet Dependency**: Moderate (Prayer times, audio streaming, ads require internet)
- **Offline Reading Capability**: Yes (Offline reading of downloaded Mushaf)
- **Script Support**: IndoPak: `Yes (Classic Pakistani IndoPak 15/16 line Nastaliq)` | Uthmani: `Yes (Uthmani Hafs & IndoPak switchable)`
- **Content Support**: Urdu: `Yes (Multiple Urdu translations: Jalandhri, Maududi, Ahmed Raza)` | English: `Yes (Pickthall, Yusuf Ali, Saheeh, Mohsin Khan)` | Translations: `Yes (45+ translations, multiple Urdu & English)`
- **Reader Modes**: Mushaf Mode: `Yes (Classic Pakistani IndoPak printed Mushaf reproduction)` | Continuous Mode: `Yes (Verse-by-verse list mode)`
- **Bookmarks & Collections**: Bookmarks: `Yes (Bookmarks, notes, tags)` | Collections: `Yes (Folders, bookmark categories)`
- **Search Architecture**: Yes (Voice search, Arabic, translation search)
- **Habit & Analytics**: Streaks: `Yes (Reading tracker, daily reminders)` | Reminders: `Yes (Prayer times, Athan, reading notifications)` | Analytics: `Yes (Reading duration, Quran completion tracking)`
- **Ergonomics & Controls**: Auto-Scroll: `Yes (Auto-scroll reading mode)` | Themes: `Yes (Green, Classic, Night mode, Sepia)` | Font Controls: `Yes (Font sizing, script selection, line spacing)`
- **Privacy & Data Safety**: Heavy advertising SDKs (Google AdMob, Unity, AppLovin). Analytics SDKs. Commercial banners, interstitial video ads, in-app purchases.

```text
Store Summary: Trusted by 100 Million Muslims Globally - Prayer Times, Athan, Qibla Finder
```

---

### Muslim Pro: Quran Athan Prayer
- **Developer**: `Bitsmedia`
- **Package ID**: `com.bitsmedia.android.muslimpro`
- **Current Rating**: **4.1 / 5.0** (34,517 reviews)
- **Install Band**: **100,000,000+**
- **Last Updated**: `2026-09-02` (Version: `Varies with device`)
- **Monetization**: Free: `True` | Contains Ads: `True` | Offers In-App Purchases: `True`
- **Account Requirement**: Optional/Forced nudges (Free tier functional with heavy ads)
- **Internet Dependency**: High (Community feed, streaming TV/Qalbox, ads, prayer calculations)
- **Offline Reading Capability**: Partial (Quran text readable offline; app is heavily online)
- **Script Support**: IndoPak: `Yes (IndoPak script available)` | Uthmani: `Yes (Standard Uthmani)`
- **Content Support**: Urdu: `Yes (Urdu translation)` | English: `Yes (English translation)` | Translations: `Yes (40+ languages)`
- **Reader Modes**: Mushaf Mode: `Yes (Digital Mushaf pages)` | Continuous Mode: `Yes (Ayah scroll list)`
- **Bookmarks & Collections**: Bookmarks: `Yes (Favorites, pins)` | Collections: `Yes (Playlist style collections)`
- **Search Architecture**: Yes (Keyword search)
- **Habit & Analytics**: Streaks: `Yes (Prayer tracker, fasting tracker, Quran reading tracker)` | Reminders: `Yes (Athan, prayer alarms, Quran reminder)` | Analytics: `Yes (Islamic lifestyle analytics)`
- **Ergonomics & Controls**: Auto-Scroll: `Yes (Auto-scroll with audio)` | Themes: `Yes (Light, dark, colored accents)` | Font Controls: `Yes (Font size controls)`
- **Privacy & Data Safety**: Extreme commercial pressure. Full-screen video ads, banners, subscriptions (Premium, Qalbox video streaming). Location tracking history (past public scrutiny on location data brokers). Multiple third-party tracking SDKs.

```text
Store Summary: Never miss a prayer: Azan Reminders and Qibla direction to support your worship.
```

---
