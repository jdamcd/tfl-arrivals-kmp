# TFL Arrivals

See your train station arrivals board in your status bar! It's currently just a MacOS app, but the API layer is built with Kotlin Multiplatform for the flexibility to add other UI targets later. 

![Screenshot: arrivals app in the MacOS status bar](screenshot.png)

## Local builds
You'll need to add a valid [Transport for London API](https://api-portal.tfl.gov.uk) app key, and configure the line, station, and platform in `Arrivals.kt`.
