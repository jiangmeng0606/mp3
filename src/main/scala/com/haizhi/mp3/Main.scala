package com.haizhi.mp3

import org.json4s._

import java.io.File

import Mp3Utils._

object Main {
  implicit val format: DefaultFormats = DefaultFormats

  private def printUsage(): Unit = {
    println(
      s"""Usage:
         |  mp3 <command> <input-dir>
         |Commands:
         |  ls           List all mp3 files in a directory
         |  convert      Process all mp3 files and modify their metadata, cover art, and lyrics
         |Example:
         |  mp3 ls ~/Music/mp3
         |  mp3 convert ~/Music/mp3
         |
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
      println("Error: missing command or input directory")
      printUsage()
      sys.exit(1)
    }

    val command = args(0)
    val inputDir = args(1)
    command match {
      case "ls" =>
        processMp3Files(inputDir, showInfo = true)
      case "convert" =>
        processMp3Files(inputDir, showInfo = false)
      case _ =>
        println(s"Error: unknown command $command")
        printUsage()
        sys.exit(1)
    }
  }

  private def processMp3Files(inputDir: String, showInfo: Boolean): Unit = {
    val files = Mp3Utils.listAllFiles(new File(inputDir))
    val fileType = if (showInfo) "files" else "mp3 files"
    println(s"Found ${files.size} $fileType in $inputDir:")

    if (files.isEmpty) {
      println("No mp3 files found in the directory.")
    } else {
      println("Starting to process mp3 files...\n")
      val mp3Files = files.filter(_.getName.endsWith(".mp3"))
      mp3Files.foreach(f => if (showInfo) show(f) else modify(f))
      println("\n========== All mp3 files processed successfully! ==========")
    }
  }
}

