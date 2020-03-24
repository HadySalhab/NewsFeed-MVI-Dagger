# NewsFeed-MVI-Dagger

## Project Overview

NewsFeed, provides live top and breaking headlines for a country, specific category in a country, single source, or multiple sources, by interacting to [News API](https://newsapi.org/)

 <p align="left"><img src="https://img.shields.io/badge/status-incomplete-orange.svg" /></p>

## Screenshots

![Headlines](https://user-images.githubusercontent.com/51857962/77375276-1e23cd80-6dc1-11ea-9e38-69da7f95a66e.png) ![headline_dialog](https://user-images.githubusercontent.com/51857962/77375281-211ebe00-6dc1-11ea-9e73-d1a97f362fda.png) ![Sources](https://user-images.githubusercontent.com/51857962/77375284-25e37200-6dc1-11ea-965c-1915ad1d78b4.png) ![source-articles](https://user-images.githubusercontent.com/51857962/77375290-2976f900-6dc1-11ea-8a05-4423bdb36a3b.png) ![favorites](https://user-images.githubusercontent.com/51857962/77375294-2bd95300-6dc1-11ea-8d97-0c081ed41224.png)

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
