package akka.stream.checkpoint.scaladsl

import akka.NotUsed
import akka.stream.checkpoint.{CheckpointBackend, CheckpointStage, SystemClock}
import akka.stream.scaladsl.{Flow, Source}

object Checkpoint {

  /**
    * Scala API
    * Creates a checkpoint Flow.
    *
    * @param name checkpoint identification
    * @param tags set of key, values to add to metrics
    * @param backend backend to store the checkpoint readings
    * @tparam T pass-through type of the elements that will flow through the checkpoint
    * @return a newly created checkpoint Flow
    */
  def apply[T](name: String, tags: Map[String, String] = Map.empty)(implicit backend: CheckpointBackend): Flow[T, T, NotUsed] =
    Flow.fromGraph(CheckpointStage[T](repository = backend.createRepository(name, tags), clock = SystemClock))
}

object Implicits {

  final implicit class FlowMetricsOps[In, Out, Mat](val flow: Flow[In, Out, Mat]) extends AnyVal {

    def checkpoint(name: String, tags: Map[String, String] = Map.empty)(implicit backend: CheckpointBackend): Flow[In, Out, Mat] =
      flow.via(CheckpointStage(repository = backend.createRepository(name, tags), clock = SystemClock))
  }

  final implicit class SourceMetricsOps[Out, Mat](val source: Source[Out, Mat]) extends AnyVal {

    def checkpoint(name: String, tags: Map[String, String] = Map.empty)(implicit backend: CheckpointBackend): Source[Out, Mat] =
      source.via(CheckpointStage(repository = backend.createRepository(name, tags), clock = SystemClock))
  }
}