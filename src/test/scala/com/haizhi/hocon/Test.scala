package com.haizhi.hocon

import org.ekrich.config.{ConfigFactory, ConfigParseOptions, ConfigRenderOptions}
import org.ekrich.config.impl.{ConfigImpl, FromMapMode}

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

object Test {
  def main(args: Array[String]): Unit = {
    val inputPath = "conf/application.conf"
    val outputPath = "conf/output.conf"
    val key = "server.port"
    val value = "newValue"

    Common.updateConfigBatch(inputPath, outputPath, Map(key -> value))
  }

}
