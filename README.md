# LastAir

LastAir is a small music-focused social network for Android, built around Last.fm. The idea: keep all your listening in one place, see what your friends are playing, and react to their music.

It is a native Android app written in Kotlin with Jetpack Compose, following Material 3 Expressive.

> **Project status: unfinished.** The app works for the most part, but some features are still missing (see [Progress](#progress)). This repository also serves as a way to track that progress.

## Features

- **Two-step sign-in**: Google account (Credential Manager, Firebase Auth), then Last.fm account linking via OAuth.
- **Home screen** with two pages:
  - *My activity*: carousels of recently played tracks, top albums and top artists.
  - *Friends*: a feed of what your friends are listening to.
- **Friends**: add friends and browse their activity.
- **Reactions**: react to a friend's track with an emoji, and remove your reaction later.
- **Inbox**: see the reactions you received, with real-time notifications.
- **Show all**: ranked grid of tracks, albums and artists, filterable by period (7 days, 1 month, 12 months, all time).
- **High-quality covers**: fetched from the iTunes Search API first (1200x1200), with Last.fm as a fallback. When no cover exists, a gradient generated from the artist name is shown instead.

## Tech stack

| Area | Technology |
|---|---|
| Language / UI | Kotlin, Jetpack Compose, Material 3 Expressive |
| Navigation | Navigation Compose (`NavHost`), deep links |
| Authentication | Firebase Auth, Google Sign-In (Credential Manager) |
| Database | Cloud Firestore |
| Networking | Retrofit, Last.fm API, iTunes Search API |
| Background work | Foreground service listening for reactions |
| Architecture | MVVM (ViewModels + StateFlow), no dependency injection framework |

Package: `com.bughaxx.lastair`

## Project structure

```
app/src/main/java/com/bughaxx/lastair/
├── MainActivity.kt          Entry point, deep links, notification channels
├── Navigation.kt            Navigation graph and floating bottom bar
├── *ViewModel.kt            State logic for each screen
├── *Repository.kt           Firestore, Last.fm and local cache access
├── LastFmApi.kt / Auth.kt   Last.fm client and authentication
├── ItunesHelper.kt          Cover art retrieval
├── ReactionListenerService  Reaction listener and notifications
└── ui/theme/                Theme, colors, home screen
```

## Building the project

The repository intentionally contains no API keys and no Firebase configuration. To build it, you need to provide your own.

### 1. Requirements

- A recent version of Android Studio
- A Last.fm account and a [Last.fm API key](https://www.last.fm/api/account/create)
- A Firebase project with Authentication (Google provider) and Firestore enabled

### 2. Set up Firebase

In the Firebase console, add an Android app with the package name `com.bughaxx.lastair`, add the SHA-1 fingerprint of your debug key, then download `google-services.json` and place it in `app/`.

### 3. Set up Last.fm

Add your credentials to `local.properties` at the project root (this file is ignored by Git):

```properties
LASTFM_API_KEY=your_api_key
LASTFM_API_SECRET=your_shared_secret
```

These values are exposed to the app through `BuildConfig`. The OAuth callback uses the `lastair://callback` scheme.

### 4. Run

Open the project in Android Studio, sync Gradle, then run the app on a device or emulator.

## Progress

Working:

- Google sign-in and Last.fm linking
- Home screen, friends feed, "Show all" screen
- Reactions system (send, change, remove)
- Reaction notifications through a foreground service

To do / in progress:

- [ ] Fix a crash when scrolling the friends feed on some devices
- [ ] Validate reaction notifications end to end
- [ ] "Your friends are listening" notifications via WorkManager
- [ ] Profile creation wizard (the profile picture currently comes from the Google account)

## Contributing

Contributions are welcome. If you spot a bug or have an improvement in mind, please open an issue or a pull request rather than maintaining a separate copy: it will benefit everyone and I am happy to review it.

Forking is of course allowed under the terms of the license below, which means a fork must remain open source under the same license and keep the original credit.

## License

This project is licensed under the GNU General Public License v3.0. See the [LICENSE](LICENSE) file.
