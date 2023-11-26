# TFL Arrivals

See your local train station arrivals board in your status bar! It's a MacOS app, but the API layer is built with Kotlin Multiplatform for the flexibility to add more UI targets later.

![Screenshot: arrivals app in the MacOS status bar](screenshot.png)

## Attribution

* Uses this incredible dot matrix display font: [London Underground Typeface](https://github.com/petykowski/London-Underground-Dot-Matrix-Typeface)
* Powered by TfL Open Data
  * OS data © Crown copyright and database rights 2016
  * Geomni UK Map data © and database rights 2019

## Local builds

1. Get a [Transport for London API](https://api-portal.tfl.gov.uk) app key and add it as property in `shared/secret.properties` like `tfl_app_key=YOURKEY`
2. Make sure you have a JDK configured at `$JAVA_HOME`
3. Run the project from Xcode
