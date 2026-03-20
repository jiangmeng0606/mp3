package com.haizhi.mp3

import java.io.File
import java.nio.file.{FileVisitOption, FileVisitResult, Files, Path, Paths, SimpleFileVisitor}
import java.nio.file.attribute.BasicFileAttributes
import java.util
import scala.jdk.CollectionConverters.CollectionHasAsScala
import com.mpatric.mp3agic.{ID3v1, ID3v2, ID3v23Tag, Mp3File}
import scala.io.Source
import scala.util.Using

/**
 * Utility functions for listing and processing MP3 files.
 * This refactor improves readability and adds comments but preserves original behavior.
 */
object Mp3Utils {
  private val imageTypes: Set[String] = Set("jpg", "jpeg", "png")

  /**
   * Recursively list all files under `folder`.
   */
  def listAllFiles(folder: File): List[File] = {
    val list = new util.ArrayList[File]
    try {
      val path = Paths.get(folder.getPath)
      Files.walkFileTree(
        path,
        util.EnumSet.noneOf(classOf[java.nio.file.FileVisitOption]),
        Integer.MAX_VALUE,
        new SimpleFileVisitor[Path]() {
          override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
            if (file.toFile.isFile) list.add(file.toFile)
            FileVisitResult.CONTINUE
          }
        }
      )
    } catch {
      case e: Exception =>
        System.err.println(s"文件扫描出错: ${e.getMessage}")
        e.printStackTrace()
    }
    list.asScala.toList
  }

  /**
   * Modify a single MP3 file: detect cover image and lyrics, update ID3 tags and save safely.
   */
  def modify(file: File): Unit = {
    try {
      if (!file.getName.endsWith(".mp3")) {
        println(s"文件 ${file.getName} 不是MP3文件")
        return
      }

      println(s"\n===== MP3文件信息: $file =====")
      val fileName = file.getName
      val title = fileName.replaceAll(".mp3", "")
      val author = file.getParentFile.getName
      val album = author

      // 查找并读取专辑封面
      val (imageType, imageData) = findAndReadAlbumImage(file.getParentFile.getAbsolutePath, album)
      println(s"找到的图片类型: ${imageType.getOrElse("无")}")
      println(s"图片数据长度: ${imageData.map(_.length).getOrElse(0)} 字节")

      // 查找并读取歌词文件
      val lyric = findAndReadLyric(file.getParentFile.getAbsolutePath, title)

      val mp3File = new Mp3File(file)

      if (mp3File.hasId3v1Tag) mp3File.removeId3v1Tag()
      val id3v24Tag: ID3v2 = if (mp3File.hasId3v2Tag) mp3File.getId3v2Tag else new ID3v23Tag
      setId3v2Tag(id3v24Tag, title, author, album, imageType, imageData, lyric)
      saveId3v2Tag(file, mp3File, id3v24Tag)

      println(s"\n=====保存MP3文件信息: $file 完成=====")
    } catch {
      case e: Exception =>
        println(s"处理MP3文件时出错: ${e.getMessage}")
        e.printStackTrace()
    }
  }

  /**
   * Try to find an album image file in the parentPath using albumName.
   */
  private def findAndReadAlbumImage(parentPath: String, albumName: String): (Option[String], Option[Array[Byte]]) = {
    imageTypes.find { imageType =>
      new File(parentPath, s"$albumName.$imageType").exists()
    } match {
      case Some(imageType) =>
        val imageFile = new File(parentPath, s"$albumName.$imageType")
        (Some(imageType), Some(Files.readAllBytes(imageFile.toPath)))
      case None =>
        (None, None)
    }
  }

  /**
   * Read lyric file if exists (songName.lrc in the same directory).
   */
  private def findAndReadLyric(parentPath: String, songName: String): Option[String] = {
    val lyricFile = new File(parentPath, s"$songName.lrc")
    if (lyricFile.exists()) {
      Using.resource(Source.fromFile(lyricFile)) { source =>
        Option(source.getLines().mkString("\n"))
      }
    } else {
      None
    }
  }

  /**
   * Save ID3v2 tag into file using a safe temporary file then rename.
   */
  private def saveId3v2Tag(file: File, mp3File: Mp3File, id3v2: ID3v2): Unit = {
    mp3File.setId3v2Tag(id3v2)
    val tempFilePath = s"${file.getAbsolutePath}_tmp"
    mp3File.save(tempFilePath)
    val tempFile = new File(tempFilePath)
    val rename = tempFile.renameTo(file)
    if (rename) {
      println(s"文件 ${file.getAbsolutePath} 已保存.")
    } else {
      println(s"文件 ${file.getAbsolutePath} 保存失败.")
    }
  }

  /**
   * Populate ID3v2 fields including album image and lyrics.
   */
  private def setId3v2Tag(id3v2: ID3v2, title: String, author: String, album: String,
                          imageType: Option[String], imageData: Option[Array[Byte]],
                          lyric: Option[String]): Unit = {
    id3v2.setPadding(false)
    id3v2.setTitle(title)
    id3v2.setArtist(author)
    id3v2.setAlbum(album)
    id3v2.setAlbumArtist(author)
    id3v2.setComposer(author)
    id3v2.setYear("2026")

    // 设置专辑封面
    (imageType, imageData) match {
      case (Some("jpg") | Some("jpeg"), Some(data)) =>
        id3v2.setAlbumImage(data, "image/jpeg")
      case (Some("png"), Some(data)) =>
        id3v2.setAlbumImage(data, "image/png")
      case (None, _) =>
        println("没有找到图片文件")
      case _ =>
        println("图片格式不支持")
    }

    // 设置歌词
    lyric.foreach { lyricsContent =>
      println(s"写入歌词: ${lyricsContent.length} 字符")
      id3v2.setLyrics(lyricsContent)
    }
  }

  /**
   * Show detailed information for a single MP3 file.
   */
  def show(file: File): Unit = {
    try {
      if (!file.getName.endsWith(".mp3")) {
        println(s"文件 ${file.getName} 不是MP3文件")
        return
      }
      println(s"\n===== MP3文件信息: ${file.getName} =====")
      val mp3File = new Mp3File(file)

      // 打印基本信息
      printBasicInfo(file, mp3File)

      // 打印标签信息
      if (mp3File.hasId3v2Tag) {
        println("=== ID3v2标签信息 ===")
        printID3v2Info(mp3File.getId3v2Tag)
      } else if (mp3File.hasId3v1Tag) {
        println("=== ID3v1标签信息 ===")
        printID3v1Info(mp3File.getId3v1Tag)
      } else {
        println("=== 无ID3标签，使用默认ID3v2.4标签 ===")
      }

      println(s"===== MP3文件信息: ${file.getName} =====\n")
    } catch {
      case e: Exception =>
        println(s"读取MP3文件时出错: ${e.getMessage}")
        e.printStackTrace()
    }
  }

  private def printBasicInfo(file: File, mp3File: Mp3File): Unit = {
    println(s"文件路径: ${file.getAbsolutePath}")
    println(s"文件大小: ${file.length()} 字节")
    println(s"时长: ${mp3File.getLengthInSeconds} 秒")
    println(s"比特率: ${mp3File.getBitrate} kbps")
    println(s"采样率: ${mp3File.getSampleRate} Hz")
    println(s"声道模式: ${mp3File.getChannelMode}")
    println(s"是否有ID3v1标签: ${mp3File.hasId3v1Tag}")
    println(s"是否有ID3v2标签: ${mp3File.hasId3v2Tag}")
    println(s"帧数: ${mp3File.getFrameCount}")
  }

  private def printID3v2Info(tag: ID3v2): Unit = {
    printTagField("标题", tag.getTitle)
    printTagField("艺术家", tag.getArtist)
    printTagField("专辑", tag.getAlbum)
    printTagField("年份", tag.getYear)
    printTagField("流派", tag.getGenreDescription)
    printTagField("音轨号", tag.getTrack)
    printTagField("作曲家", tag.getComposer)
    printTagField("出版商", tag.getPublisher)
    printTagField("原始艺术家", tag.getOriginalArtist)
    printTagField("专辑艺术家", tag.getAlbumArtist)
    printTagField("版权", tag.getCopyright)
    printTagField("URL", tag.getUrl)
    printTagField("评论", tag.getComment)

    // 如果有歌词信息
    Option(tag.getLyrics).foreach { lyrics =>
      println(s"歌词: ${lyrics.length} 字符")
    }

    // 专辑封面信息
    Option(tag.getAlbumImage).foreach { image =>
      println(s"专辑封面: ${image.length} 字节")
      println(s"封面MIME类型: ${Option(tag.getAlbumImageMimeType).getOrElse("未知")}")
    }
  }

  private def printTagField(fieldName: String, fieldValue: Any): Unit = {
    val value = Option(fieldValue).map(_.toString).getOrElse("无")
    println(s"$fieldName: $value")
  }

  private def printID3v1Info(tag: ID3v1): Unit = {
    printTagField("标题", tag.getTitle)
    printTagField("艺术家", tag.getArtist)
    printTagField("专辑", tag.getAlbum)
    printTagField("年份", tag.getYear)
    printTagField("流派", tag.getGenreDescription)
    printTagField("评论", tag.getComment)
    printTagField("音轨号", tag.getTrack)
  }

}
