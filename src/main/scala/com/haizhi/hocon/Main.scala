package com.haizhi.hocon

import java.io.File
import scala.util.{Try, Failure, Success}

object Main {

  private def printUsage(): Unit = {
    println(
      s"""Usage:
         |  hocon <input-file> <output-file> [key1=value1 key2=value2 ...]
         |
         |Example:
         |  hocon config.conf out.conf
         |
         |  hocon config.conf out.conf server.host=127.0.0.1 server.port=8081
         |
         |  hocon config.conf out.conf db.url=jdbc:mysql://localhost:3306/mydatabase?useSSL=false db.user=root db.password=password
         |Options:
         |  -h, --help    Show this help message
         |""".stripMargin)
  }

  def main(args: Array[String]): Unit = {
    if (args.isEmpty || args.contains("-h") || args.contains("--help")) {
      printUsage()
      sys.exit(0)
    }

    if (args.length < 2) {
      println(s"Error: Insufficient arguments.")
      printUsage()
      sys.exit(1)
    }

    val inputPath = args(0)
    val outputPath = args(1)
    val kvPairs = args.drop(2)

    // 校验输入文件
    val inputFile = new File(inputPath)
    if (!inputFile.exists() || !inputFile.isFile || !inputFile.canRead) {
      System.err.println(s"Error: Input file '$inputPath' does not exist or is not readable.")
      sys.exit(2)
    }

    // 校验输出目录
    val outputFile = new File(outputPath)
    val outputDir = outputFile.getParentFile
    if (outputDir != null && (!outputDir.exists() || !outputDir.isDirectory || !outputDir.canWrite)) {
      System.err.println(s"Error: Output directory '${outputDir.getPath}' does not exist or is not writable.")
      sys.exit(3)
    }

    // 解析 key=value 参数
    val kvMapTry = Try {
      kvPairs.map { kv =>
        val parts = kv.split("=", 2)
        if (parts.length != 2) throw new IllegalArgumentException(s"Invalid key=value pair: '$kv'")
        parts(0).trim -> parts(1).trim
      }.toMap
    }

    val kvMap = kvMapTry match {
      case Success(map) => map
      case Failure(ex) =>
        System.err.println(s"Error parsing key=value pairs: ${ex.getMessage}")
        sys.exit(4)
    }

    try {
      Common.updateConfigBatch(inputPath, outputPath, kvMap)
      println(s"Success: Updated config written to '$outputPath'")
    } catch {
      case ex: Exception =>
        System.err.println(s"Error processing config: ${ex.getMessage}")
        sys.exit(5)
    }
  }
}
