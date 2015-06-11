package org.scalex

package object search {

  type Score = Int
}

package search {

  import com.sksamuel.elastic4s.source.DocumentSource

  private[search] final class JsonSource(
    root: play.api.libs.json.JsObject
  ) extends DocumentSource {
    def json = root.toString
  }
}
