# Annotation Processor Integration Test
This module tests if the generated code (by annotation processor) works as expected by annotating
concrete classes and verify that there MAPPER function is fully functional.

It seems that annotation processing doesn't run from Android Studio when triggering a rebuild. Therefore this module might be compiled and executed from command line

```
gradle clean build
```

Afterwards the generated source code is also visibile in Android Studio and the IDE will not complain about missing classes.
