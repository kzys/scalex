package org.scalex.model

import com.github.zafarkhaja.semver

class Version private[Version] (v: semver.Version) extends Ordered[Version] {
  private val javaVersion = v

  val major = javaVersion.getMajorVersion
  val minor = javaVersion.getMinorVersion
  val patch = javaVersion.getPatchVersion

  def compare(other: Version) = javaVersion.compareTo(other.javaVersion)

  override def toString = javaVersion.toString
  def shows = this.toString
}

object Version {
  def parse(s: String) = {
    try {
      val v = semver.Version.valueOf(s)
      Right(new Version(v))
    } catch {
      case e: semver.ParseException => Left(e)
    }
  }
}
