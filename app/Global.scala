//import controllers.CustomRoutesService

import java.lang.reflect.Constructor
import play.api.db.DB
import securesocial.core.RuntimeEnvironment
import securesocial.core.providers.UsernamePasswordProvider
import service.{LoginUser, UserService}

import scala.collection.immutable.ListMap

//import service.{DemoUser, UserService}

import scala.slick.driver.H2Driver.simple._

import play.api.Application
import play.api.Play.current

object Global extends play.api.GlobalSettings {

  //  override def onStart(app: Application) {
  //
  //    lazy val database = Database.forDataSource(DB.getDataSource())
  //
  //  }
  //
  /**
   * An implementation that checks if the controller expects a RuntimeEnvironment and
   * passes the instance to it if required.
   *
   * This can be replaced by any DI framework to inject it differently.
   *
   * @param controllerClass
   * @tparam A
   * @return
   */
  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    val instance = controllerClass.getConstructors.find { c =>
      val params = c.getParameterTypes
      params.length == 1 && params(0) == classOf[RuntimeEnvironment[LoginUser]]
    }.map {
      _.asInstanceOf[Constructor[A]].newInstance(MyRuntimeEnvironment)
    }
    instance.getOrElse(super.getControllerInstance(controllerClass))
  }

  /**
   * The runtime environment for this sample app.
   */
  object MyRuntimeEnvironment extends RuntimeEnvironment.Default[LoginUser] {
    //    override lazy val routes = new CustomRoutesService()
    override lazy val userService: UserService = new UserService()
    //    override lazy val eventListeners = List(new MyEventListener())
    override lazy val providers = ListMap(
      include(
        new UsernamePasswordProvider[LoginUser](userService, avatarService, viewTemplates, passwordHashers)
      )
    )
    override implicit val executionContext = play.api.libs.concurrent.Execution.defaultContext
  }
}
