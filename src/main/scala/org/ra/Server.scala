package org.ra

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scala.io.StdIn
import akka.http.scaladsl.server.directives._
import ContentTypeResolver.Default
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ ContentTypes, HttpEntity, HttpRequest }
import akka.http.scaladsl.server.Directives
import spray.json.{ DefaultJsonProtocol, RootJsonFormat }

import scala.concurrent.Future

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val taskFormat: RootJsonFormat[Task] = jsonFormat3(Task)
  implicit val taskListFormat: RootJsonFormat[TaskList] = jsonFormat1(TaskList)
}

object Server extends Directives with JsonSupport {
  def main(args: Array[String]) {

    import akka.http.scaladsl.model.HttpMethods._

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val route =
      get {
        pathSingleSlash {
          getFromFile("index.html")
        } ~ path("index") {
          getFromFile("index.html")
        } ~ path("create") {
          parameter('task) { task =>
            onSuccess(WebAction.processAction(CreateTaskMessage(Task("", s"$task", "opened")))) { x =>
              complete(WebAction.showTasks())
            }
          }
        } ~ path("delete") {
          parameter('id) { id =>
            onSuccess(WebAction.processAction(DeleteTaskMessage(s"$id"))) { x =>
              complete(WebAction.showTasks())
            }
          }
        } ~ path("statuson") {
          parameter('id) { id =>
            onSuccess(WebAction.processAction(ChangeStatusTaskMessage(s"$id"))) { x =>
              complete(WebAction.showTasks())
            }
          }
        } ~ path("statusoff") {
          parameter('id) { id =>
            onSuccess(WebAction.processAction(ChangeStatusTaskOffMessage(s"$id"))) { x =>
              complete(WebAction.showTasks())
            }
          }
        } ~ path("deleteall") {
          onSuccess(WebAction.processAction(DeleteAllCompletedTasksMessage())) { x =>
            complete(WebAction.showTasks())
          }

        } ~ path("show") {
          complete(WebAction.showTasks())

        } ~ path("newlist") {
          onSuccess(WebAction.processAction(DeleteListMessage())) { x =>
            complete(WebAction.showTasks())
          }
        } ~ path("test") {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<body><h1>Test</h1></body>"))
        } ~ path("testelastic") {
          complete(Http().singleRequest(HttpRequest(GET, uri = "http://elasticsearch:9200")))
        } ~ path("testredis") {
          onSuccess(WebAction.processAction(TestRedis())) { x =>
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<body><h1>$x</h1></body>"))
          }
        } ~ path("eltest") {
          onSuccess(WebAction.processAction(ElasticTest())) { x =>
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<body><h1>$x</h1></body>"))
          }
        } ~ path("retest") {
          onSuccess(WebAction.processAction(TestRedis2())) { x =>
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<body><h1>$x</h1></body>"))
          }
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 9050)

    println(s"Server online at http://localhost:9050/index\nPress RETURN to stop...")
    //    StdIn.readLine() // let it run until user presses return
    //    bindingFuture
    //      .flatMap(_.unbind()) // trigger unbinding from the port
    //      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
