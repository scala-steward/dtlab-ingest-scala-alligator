package somind.dtlab.ingest

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import somind.dtlab.ingest.actors._
import somind.dtlab.ingest.observe.Observer

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.{Duration, FiniteDuration}

object Conf extends LazyLogging {

  implicit val system: ActorSystem = ActorSystem("DtLab-ingest-system")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val conf: Config = ConfigFactory.load()

  val healthToleranceSeconds: Int = conf.getString("main.healthToleranceSeconds").toInt

  def requestDuration: Duration = {
    val t = "120 seconds"
    Duration(t)
  }
  implicit def requestTimeout: Timeout = {
    val d = requestDuration
    FiniteDuration(d.length, d.unit)
  }

  val observer: ActorRef = system.actorOf(Props[Observer], "observer")

  val telemetryExtractor: ActorRef = system.actorOf(Props[TelemetryExtractorActor], "telemetry_extractor")
  val objectExtractor: ActorRef = system.actorOf(Props[ObjectExtractorActor], "object_extractor")

  val persistIdRoot: String = conf.getString("main.persistIdRoot")
  val snapshotInterval: Int = conf.getInt("main.snapshotInterval")

}
