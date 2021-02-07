package co.uk.redpixel.orgcontrib.stats
import munit.CatsEffectSuite

class HelloWorldSpec extends CatsEffectSuite {

  test("ddd") {
//    val a: Map[String, Int] = fs2.Stream.iterate(1)(_ + 1)
//      .map(i => "k" -> i).mapAccumulate()
//      .map(i => i.toString).takeWhile(x => x != "8")
//      .parEvalMap(10)(s => "")

    assertEquals("a", "a")
  }

//  test("HelloWorld returns status code 200") {
//    assertIO(retHelloWorld.map(_.status) ,Status.Ok)
//  }
//
//  test("HelloWorld returns hello world message") {
//    assertIO(retHelloWorld.flatMap(_.as[String]), "{\"message\":\"Hello, world\"}")
//  }
//
//  private[this] val retHelloWorld: IO[Response[IO]] = {
//    val getHW = Request[IO](Method.GET, uri"/hello/world")
//    val helloWorld = HelloWorld.impl[IO]
//    GithubcontributorsapiRoutes.helloWorldRoutes(helloWorld).orNotFound(getHW)
//  }
}