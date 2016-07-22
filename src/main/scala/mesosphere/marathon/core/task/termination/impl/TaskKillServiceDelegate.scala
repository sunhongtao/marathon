package mesosphere.marathon.core.task.termination.impl

import akka.Done
import akka.actor.ActorRef
import mesosphere.marathon.core.task.Task
import mesosphere.marathon.core.task.Task.Id
import mesosphere.marathon.core.task.termination.TaskKillService

import scala.concurrent.{ Future, Promise }

private[termination] class TaskKillServiceDelegate(actorRef: ActorRef) extends TaskKillService {
  import TaskKillServiceActor._

  override def killTasks(tasks: Iterable[Task]): Future[Done] = {
    val promise = Promise[Done]
    actorRef ! KillTasks(tasks, promise)
    promise.future
  }

  override def killTaskById(taskId: Task.Id): Unit = {
    actorRef ! KillTaskById(taskId)
  }

  override def kill(task: Task): Unit = {
    actorRef ! KillTask(task)
  }

  override def killUnknownTask(taskId: Id): Unit = {
    actorRef ! KillUnknownTaskById(taskId)
  }
}
