package org.scalex
package document

import model.instances._

sealed trait Doc extends DocImpl

case class Template(
    member: Member,
    typeParams: List[model.TypeParam]) extends Doc {

  def declaration = "%s %s.%s%s".format(
    member.role.shows,
    member.parent.entity.shortQualifiedName,
    member.entity.name,
    typeParams.shows)
}

case class Def(
    member: Member,
    typeParams: List[model.TypeParam],
    valueParams: List[List[model.ValueParam]]) extends Doc {

  def declaration = "%s %s %s%s%s: %s".format(
    member.parent.signature,
    member.role.shows,
    member.entity.name,
    typeParams.shows,
    valueParams.shows,
    member.resultType)
}

case class Val(
    member: Member) extends Doc {

  def declaration = "%s %s %s: %s".format(
    member.parent.signature,
    member.role.shows,
    member.entity.name,
    member.resultType)
}
