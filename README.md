# NewsFeed-MVI-Dagger

## Project Overview

NewsFeed, provides live top and breaking headlines for a country, specific category in a country, single source, or multiple sources, by interacting to [News API](https://newsapi.org/)

 <p align="left"><img src="https://img.shields.io/badge/status-incomplete-orange.svg" /></p>

## Screenshots

![Headlines](https://user-images.githubusercontent.com/51857962/75750498-721a3400-5d78-11ea-9535-608cbb8f64b5.png) ![Sources](https://user-images.githubusercontent.com/51857962/75750506-75152480-5d78-11ea-9d6e-0f1e322a7d99.png)

## API Key Note

**Define key in build.gradle**

In your Android studio root directory, locate the `gradle.properties` under `.gradle` folder and add the following:
Add `NEWS_APIKEY = "YOUR-API-KEY"`.

## Language

[Kotlin](https://kotlinlang.org/)

## Features

- MVI with Android Architecture Components(Room, LiveData, ViewModel)
- Leverage `NetworkBoundResource`
- `Dagger2` for Dependency injection architectural pattern
- Discover the top headlines news in a country
- Look for top headlines news based on Sources and categories
- Users can mark an article as favorite in the details view by tapping a heart icon
- Pagination and endless scrolling using custom pagination.
- Handle network status and network failures
- ConstraintLayout(guidelines, barriers... etc)
- ViewPager2
- Material design.
- Custom Navigation Controller for Multiple navigation graph
- Bottom Navigation Bar
- `Kotlin Coroutines`

## Libraries

- [AndroidX](https://developer.android.com/jetpack/androidx/) - Previously known as 'Android support Library'
- [Glide](https://github.com/bumptech/glide) - for loading and caching images
- [Retrofit 2](https://github.com/square/retrofit) - Type-safe HTTP client for Android and Java by Square, Inc.
- [Gson](https://github.com/google/gson) - for serialization/deserialization Java Objects into JSON and back
- [Dagger 2](https://dagger.dev/) - for dependency injection
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture/)
  - [Room](https://developer.android.com/topic/libraries/architecture/room)
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
  - [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
  - [Navigation](https://developer.android.com/guide/navigation?gclid=CjwKCAiA-vLyBRBWEiwAzOkGVLT4zk8NhxWujb6P4i-NUdcw4U3hWy5gKcnTkHE3IgE18_dayy7bBRoCxtYQAvD_BwE)
- [Material Design](https://material.io/develop/)

## External Resources

- [CodingWithMitch/courses](https://codingwithmitch.com/courses/)

**NewsFeed uses the News API but is not endorsed or certified by NewsApi.**
