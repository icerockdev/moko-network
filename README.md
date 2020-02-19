![moko-network](img/logo.png)  
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0) [![Download](https://api.bintray.com/packages/icerockdev/moko/moko-network/images/download.svg) ](https://bintray.com/icerockdev/moko/moko-network/_latestVersion) ![kotlin-version](https://img.shields.io/badge/kotlin-1.3.61-orange)

# Mobile Kotlin network components
This is a Kotlin MultiPlatform library that provide network components for iOS & Android. Library is
 addition to [ktor-client](https://github.com/ktorio/ktor) with gradle plugin to generate entities
 and api classes from OpenAPI (Swagger) specification file. Entities serialization done by
 [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization).

## Table of Contents
- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
- [Samples](#samples)
- [Set Up Locally](#setup-locally)
- [Contributing](#contributing)
- [License](#license)

## Features
- **OpenAPI client code generation** - just configure plugin then use generated entities and api classes;
- **TokenFeature** feature to ktor-client with auth token header support;
- **ExceptionFeature** feature to ktor-client that parse errors from server to domain exceptions.
- **RefreshTokenFeature** feature to ktor-client that handle Unauthorized response from server, try to update token and repeat failed request in case, when token update was successful;

## Requirements
- Gradle version 5.4.1+
- Android API 16+
- iOS version 9.0+

## Versions
- kotlin 1.3.50
  - 0.1.0
  - 0.1.1
- kotlin 1.3.61
  - 0.2.0
  - 0.3.0
  - 0.4.0
  - 0.5.0
  
## Installation
root build.gradle  
```groovy
buildscript {
    repositories {
        maven { url = "https://dl.bintray.com/icerockdev/plugins" }
    }

    dependencies {
        classpath "dev.icerock.moko:network-generator:0.5.0"
    }
}


allprojects {
    repositories {
        maven { url = "https://dl.bintray.com/icerockdev/moko" }
    }
}
```

project build.gradle
```groovy
apply plugin: "dev.icerock.mobile.multiplatform-network-generator"

dependencies {
    commonMainApi("dev.icerock.moko:network:0.5.0")
}
```

settings.gradle  
```groovy
enableFeaturePreview("GRADLE_METADATA")
```

## Usage
TODO

## Samples
More examples can be found in the [sample directory](sample).

## Set Up Locally 
- In [network directory](network) contains `network` library;
- In [gradle-plugin directory](gradle-plugin) contains gradle plugin with OpenAPI implementation generator;
- In [sample directory](sample) contains samples on android, ios & mpp-library connected to apps;
- For test changes locally use `:network:publishToMavenLocal` gradle task, after it samples will use locally published version.
- For test changes of plugin locally use `:gradle-plugin:publishToMavenLocal` gradle task, after it samples will use locally published version.

## Contributing
All development (both new features and bug fixes) is performed in `develop` branch. This way `master` sources always contain sources of the most recently released version. Please send PRs with bug fixes to `develop` branch. Fixes to documentation in markdown files are an exception to this rule. They are updated directly in `master`.

The `develop` branch is pushed to `master` during release.

More detailed guide for contributers see in [contributing guide](CONTRIBUTING.md).

## License
        
    Copyright 2019 IceRock MAG Inc
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
