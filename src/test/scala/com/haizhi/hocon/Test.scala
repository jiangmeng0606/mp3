package com.haizhi.hocon

import com.haizhi.hocon.Common
import org.ekrich.config.impl.ConfigImplUtil.renderJsonString
import org.scalatest.matchers.should.Matchers._
import org.scalatest.funsuite.AnyFunSuite


class Test extends AnyFunSuite {


  test("Test updateConfigBatch") {
    val inputPath = "conf/application.conf"
    val outputPath = "conf/output.conf"
    val key = "server.port"
    val value = "newValue"

    Common.updateConfigBatch(inputPath, outputPath, Map(key -> value))
  }


  test("Test ConfigImplUtils") {
    println(!Character.isLetter('-'))
    println(!Character.isLetter('0'))
    println(!Character.isLetter('9'))
    println(!Character.isLetter('a'))
  }

}
