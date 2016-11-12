package controllers

import java.net.{InetAddress, NetworkInterface}
import javax.inject._

import akka.actor.{ActorSystem, Address}
import akka.cluster.Cluster
import play.api.libs.json.Json
import play.api.mvc._
import play.api.{Configuration, Logger}

import scala.collection.JavaConversions._
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Try

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(config: Configuration, system: ActorSystem) extends Controller {

  val cluster = Cluster(system)
  val clusterPort = config.underlying.getInt("akka.remote.netty.tcp.port")
  val hostAddress = NetworkInterface.getNetworkInterfaces
    .find(_.getName == "eth0")
    .flatMap(_.getInetAddresses.find(_.isSiteLocalAddress).map(_.getHostAddress))
    .getOrElse("127.0.0.1")
  val initialSeedNodes = getSeedNodes
  Logger.info(s"Joining initial seed nodes: ${initialSeedNodes.mkString(", ")}")
  cluster.joinSeedNodes(initialSeedNodes)

  cluster.registerOnMemberRemoved({
    system.registerOnTermination(System.exit(0))
    system.terminate()
    new Thread {
      override def run(): Unit = {
        if (Try(Await.ready(system.whenTerminated, 10 seconds)).isFailure) {
          System.exit(-1)
        }
      }
    }.start()
  })

  private def getSeedNodes: List[Address] = {
    val nodes = if (hostAddress == "127.0.0.1") {
      List(Address("akka.tcp", system.name, hostAddress, clusterPort))
    } else {
      try {
        InetAddress.getAllByName("play-poc-discovery-service.default.svc.cluster.local")
          .map(a => Address("akka.tcp", system.name, a.getHostAddress, clusterPort)).toList
      } catch {
        case ex: Throwable =>
          Logger.error("Unable to connect to discovery service", ex)
          List(Address("akka.tcp", system.name, hostAddress, clusterPort))
      }
    }
    if (nodes.size > 1) nodes.filterNot(_.host.get == hostAddress) else nodes
  }

  def checkHealth = Action {
    implicit val writes = Json.writes[Address]
    val json = Json.obj(
      "hostAddress" -> hostAddress,
      "leader" -> cluster.state.leader,
      "seenBy" -> cluster.state.seenBy,
      "unreachable" -> cluster.state.unreachable.map(m => m.address)
    )
    Ok(json)
  }

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

}
