package org.scalex
package model

import scala.util.{ Try, Success, Failure }

case class Project(
    name: ProjectId,
    version: Version,
    scaladocUrl: Option[String]) {

  def id: ProjectId = name + "_" + version.shows

  def fullName = name + " " + version.shows

  def versionMatch(v: Version) = (version, v) match {

    case (a, b) ⇒ a == b
  }

  def semVersion = version

  override def toString = id

  override def equals(other: Any) = other match {
    case p: Project ⇒ name == p.name && version.shows == p.version.shows
    case _          ⇒ false
  }

  lazy val tokenize: List[Token] = name :: version.shows :: Nil
}

object Project extends Function3[ProjectId, Version, Option[String], Project] {

  def apply(name: String, version: String, url: Option[String]): Try[Project] =
    Version.parse(version) match {
      case Right(v) => Success(Project(name, v, url))
      case Left(e)  => Failure(new InvalidProjectVersionException(e.getMessage))
    }
}
