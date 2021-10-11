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

    Copyright (c) 2021 Denis O <denis.o@linux.com>

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
