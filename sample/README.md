# Sample app scaffold

This folder contains the starter implementation for an Android + iOS Kotlin Multiplatform sample that uses `nav3-companion` with Koin and Navigation 3.

## What’s included

- shared navigation keys and navigator state
- a shared Koin bootstrap
- a shared Compose host using `NavDisplay`
- one module per feature screen
- Android and iOS entry-point samples

## Layout

```text
sample/
  shared/src/commonMain/kotlin/com/mayakapps/nav3sample/
  androidApp/src/main/
  iosApp/
```

This is intentionally lightweight so it can grow with the docs and the real sample module later.

