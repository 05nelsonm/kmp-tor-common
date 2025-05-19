# test-info

For CI tests to check `OSInfo` host & architecture outputs in select environments

```sh
./gradlew :test-info:assembleFatJar
java -jar test-info/build/libs/test-info.jar "linux-libc" "x86_64"
```
