## What is this ?
Simple Web Application Framework in Java.


## Kaze = Wind
Kaze means wind in japanese.（日本語表記：風・かぜ）


## Status
Developping. 


## Sample Application

```
package sample.kaze;

import kaze.App;
import kaze.http.Route;
import kaze.http.Req;
import kaze.http.Res;

public class Main {
  
  @Route({"GET", "/hello"})
  public void hello(Req req, Res res) {
    res.json("msg", "Hello!");
  }
  
  public static void main(String[] args) {
    App.start("sample.kaze");
  }
}
```

Run as java application, and serves ...

```
$ curl http://localhost:8080/hello -s
{"msg":"Hello!"}
```
