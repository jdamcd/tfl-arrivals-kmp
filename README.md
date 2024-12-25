# Arrivals

Arrivals is a Kotlin Multiplatform project for live transit times. The first target is a MacOS status bar app. Supported data sources include:
- TfL API for London Underground, Overground, DLR, etc.
- MTA GTFS feeds for NYC Subway
- Custom GTFS feeds for other transit systems (many can be found [here](https://mobilitydatabase.org))

![Screenshot: Arrivals app in the MacOS status bar](screenshot.png)

## Attribution

* Uses this incredible dot matrix display font: [London Underground Typeface](https://github.com/petykowski/London-Underground-Dot-Matrix-Typeface)
* Powered by TfL Open Data
  * OS data © Crown copyright and database rights 2016
  * Geomni UK Map data © and database rights 2019

## Local builds

1. Get a [Transport for London API](https://api-portal.tfl.gov.uk) app key and add it as property in `shared/secret.properties` like `tfl_app_key=YOURKEY`
2. Make sure you have a JDK configured at `$JAVA_HOME`
3. Run the project from Xcode
