/**
 * Copyright (C) 2011-2012 Typesafe Inc. <http://typesafe.com>
 */
package org.ekrich.config.impl

import org.ekrich.config.{ConfigOrigin, ConfigValueType}

import java.io.{ObjectStreamException, Serializable}
import java.{lang => jl}

@SerialVersionUID(2L)
final class ConfigLong(
    origin: ConfigOrigin,
    val value: Long,
    originalText: String
) extends ConfigNumber(origin, originalText)
    with Serializable {
  override def valueType: ConfigValueType = ConfigValueType.NUMBER

  override def unwrapped: jl.Long = value

  override def transformToString: String = {
    val s = super.transformToString
    if (s == null) jl.Long.toString(value) else s
  }
  override def longValue: Long = value

  override def doubleValue: Double = value.toDouble

  override def newCopy(origin: ConfigOrigin): AbstractConfigValue =
    new ConfigLong(origin, value, originalText)

  // serialization all goes through SerializedConfigValue
  @throws[ObjectStreamException]
  private def writeReplace(): jl.Object = new SerializedConfigValue(this)
}
