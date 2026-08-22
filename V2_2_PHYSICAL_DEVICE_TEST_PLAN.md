# V2.2 Physical Device Test Plan

Execute after a device is connected, with airplane mode enabled after install.

| Area | Checks |
|---|---|
| Reader | Continuous/Ayah, IndoPak/Uthmani, English/Urdu/off, large font, process death |
| Daily Ayah | Home card, history, exact ayah open, date rollover, translation switch |
| Widget | Compact/standard/reading resize, process kill, reboot, theme, exact deep link |
| Activity | 2-minute/3-ayah threshold, streak, calendar, reminders, suppression |
| Accessibility | TalkBack labels, touch targets, Urdu RTL, diacritics, Elder Mode |
| Offline | Search, bookmarks, backup/restore, Trust Center with network disabled |
| OEM | Samsung, Xiaomi/Redmi, Oppo/Vivo, Infinix/Tecno battery/widget behavior |

Capture APK version, device model/API, screenshots, logcat crash scan, and
pass/fail per row. Do not claim audio playback until an approved source is
installed and mapped.
