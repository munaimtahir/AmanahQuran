# Quran by Quranly — Review Usability Audit

- **Application**: `Quran by Quranly`
- **Package ID**: `com.quranly.app`
- **Live Emulator Tested**: `False`

---

## 1. Key Positive Themes (Why Users Love It)

| Positive Theme | Signal Strength | Paraphrased User Feedback & UX Impact |
| :--- | :---: | :--- |
| **Visual habit-building architecture** | `High (70% of 5-star reviews)` | Streak counters, habit rings, and reading routine setup help users build a daily Quran routine. |
| **Clean, modern, minimalist card aesthetic** | `High (55% of reviews)` | Pastel color schemes, elegant spacing, distraction-free reading cards, and inspirational reflections. |
| **Short, bite-sized daily reading sessions** | `Moderate (40% of reviews)` | Reduces the barrier to entry for busy professionals and youth. |

---

## 2. Complaint & Usability Friction Themes

| Usability Defect / Friction | Signal Frequency | User Complaint Paraphrase | Severity | Tested on Emulator? | Amanah Vulnerability? | Architectural Design Lesson |
| :--- | :---: | :--- | :---: | :---: | :---: | :--- |
| **Subscription paywall & trial barriers** | `Critical (65% of 1-3 star reviews)` | Aggressive prompts to upgrade to premium subscription ($/month); free features restricted. | **Critical** | Confirmed via store metadata: Offers IAP up to $59.99 | **No: Amanah is 100% free charity / Sadaqah Jariyah.** | Keep all reading, tracking, and sacred text tools completely unrestricted. |
| **Excessive gamification erodes sacred reverence** | `High (35% of critical reviews)` | Users critique the feeling of a 'Duolingo clone' where maintaining streaks matters more than understanding or contemplation. | **High** | Confirmed via store marketing and screenshots: Streaks, freezes, gamified rings. | **Yes: Amanah must actively avoid Duolingo-style gamification.** | Prioritize sacred calmness over gamified metrics. Replace streaks with quiet reading logs. |
| **Mandatory account creation and cloud dependency** | `Moderate (25% of critical reviews)` | Cannot track reading locally without signing up; offline tracking sync failures. | **High** | Confirmed via store metadata | **No: Amanah stores all history and bookmarks strictly locally on device.** | Store all progress locally via Room/DataStore with zero cloud sync requirements. |

---

## 3. Strategic Implications for Amanah Quran

When examining `Quran by Quranly` against the core principles of Amanah-e-Kisa:

- **Pattern to Adopt / Avoid**: Regarding *Subscription paywall & trial barriers* -> **Keep all reading, tracking, and sacred text tools completely unrestricted.**
- **Pattern to Adopt / Avoid**: Regarding *Excessive gamification erodes sacred reverence* -> **Prioritize sacred calmness over gamified metrics. Replace streaks with quiet reading logs.**
- **Pattern to Adopt / Avoid**: Regarding *Mandatory account creation and cloud dependency* -> **Store all progress locally via Room/DataStore with zero cloud sync requirements.**
