/**
 * Copyright (C) 2011-2012 Typesafe Inc. <http://typesafe.com>
 */
package org.ekrich.config.impl

import org.ekrich.config.{ConfigOrigin, ConfigValueType}

import java.io.{ObjectStreamException, Serializable}
import java.{lang => jl}

@SerialVersionUID(2L)
final class ConfigBoolean(origin: ConfigOrigin, val value: jl.Boolean)
    extends AbstractConfigValue(origin)
    with Serializable {
  override def valueType: ConfigValueType = ConfigValueType.BOOLEAN

  override def unwrapped: jl.Boolean = value

  override def transformToString: String = value.toString()

  override def newCopy(origin: ConfigOrigin): AbstractConfigValue =
    new ConfigBoolean(origin, value)

  // serialization all goes through SerializedConfigValue (signature is critical)
  @throws[ObjectStreamException]
  private def writeReplace(): jl.Object = new SerializedConfigValue(this)
}
