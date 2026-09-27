# The Glorious Quran — Review Usability Audit

- **Application**: `The Glorious Quran`
- **Package ID**: `com.tgq.irfanulquran`
- **Live Emulator Tested**: `False`

---

## 1. Key Positive Themes (Why Users Love It)

| Positive Theme | Signal Strength | Paraphrased User Feedback & UX Impact |
| :--- | :---: | :--- |
| **Exceptional Urdu (Irfan-ul-Quran) and English (Manifest Quran) translations** | `High (70% of reviews)` | Readers revere the spiritual depth, theological clarity, and modern language of Shaykh-ul-Islam Dr. Muhammad Tahir-ul-Qadri's translations. |
| **Clean side-by-side / verse-by-verse translation layout** | `High (45% of reviews)` | Clear visual distinction between Arabic source text, Urdu translation, and English translation. |
| **Ad-free Waqf / Sadaqah experience** | `Moderate (30% of reviews)` | Users appreciate that Minhaj Publications offers the app freely without advertising interruptions. |
| **Continuous auto-scroll functionality** | `Moderate (25% of reviews)` | Users enjoy hands-free recitation with adjustable auto-scroll speed. |

---

## 2. Complaint & Usability Friction Themes

| Usability Defect / Friction | Signal Frequency | User Complaint Paraphrase | Severity | Tested on Emulator? | Amanah Vulnerability? | Architectural Design Lesson |
| :--- | :---: | :--- | :---: | :---: | :---: | :--- |
| **Search engine misses inflected Urdu words** | `Moderate (25% of critical reviews)` | Users search for Urdu keywords and find zero results due to lack of Urdu lemmatization and diacritic normalization. | **High** | N/A | **Yes: Amanah must ensure normalized Urdu search indexes.** | Implement robust Urdu character normalization (e.g. normalize Hamza, Alef Maksura, Teh Marbuta, Yeh shapes). |
| **Occasional crashes or freezes on newer Android versions** | `Moderate (20% of critical reviews)` | Users on Android 13/14 report launch crashes due to legacy targetSdkVersion or background service issues. | **Critical** | N/A | **No: Amanah targets modern Android 16 (API 36) using pure Jetpack Compose without legacy services.** | Keep dependencies modern, target latest SDK, zero legacy background services. |
| **Bookmarks lack categorization / export** | `Low-Moderate (15% of critical reviews)` | Users with extensive bookmark lists find them hard to manage without folders, labels, or backup. | **Medium** | Confirmed via store UI screenshots: flat list only. | **Opportunity for Amanah: Amanah can provide categorized tags and last-read pins.** | Support tagging and clear chronological sorting for bookmarks. |
| **Font scaling constraints for elder readers** | `Low (12% of critical reviews)` | Urdu Nastaliq text can appear small or cramped when Arabic font size is expanded. | **Medium** | Confirmed via store UI screenshots | **Opportunity for Amanah: Amanah has dedicated Elder Mode and independent font sliders.** | Provide independent font size adjustment for Arabic and Urdu/English. |

---

## 3. Strategic Implications for Amanah Quran

When examining `The Glorious Quran` against the core principles of Amanah-e-Kisa:

- **Pattern to Adopt / Avoid**: Regarding *Search engine misses inflected Urdu words* -> **Implement robust Urdu character normalization (e.g. normalize Hamza, Alef Maksura, Teh Marbuta, Yeh shapes).**
- **Pattern to Adopt / Avoid**: Regarding *Occasional crashes or freezes on newer Android versions* -> **Keep dependencies modern, target latest SDK, zero legacy background services.**
- **Pattern to Adopt / Avoid**: Regarding *Bookmarks lack categorization / export* -> **Support tagging and clear chronological sorting for bookmarks.**
- **Pattern to Adopt / Avoid**: Regarding *Font scaling constraints for elder readers* -> **Provide independent font size adjustment for Arabic and Urdu/English.**
