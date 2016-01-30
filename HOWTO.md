# HOW TO

## Publish a new release

1. Apply any needed change
1. Increment the version number into the Gruntfile
1. Run both `default` and `release` target to build the scripts
1. Increment the version number into the Readme, installation section (gradle has to match the latest tag)
1. Push your changes and create a new tag. The new tag has to be created before pushing the lib.
1. From the root folder, run this command to update the lib (and pushing to JitPack):

  ```./gradlew install```
