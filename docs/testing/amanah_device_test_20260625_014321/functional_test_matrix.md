# Amanah Quran Functional Test Matrix

| Area | Test | Expected | Actual | Pass/Fail | Evidence |
| ---- | ---- | -------- | ------ | --------- | -------- |
| Install | Clean install | APK installs and package exists | Clean uninstall/install succeeded; package path present | PASS | `install_log.txt` |
| Launch | First launch | Home, no login/prompt/ad/internet requirement | Home opened offline with no login, permission prompt, ad, or popup | PASS | `app_launch_log.txt`, `screenshots/01_home_first_launch.png` |
| Offline | Offline home | Home works without network | Airplane mode enabled; Wi-Fi/data disabled; Home usable | PASS | `device_info.txt`, `screenshots/01_home_first_launch.png` |
| Offline | Offline Surah Index | Surah list accessible | Surah list loaded offline | PASS | `screenshots/02_surah_index.png` |
| Offline | Offline Juz Index | Juz list accessible | Juz list loaded offline | PASS | `screenshots/07_juz_index.png` |
| Offline | Offline Page Index | Page list accessible | Page list loaded offline | PASS | `screenshots/09_page_index.png` |
| Reader | Al-Fatihah opening | Heading and non-duplicated Bismillah | Heading visible; Bismillah appears once as source ayah; 284 ms | PASS | `screenshots/03_surah_fatihah_opening.png`, `performance_raw.csv` |
| Reader | Al-Baqarah opening | Heading and Bismillah | Heading and Bismillah visible; 285 ms | PASS | `screenshots/04_surah_baqarah_opening.png`, `performance_raw.csv` |
| Reader | At-Tawbah no Bismillah | Heading, no Bismillah | Heading visible; no Bismillah; 230 ms | PASS | `screenshots/05_surah_tawbah_no_bismillah.png`, `performance_raw.csv` |
| Reader | Al-Ikhlas opening speed | <300 ms after warmup | Uthmani search-open was 287 ms | PASS | `screenshots/30_sepia_theme_reader.png`, `performance_raw.csv` |
| Juz | Juz 30 timing | <1000 ms | 191 ms from tap | PASS | `full_logcat.txt`, `performance_raw.csv` |
| Juz | Juz 30 boundaries | Headers and short-Surah boundaries visible | Earlier screenshot `08_juz_30_boundaries.png` is launcher content, not valid evidence; not re-captured in resumed scope | NOT VERIFIED | `screenshots/08_juz_30_boundaries.png` must not be used as positive evidence |
| Page | Page 1 | Correct page and content, <300 ms | Correct Page 1; 288 ms | PASS | `screenshots/10_page_001.png` |
| Page | Page 2 | Correct page and content, <300 ms | Correct Page 2; 249 ms | PASS | `screenshots/10b_page_002.png` |
| Page | Page 59 | Correct page and content, <300 ms | Correct Page 59; 171 ms | PASS | `screenshots/11_page_059.png` |
| Page | Page 76 | Correct page and content, <300 ms | Correct Page 76; 160 ms | PASS | `screenshots/12_page_076.png` |
| Page | Page 532 | Correct page and content, <300 ms | Correct Page 532; 152 ms | PASS | `screenshots/13_page_532.png` |
| Page | Page 540 IndoPak | Correct page/script/content | Page 540, IndoPak, first visible 75:35; 144-245 ms depending route | PASS | `screenshots/14_page_540.png`, `screenshots/16_indopak_reader.png` |
| Page | Page 540 Uthmani | Correct page/script/content | Page 540, Uthmani, first visible 75:35; standard reader 138 ms | PASS | `screenshots/18_script_switch_same_page.png`, `screenshots/22_bookmarks_list.png` |
| Page | Last available page | Last packaged page opens | DB maximum is 559; Page 559 opened offline in 259 ms via Search | PASS | `screenshots/15_page_last_559.png`, `performance_raw.csv` |
| Script | Script switching | Switch in reader, retain canonical identity, <500 ms | Only global Settings control exists. Standard Page 540 reload retains identity; Continue Reading path takes ~13.7 s | FAIL | `screenshots/16_indopak_reader.png`, `screenshots/18_script_switch_same_page.png`, `full_logcat.txt` |
| Script | Missing glyphs | No tofu/missing glyphs | No tofu observed in captured IndoPak/Uthmani screens | PASS | `screenshots/16_indopak_reader.png`, `screenshots/18_script_switch_same_page.png` |
| Search | Search screen offline | Opens and searches offline | Search screen and results work offline | PASS | `screenshots/19_search_screen.png` |
| Search | Query `1` | Al-Fatihah result | Al-Fatihah/Surah 1 returned | PASS | `screenshots/20g_search_1_results.png` |
| Search | Query `36` | Ya-Sin result | Ya-Sin/Surah 36 returned | PASS | `screenshots/20f_search_36_results.png` |
| Search | Query `Yaseen` | Ya-Sin result | No result | FAIL | `screenshots/20b_search_yaseen_results.png` |
| Search | Query `Ikhlas` | Al-Ikhlas result | Al-Ikhlas/Surah 112 returned | PASS | `screenshots/20c_search_ikhlas_results.png` |
| Search | Query `Juz 30` | Juz 30 results | Juz 30 ayah results returned | PASS | `screenshots/20d_search_juz30_results.png` |
| Search | Query `Page 540` | Page 540 results | Page 540 ayahs returned | PASS | `screenshots/20e_search_page540_results.png` |
| Search | Query `2:255` result | Open Ayat al-Kursi at 2:255 | Result text is correct, but tap opens reader at 2:1 | FAIL | `screenshots/20_search_2_255_results.png`, `screenshots/21_search_result_opened.png` |
| Search | Arabic phrase | Offline Arabic phrase result | Not executed; ADB keyboard path could not reliably inject Arabic text | NOT EXECUTED | Documented limitation |
| Bookmark | Add ayah bookmark | Add 2:255 and list canonical identity | 2:255 added and listed with Uthmani text | PASS | `screenshots/23_bookmark_ayah_2_255_added.png`, `screenshots/22_bookmarks_list.png` |
| Bookmark | Open ayah bookmark | Open same canonical ayah | Bookmark carries 2:255 but opens at 2:1 | FAIL | `screenshots/23_bookmark_opened.png`, `full_logcat.txt` |
| Bookmark | Add/open page bookmark | Page 540 listed and opens | Uthmani Page 540 listed and opened in 138 ms | PASS | `screenshots/22_bookmarks_list.png`, `performance_raw.csv` |
| Bookmark | Persistence | Bookmarks survive force-stop | Page bookmark survived force-stop/reopen; ayah bookmark was not force-stop-tested before removal | PARTIAL | `screenshots/22_bookmarks_list.png`, device sequence notes |
| Bookmark | Removal | Removed items disappear | Both ayah and page bookmarks removed; empty state shown | PASS | UIAutomator dump recorded `No bookmarks yet` |
| Last read | Continue Reading | Same/nearby content after force-stop, fast | Page 540 identity persisted; Mushaf path showed loading for ~13.7 s | FAIL | `screenshots/27_elder_mode_home.png`, `screenshots/25_continue_reading_after_reopen.png`, `full_logcat.txt` |
| Settings | Script persistence | Script persists after restart | Uthmani persisted after force-stop; later IndoPak selection applied | PASS | UIAutomator checked selected state |
| Settings | Theme persistence | System/Light/Dark/Sepia selectable and persistent | Light, Dark, Sepia selectable; Sepia remained selected after force-stop | PASS | `screenshots/29b_light_theme_settings.png`, `screenshots/29_dark_theme_settings.png`, `screenshots/30_sepia_theme_home_after_restart.png` |
| Settings | Elder Mode | Larger UI persists and remains usable | Elder Mode changed layout and persisted after restart; later disabled | PASS | `screenshots/27_elder_mode_home.png`, `screenshots/27_elder_mode_settings.png` |
| Settings | Elder Mode theme access | No controls hidden | Sepia was not exposed in the first Elder Mode top-screen hierarchy; control required disabling Elder Mode to access reliably | FAIL | UIAutomator settings hierarchy |
| Trust | Trust Center offline | Sources, validation, checksums, privacy offline | Loads offline, but shows checksum `N/A - prototype data`, `NOT VERIFIED`, `PENDING REVIEW`, while release status says `APPROVED` | FAIL | `screenshots/31_trust_center_main.png`, `screenshots/33_trust_center_privacy.png` |
| Trust | IndoPak attribution | Correct source type and attribution | Source visible, but type says `Search normalization` while notes call it display candidate | FAIL | `screenshots/32_trust_center_sources.png` |
| Privacy | Permissions | No dangerous/unnecessary permissions | No dangerous/runtime/Internet/AD_ID permissions; only internal signature permission from AndroidX | PASS | `privacy_permission_audit.txt` |
| Privacy | Ads/analytics/login | None present | No SDK declarations/dependencies found; no login shown | PASS | `privacy_permission_audit.txt`, manifest/build audit |
| Stability | No crash/ANR | No crash or ANR | No app crash, ANR, or OOM observed | PASS | `crash_anr_summary.txt` |
| Stability | Frame pacing | No severe main-thread stalls | Several 31-67 skipped-frame events occurred | WARNING | `full_logcat.txt` |
| Build | Unit tests | Pass | `./gradlew test` successful | PASS | Gradle output summarized in final report |
| Build | Android lint | Pass | `./gradlew lintDebug` successful | PASS | `apps/android/app/build/reports/lint-results-debug.html` |
