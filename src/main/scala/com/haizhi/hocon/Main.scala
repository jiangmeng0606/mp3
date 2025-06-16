package com.haizhi.hocon



object Main {

  def main(args: Array[String]): Unit = {
    if (args.length < 3) {
      println(
        s"""Usage:
           |  hocon-tool <input-file> <output-file> key1=value1 [key2=value2 ...]
           |
           |Example:
           |  hocon-tool config.conf out.conf server.host=127.0.0.1 server.port=8081
           |""".stripMargin)
      sys.exit(1)
    }

    val inputPath = args(0)
    val outputPath = args(1)
    val kvPairs = args.drop(2)

    val kvMap = kvPairs.map { kv =>
      val Array(key, value) = kv.split("=", 2)
      key.trim -> value.trim
    }.toMap

    Common.updateConfigBatch(inputPath, outputPath, kvMap)
  }

}
