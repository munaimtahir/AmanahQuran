# Privacy, No-Tracking, and Compliance
# Amanah Quran - V1 Sacred Reader MVP

## 1. Privacy Position

Amanah Quran V1 should be designed so the user can read the Quran offline without creating an account, granting dangerous permissions, being tracked, or sharing any personal data.

## 2. Product Privacy Commitments

1. No ads.
2. No ad SDK.
3. No tracking.
4. No analytics SDK in V1.
5. No advertising ID usage.
6. No forced login.
7. No account creation.
8. No personal data collection.
9. No cloud sync in V1.
10. No donation prompts during reading.
11. No unnecessary notifications.
12. No selling or sharing user data.

## 3. Permissions Policy

V1 should require no dangerous runtime permissions.

Do not request:

1. Location.
2. Contacts.
3. Camera.
4. Microphone.
5. Phone.
6. SMS.
7. Storage access.
8. Notifications.
9. Nearby devices.
10. Advertising ID.

## 4. Local Data Stored

The app may store locally:

1. Last-read position.
2. Bookmarks.
3. Theme preference.
4. Script preference.
5. Arabic font size preference.
6. Elder Mode preference.
7. First-launch message seen state.

This data must remain on the device in V1.

## 5. Network Policy

Core V1 features must work without network:

1. Reader.
2. Navigation.
3. Search.
4. Bookmarks.
5. Last-read.
6. Settings.
7. Trust Center.

External links in Trust Center may open in the device browser, but this must not be required to use the app.

## 6. Play Store Data Safety Target

V1 should be engineered so the Play Store Data Safety form can honestly declare:

1. No data collected.
2. No data shared.
3. No location collected.
4. No personal information collected.
5. No financial information collected.
6. No contacts collected.
7. No app activity analytics collected.
8. No advertising data collected.

## 7. SDK Audit Checklist

Before release, confirm the APK/AAB does not contain:

1. Google AdMob.
2. Meta/Facebook SDK.
3. Firebase Analytics.
4. Any third-party analytics SDK.
5. Any attribution SDK.
6. Any social login SDK.
7. Any push notification SDK.
8. Any tracking or fingerprinting library.

Allowed dependencies should be limited to Android app infrastructure such as Compose, Room, DataStore, Navigation, and other non-tracking AndroidX components.

## 8. Privacy Policy Draft Points

The public privacy policy should state:

1. Amanah Quran does not show ads.
2. Amanah Quran does not collect personal data in V1.
3. Amanah Quran does not require login.
4. Amanah Quran does not use analytics or advertising identifiers in V1.
5. Bookmarks and reading position are stored locally on the user's device.
6. Offline Quran reading works without internet.
7. External source links, if opened, are handled by the user's browser.

## 9. Release Gate

Do not release if:

1. Any analytics SDK is present.
2. Any ad SDK is present.
3. Any unnecessary permission is present.
4. Any core feature requires internet.
5. Any user data is transmitted from the app.
6. Any privacy statement cannot be truthfully supported by the build.
