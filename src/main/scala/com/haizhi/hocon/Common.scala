package com.haizhi.hocon

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import org.ekrich.config.{ConfigException, ConfigFactory, ConfigParseOptions, ConfigRenderOptions, ConfigValueFactory}
import org.ekrich.config.impl.{ConfigImpl, FromMapMode, SimpleConfigOrigin}

import scala.util.Try

object Common {
  def updateConfig(inputPath: String, outputPath: String, key: String, value: String): Unit = {
    try {
      val content = new String(Files.readAllBytes(Paths.get(inputPath)), StandardCharsets.UTF_8)
      val parseOptions = ConfigParseOptions.defaults
      val conf = ConfigFactory.parseString(content, parseOptions)
      // 修改参数，如果key存在，则更新，否则新增
      val formatValue: AnyRef = if (isNumeric(value)) Integer.valueOf(value) else value
      val newValue = if (conf.hasPath(key)) {
        ConfigImpl.fromAnyRef(formatValue, conf.getValue(key).origin, FromMapMode.KEYS_ARE_KEYS)
      } else {
        ConfigValueFactory.fromAnyRef(formatValue)
      }
      val newConf = conf.withValue(key, newValue)
      val renderOptions = ConfigRenderOptions.defaults
        .setComments(true)
        .setFormatted(true)
        .setOriginComments(false)
        .setJson(false)

      val str = newConf.root.render(renderOptions)
      Files.write(Paths.get(outputPath), str.getBytes(StandardCharsets.UTF_8))
      // scalastyle:off println
      println(
        s"""
           |Update Operation:
           |-------------------
           |Input Path:  $inputPath
           |Key:         $key
           |New Value:   $value
           |Output Path: $outputPath
           |-------------------
           |""".stripMargin
      )
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }

  def isNumeric(str: String): Boolean = Try(str.toInt).isSuccess

}
