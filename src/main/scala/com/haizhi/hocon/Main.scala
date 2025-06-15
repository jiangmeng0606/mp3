package com.haizhi.hocon



object Main {

  def main(args: Array[String]): Unit = {
    if (args.length < 4) {
      // scalastyle:off println
      println("Usage: Update <input-path> <output-path> <key> <new-value>")
      System.exit(1)
    }

    val inputPath = args(0)
    val outputPath = args(1)
    val key = args(2)
    val value = args(3)

    Common.updateConfig(inputPath, outputPath, key, value)

  }

}
