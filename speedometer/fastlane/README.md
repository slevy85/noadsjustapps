fastlane documentation
----

# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```sh
xcode-select --install
```

For _fastlane_ installation instructions, see [Installing _fastlane_](https://docs.fastlane.tools/#installing-fastlane)

# Available Actions

## Android

### android test

```sh
[bundle exec] fastlane android test
```

Run unit tests

### android build_debug

```sh
[bundle exec] fastlane android build_debug
```

Build a debug APK

### android build_release

```sh
[bundle exec] fastlane android build_release
```

Build a release AAB

### android build_release_apk

```sh
[bundle exec] fastlane android build_release_apk
```

Build a release APK

### android deploy_internal

```sh
[bundle exec] fastlane android deploy_internal
```

Deploy to Google Play internal track

### android promote_to_alpha

```sh
[bundle exec] fastlane android promote_to_alpha
```

Promote from internal to alpha

### android promote_to_beta

```sh
[bundle exec] fastlane android promote_to_beta
```

Promote from alpha to beta

### android promote_to_production

```sh
[bundle exec] fastlane android promote_to_production
```

Promote from beta to production

----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.

More information about _fastlane_ can be found on [fastlane.tools](https://fastlane.tools).

The documentation of _fastlane_ can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
