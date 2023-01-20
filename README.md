# http client library

Library to provide ready to use HTTP client 

Tech stack:
 * gradle
 * apache http client
 * ok http
 * phantomjs
 * java 17

Modules:
 * apache-http-client
 * ok-http
 * phantom-js

## HTTP client usage

Usage:
```
...
repositories {
    maven {
        url "https://mvn.universal-development.com/public" 
    }
}
...
dependencies {
	implementation('com.unidev.http-client:apache-http-client:0.2.2')
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

## References
* https://square.github.io/okhttp/recipes/
* https://square.github.io/okhttp/


# License

This code is released under the MIT License. See LICENSE.
