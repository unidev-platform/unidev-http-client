# http client library

Library to provide ready to use HTTP client 

Tech stack:
 * gradle
 * apache http client
 * ok http
 * java 11

Local build:
```
./gradlew clean build  publishToMavenLocal
```


HTTP client usage

Gradle file:
```
...
repositories {
	maven { url 'https://jitpack.io' }
}
...
dependencies {
	implementation('com.github.unidev-platform:unidev-http-client:0.0.12')
}

```
Usage in code:
```
        String page = new OkHttp().get("http://canyouseeme.org/");
        String ip = StringUtils.substringBetween(page, "id=\"ip\" type=\"text\" value=\"", "\"");
        System.out.println(ip);
```

Proxy request:
```
        OkHttpClient.Builder builder = OkHttp.builder();
        String proxyPage = new OkHttp(OkHttp.socksProxy(builder, "10.10.10.81", 9001)).get("http://canyouseeme.org/");
        String proxyIp = StringUtils.substringBetween(proxyPage, "id=\"ip\" type=\"text\" value=\"", "\"");
```

References
* https://square.github.io/okhttp/recipes/
* https://square.github.io/okhttp/


# License

This code is released under the MIT License. See LICENSE.
