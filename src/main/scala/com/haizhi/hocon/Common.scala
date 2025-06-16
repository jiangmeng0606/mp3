package com.haizhi.hocon

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import org.ekrich.config.{Config, ConfigException, ConfigFactory, ConfigParseOptions, ConfigRenderOptions, ConfigValueFactory}
import org.ekrich.config.impl.{ConfigImpl, FromMapMode, SimpleConfigOrigin}

import scala.util.Try

object Common {
  def updateConfigBatch(inputPath: String, outputPath: String, kvMap: Map[String, String]): Unit = {
    try {
      val content = new String(Files.readAllBytes(Paths.get(inputPath)), StandardCharsets.UTF_8)
      val parseOptions = ConfigParseOptions.defaults
      val conf = ConfigFactory.parseString(content, parseOptions)
      // 批量更新
      val newConf = kvMap.foldLeft(conf) { case (c, (key, value)) =>
        val formatValue: AnyRef = if (isNumeric(value)) Integer.valueOf(value) else value
        val newValue = if (conf.hasPath(key)) {
          ConfigImpl.fromAnyRef(formatValue, conf.getValue(key).origin, FromMapMode.KEYS_ARE_KEYS)
        } else {
          ConfigValueFactory.fromAnyRef(formatValue)
        }
        c.withValue(key, newValue)
      }

      val renderOptions = ConfigRenderOptions.defaults
        .setComments(true)
        .setFormatted(true)
        .setOriginComments(false)
        .setJson(false)

      val str = newConf.root.render(renderOptions)
      Files.write(Paths.get(outputPath), str.getBytes(StandardCharsets.UTF_8))
      // scalastyle:off println
      // 操作日志
      println(
        s"""
           |Update Operation:
           |-------------------
           |Input Path:  $inputPath
           |Modifications:
           |${kvMap.map { case (k, v) => s"  $k = $v" }.mkString("\n")}
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
