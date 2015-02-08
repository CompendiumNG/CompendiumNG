package org.compendiumng.cngx.memory

import java.io.File
import java.util.UUID
import com.typesafe.scalalogging.StrictLogging
import org.apache.commons.lang.NotImplementedException
import com.typesafe.config._


object Session extends StrictLogging {
  private var AllSessions = Map[String, Session]()

  val Config = ConfigFactory.load("cngx.properties")
  val cfgVersion = Config.getNumber("version")

  assert(Config.isResolved, "Configuration is not resolved")

  /**
   * @param props properties of the session
   * @return established session
   */
  def create(props: Map[String, Any]): Session = {
    logger.info("Creating session with following properties: " + props)


    val session_id: String = props("id").asInstanceOf[String]

    val s: Session = new Session(props, session_id)

    if (s.connect()) {
      logger.info("Sucessfully connected to: " + s)
      AllSessions += session_id -> s
      s
    } else {
      logger.error("Failed to connect to: " + s.toString)
      null
    }
  }
}

class Session (updateProperties: Map[String, Any],  session_id: String) {
  val config_id: Any = updateProperties.getOrElse("id", UUID.randomUUID().toString)
  val properties = Map[String, Any]()

  def createElement(props: Map[String, Any]): Element = {
		def e = new Element(props)
		e.properties ++  props
    e
	}

	def connect(): Boolean = {
		throw new NotImplementedException()
		false
	}

	def deleteElement(element: Element, uuid: UUID, id: String): Unit = {
		throw new NotImplementedException()
	}


  def updateElementProperties(element: Element, props: Map[String,Any]): Element = {
    throw new NotImplementedException()
    null
  }

	def searchNodes(criteria: String): Seq[Element] = {
    throw new NotImplementedException()
    null
	}

	def getNode(rid: String):Element = {
    throw new NotImplementedException()
    null
	}

	def getNode(uuid: UUID): Element = {
    throw new NotImplementedException()
	}

	def createLink(NodeA: Element, nodeB: Element, props: Map[String,Any]): Element = {
    throw new NotImplementedException()
		null
	}

	def getOutLinks(element: Element): Seq[Element] =  {
    throw new NotImplementedException()
    null
	}

	def getInLinks(element: Element): Seq[Element] =  {
    throw new NotImplementedException()
    null
	}

	def getAllLinks(element: Element): Seq[Element] =  {
    throw new NotImplementedException()
    null
	}

	def link(nodeA: Element, nodeB: Element, ILink: Element): Seq[Element] = {
    throw new NotImplementedException()
    null
	}

	def unlinkInLinks(node: Element): Seq[Element] = {
    throw new NotImplementedException()
    null
	}

	def unlinkOutLinks(node: Element): Seq[Element] =  {
    throw new NotImplementedException()
    null
	}

	def unlinkAllLinks(element: Element): Seq[Element] =  {
    throw new NotImplementedException()
    null
	}

  /**
   *  add a file as document with properties and link it to an Element
   * @param file file on filesystem
   * @param name identifier of the file within Element (must be unique within Element)
   * @return uuid of stored and document
   */
  def addDoc(file: File, name: String, props: Map[String, Any]): Option[UUID] = {
    var uuid: Option[UUID] = ???
    if (file.exists() && file.canRead) {
      // read it
      val docelement = createElement(
        Map("label" -> name, "elementType" -> "document",
          "orig_file" -> file.getName, "orig_path" -> file.getAbsolutePath))
      docelement.save()
      docelement.properties("uuid")

    } else {
      return new Option[null]
    }

  }

}
