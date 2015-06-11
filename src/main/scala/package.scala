package org

import scalaz._

package object scalex
    extends instances
    with util.utilities
    with syntax.ToTypeClassOps // syntax associated with type classes
    with syntax.ToDataOps // syntax associated with Scalaz data structures
    with std.AllInstances // Type class instances for the standard library types
    with std.AllFunctions // Functions related to standard library types
    with syntax.std.ToAllStdOps // syntax associated with standard library types
    with IdInstances // Identity type and instances
    with contrib.std.TryInstances
    with scalaz.std.FutureInstances {

  type Token = String
  type Tokens = Set[Token]

  type ProjectName = String
  type ProjectId = String
}
