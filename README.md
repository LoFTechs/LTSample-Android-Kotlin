# LT SDK for Android sample
![Platform](https://img.shields.io/badge/platform-ANDROID-orange.svg)
![Languages](https://img.shields.io/badge/language-Kotlin-orange.svg)

[![Maven Central](https://img.shields.io/maven-central/v/com.loftechs.sdk/lt.svg?label=maven%20central)](https://search.maven.org/search?q=g:%22com.loftechs.sdk%22%20AND%20a:%22lt%22)

## Introduction

With LT SDK, you can build your own customized application with Call and IM function. This documentary provides a guideline that demonstrates how to build and configure an in-app message and call using LT SDK.

## Getting started

This section explains the steps you need to take before testing the Android sample app.

## Installation

To use our Android sample, you should first install 
[LTSample for Android](https://github.com/LoFTechs/LTSample-Android-Kotlin.git) 1.0.0 or higher.

### Requirements

|Sample|Android| Java         | Gradle      | 
|---|---|--------------|-------------|
| LTSample |5.0 (API level 21) or higher| 17 or higher | ８ or higher |

### Start LT Sample

- You can **clone** the project from the [LTSample repository](https://github.com/LoFTechs/LTSample-Android-Kotlin.git).

```
// Clone this repository
git clone git@github.com:LoFTechs/LTSample-Android-Kotlin.git

// Move to the LT sample
cd LTSample-Android-Kotlin/
```

- Set Develop api data and password to project level `gradle.properties` file:

```properties
Brand_ID="<YOUR_BRAND_ID>"
Auth_API="<YOUR_AUTH_API>"
LTSDK_API="<YOUR_LTSDK_API>"
LTSDK_TurnKey="<YOUR_LTSDK_TURNKEY>"
Developer_Account="<YOUR_DEVELOPER_ACCOUNT>"
Developer_Password="<YOUR_DEVELOPER_PASSWORD>"
License_Key="<YOUR_LINCENSE_KEY>"
```

### Install LT SDK in your android project

With LT SDK, you can build your own customized application with Call and IM function. Refer [DOC](https://loftechs.github.io/LTSDK-Doc)

Step 1. Add MavenCentral to your repositories in your project level build.gradle file:

```
allprojects {
    repositories {
        mavenCentral()
    }
}
```

Step 2. Add the library as a dependency in your module level build.gradle file:

[![Maven Central](https://img.shields.io/maven-central/v/com.loftechs.sdk/lt.svg?label=maven%20central)](https://search.maven.org/search?q=g:%22com.loftechs.sdk%22%20AND%20a:%22lt%22)

```
def LTSDK_version = "x.y.z"
implementation "com.loftechs.sdk:lt:$LTSDK_version"
implementation "com.loftechs.sdk:im:$LTSDK_version"
implementation "com.loftechs.sdk:call:$LTSDK_version"

```

Step 3. Grant system permissions in your module level AndroidManifest.xml file:

```
<uses-permission android:name="android.permission.INTERNET" />
```

### Use LT CallSDK in LTSample project
Step 1. Please contact us and you'll get google-services.json file.

Step 2. Add this line to build.gradle file:

```
apply plugin: 'com.google.gms.google-services'
```

Step 3. Put google-services.json file to LTSample project. 

