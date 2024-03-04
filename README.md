![moko-network](img/logo.png)  
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0) [![Download](https://img.shields.io/maven-central/v/dev.icerock.moko/network) ](https://repo1.maven.org/maven2/dev/icerock/moko/network) ![kotlin-version](https://kotlin-version.aws.icerock.dev/kotlin-version?group=dev.icerock.moko&name=network)
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
- [Set Up Locally](#set-up-locally)
- [Contributing](#contributing)
- [License](#license)

## Features
- **OpenAPI client code generation** - just configure plugin then use generated entities and api classes;
- **TokenFeature** feature to ktor-client with auth token header support;
- **ExceptionFeature** feature to ktor-client that parse errors from server to domain exceptions.
- **RefreshTokenFeature** feature to ktor-client that handle Unauthorized response from server, try to update token and repeat failed request in case, when token update was successful;

## Requirements
- Gradle version 6.8+
- Android API 16+
- iOS version 11.0+

## Installation
root build.gradle  
```groovy
buildscript {
    repositories {
        gradlePluginPortal()
    }

    dependencies {
        classpath "dev.icerock.moko:network-generator:0.21.2"
    }
}


allprojects {
    repositories {
        mavenCentral()
    }
}
```

project build.gradle
```groovy
apply plugin: "dev.icerock.mobile.multiplatform-network-generator"

dependencies {
    commonMainApi("dev.icerock.moko:network:0.21.2")
    commonMainApi("dev.icerock.moko:network-engine:0.21.2") // configured HttpClientEngine
    commonMainApi("dev.icerock.moko:network-bignum:0.21.2") // kbignum serializer
    commonMainApi("dev.icerock.moko:network-errors:0.21.2") // moko-errors integration
}
```

## Usage

1. E.g. put an OpenAPI Specification file to `src/swagger.json` path of the project Gradle module.

2. Setup the project `build.gradle`:

```groovy
mokoNetwork {
    spec("pets") {
        inputSpec = file("src/swagger.json")
    }
    spec("news") {
        inputSpec = file("src/newsApi.yaml")
        packageName = "news"
        isInternal = false
        isOpen = true
        configureTask {
            // here can be configuration of https://github.com/OpenAPITools/openapi-generator GenerateTask
        }
        enumFallbackNull = false
    }
}
```

3. Then run `openApiGenerate` Gradle task and after completion you will get all generated classes in
`build/generated/moko-network` directory.

4. To import all generated classes of model put:

```kotlin
import dev.icerock.moko.network.generated.models.*
``` 

```kotlin
import dev.icerock.moko.network.generated.apis.*
```

5. Then you can use generated API's in your application in the common sourceset:

```kotlin
import dev.icerock.moko.network.generated.apis.*

class TestViewModel : ViewModel() {
    // ..
    
    private val petApi = PetApi(
        basePath = "https://petstore.swagger.io/v2/", // Base API URL
        httpClient = ktorHttpClient, // Reference to Ktor HTTP client object
        json = kotlinxJsonParser // Reference to kotlinx.serialization.json parser object
    )

    fun apiRequest() {
        viewModelScope.launch {
            try {
                val pet = petApi.findPetsByStatus(listOf("available"))

                // ...
            } catch (error: Exception) {
                // ...
            }
        }
    }
}
```

For the moko-network specification generator, you can enable safe enum properties generation mode.
To turn on the mode in `build.gradle` to a `spec` block add flag:

```
enumFallbackNull = true
```

The enabled mode will generate special wrapper `Safeable` for all properties with the enum type,
that which in a situation for an unexpected enum value will return null.


#### Deprecated usage
Old way with `OpenApi Generator Plugin` available by:
```groovy
apply plugin: "dev.icerock.mobile.multiplatform-network-generator-deprecated"

openApiGenerate {
    inputSpec.set(file("src/swagger.json").path)
    generatorName.set("kotlin-ktor-client")
}
```
but this way limited to one spec in one time.

#### moko-network-errors

There is module **moko-network-errors** for [moko-errors](https://github.com/icerockdev/moko-errors)
library that contains built-in mappers for mapping **moko-network** exception classes into 
`StringDesc` objects (there are built-in resources for text in English and Russian).

To use the **moko-network-errors** module just call the `registerAllNetworkMappers` extension of 
`ExceptionMappersStorage` object:

```kotlin
fun initExceptionMappersStorage() {
    ExceptionMappersStorage.registerAllNetworkMappers()
}
```

And then you can use `ExceptionHandler` to automatically handle exceptions for network requests: 

```kotlin
viewModelScope.launch {
    exceptionHandler.handle {
        val pet = petApi.findPetsByStatus(listOf("available"))

        // ...
    }.execute()
}
```

For more information about exception handling see [moko-errors](https://github.com/icerockdev/moko-errors).

## Samples
More examples can be found in the [sample directory](sample).

## Set Up Locally 
- In [network directory](network) contains `network` library;
- In [network-errors directory](network-errors) contains `network-errors` module;
- In [plugins directory](plugins) contains gradle plugin with OpenAPI implementation generator;
- In [sample directory](sample) contains samples on android, ios & mpp-library connected to apps.

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
