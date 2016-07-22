package mesosphere.marathon.core.task

import akka.Done
import akka.actor.ActorSystem
import mesosphere.marathon.core.event.MesosStatusUpdateEvent
import mesosphere.marathon.core.task.Task.Id
import mesosphere.marathon.core.task.termination.TaskKillService

import scala.collection.mutable
import scala.concurrent.Future

/**
  * A Mocked TaskKillService that publishes a TASK_KILLED event for each given task and always works successfully
  */
class TaskKillServiceMock(system: ActorSystem) extends TaskKillService {

  var numKilled = 0
  val customStatusUpdates = mutable.Map.empty[Task.Id, MesosStatusUpdateEvent]
  val killed = mutable.Set.empty[Task.Id]

  override def killTasks(tasks: Iterable[Task]): Future[Done] = {
    tasks.foreach { task =>
      killTaskById(task.taskId)
    }
    Future.successful(Done)
  }
  override def killTaskById(taskId: Task.Id): Unit = {
    val appId = taskId.runSpecId
    val update = customStatusUpdates.getOrElse(taskId, MesosStatusUpdateEvent("", taskId, "TASK_KILLED", "", appId, "", None, Nil, "no-version"))
    system.eventStream.publish(update)
    numKilled += 1
    killed += taskId
  }

  override def kill(task: Task): Unit = killTaskById(task.taskId)

  override def killUnknownTask(taskId: Id): Unit = killTaskById(taskId)
}

