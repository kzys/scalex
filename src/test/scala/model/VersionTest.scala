import org.specs2.mutable._

import org.scalex.model.Version

class VersionSpec extends Specification {
    "parse" should {
      "return Version" in {
        val a = Version.parse("1.2.3").right.get
        a.major must_== 1
        a.minor must_== 2
        a.patch must_== 3
        a.shows must_== "1.2.3"

        val b = Version.parse("1.2.2").right.get
        a must be greaterThan(b)
      }

      "return Left when the parameter is not a version" in {
        Version.parse("hello").isLeft must_== true
      }
    }
}
