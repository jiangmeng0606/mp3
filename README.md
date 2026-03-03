# MP3 音乐文件处理工具

一个功能完整的 MP3 音乐文件批量处理工具，使用 Scala 语言开发。**主要作用是自动为 MP3 文件编辑 ID3 标签、添加专辑封面和嵌入歌词**，支持批量处理，是音乐库管理和元数据规范化的理想工具。

## 📋 功能特性

### 1. **MP3 文件列表查询** (`ls` 命令)
显示指定目录中的所有 MP3 文件及其详细音频信息：
- 文件路径、文件大小（字节）
- 时长（秒）、比特率（kbps）、采样率（Hz）
- 声道模式、帧数
- 检测并显示现有的 ID3 标签信息（ID3v1 或 ID3v2）
  - 标题、艺术家、专辑、年份、流派
  - 作曲家、出版商、原始艺术家、专辑艺术家、版权、评论
  - 歌词和专辑封面信息

### 2. **MP3 文件批量处理** (`convert` 命令)
自动为目录下的所有 MP3 文件修改元数据、添加封面和歌词：
- **递归扫描**：遍历指定目录及其子目录中的所有 MP3 文件
- **自动提取元数据**：
  - 从文件名提取歌曲标题
  - 从父文件夹名提取艺术家和专辑名
- **自动添加专辑封面**：
  - 自动查找匹配的图片文件（JPG、JPEG、PNG 格式）
  - 封面文件名规则：与 MP3 所在目录的上级目录名相同（如：`ArtistName.jpg`）
  - 位置：图片应放在 MP3 文件父目录的上级目录中
- **自动嵌入歌词**：
  - 自动查找匹配的 LRC 格式歌词文件
  - 歌词文件命名规则：与 MP3 文件名相同（如：`song1.lrc`）
  - 位置：歌词文件应与 MP3 文件在同一目录
- **ID3 标签更新**：
  - 移除旧的 ID3v1 标签
  - 创建或更新 ID3v2.3 标签
  - 自动设置年份为 2026
- **文件保存**：使用临时文件机制，确保原文件安全

### 3. **ID3 标签编辑** (ID3v1/v2)
支持对 MP3 文件的 ID3 标签进行完整读写操作：
- **基本信息**：标题、艺术家、专辑、年份
- **详细信息**：作曲家、出版商、原始艺术家、专辑艺术家、版权、评论等
- **音轨信息**：音轨号码和流派
- **多媒体支持**：URL 链接、歌词和专辑封面

### 4. **专辑封面管理**
- 自动检测和添加专辑封面图片
- 支持的格式：JPG、JPEG、PNG
- 按照文件夹名称自动匹配封面文件（如：`艺术家名.jpg`）
- 通过 ID3v2 标签嵌入到 MP3 文件中

### 5. **歌词管理**
- 自动检测和嵌入 LRC 格式歌词
- 按照 MP3 文件名自动匹配歌词文件（如：`歌曲名.lrc`）
- 将歌词写入 ID3v2 标签的 Lyrics 字段

## 📁 项目结构

```
mp3/
├── src/
│   ├── main/scala/com/haizhi/mp3/
│   │   ├── Main.scala              # 程序入口，命令行参数处理
│   │   └── Mp3Utils.scala          # MP3 处理核心工具类
│   └── test/scala/com/haizhi/mp3/
│       ├── Test.scala              # 单元测试
│       └── Ops.scala               # 操作测试
├── conf/
│   ├── application.conf            # 应用配置示例
│   └── output.conf                 # 配置输出示例
├── bin/
│   ├── mp3                         # Unix/Linux 执行脚本
│   └── assembly                    # Maven 编译脚本
├── pom.xml                         # Maven 项目配置文件
└── README.md                       # 项目文档
```

## 🛠 技术栈

- **语言**：Scala 2.13.9
- **运行环境**：Java 1.8+
- **构建工具**：Maven 3.x
- **核心依赖**：
  - `mp3agic` 0.9.1 - MP3 元数据处理库
  - `json4s-jackson` 3.6.7 - JSON 数据处理
  - `scalatest` 3.2.10 - 单元测试框架

## 🔨 编译

### 方式一：使用编译脚本
```bash
bin/assembly
```

### 方式二：使用 Maven
```bash
mvn clean package
```

编译完成后生成 `mp3-1.0-SNAPSHOT.jar` 文件。

## 🚀 使用方法

### 基本命令格式

```bash
java -jar mp3-1.0-SNAPSHOT.jar <command> <input-dir>
```

### 支持的命令

| 命令 | 说明 | 例子 |
|------|------|------|
| `ls` | 列出目录中的所有 MP3 文件及其详细信息 | `mp3 ls ~/Music/mp3` |
| `convert` | 自动修改目录中所有 MP3 文件的元数据、添加封面和歌词 | `mp3 convert ~/Music/mp3` |
| `-h, --help` | 显示帮助信息 | `mp3 --help` |

### 使用示例

#### 示例 1：列出指定目录的 MP3 文件及详细信息
```bash
bin/mp3 ls ~/Music/mp3
# 输出：Found X mp3 files in ~/Music/mp3:
# Starting to process mp3 files...
#
# ===== MP3文件信息: song1.mp3 =====
# 文件路径: /Users/username/Music/mp3/song1.mp3
# 文件大小: 5242880 字节
# 时长: 240 秒
# ...
```

#### 示例 2：批量修改 MP3 文件元数据、添加封面和歌词
```bash
# 需要 MP3 文件目录结构如下：
# ~/Music/mp3/
#   ├── ArtistName/
#   │   ├── song1.mp3
#   │   ├── song1.lrc              # 歌词文件（可选）
#   │   └── ArtistName.jpg         # 专辑封面（可选）
#   └── ArtistName2/
#       ├── song2.mp3
#       └── song2.lrc

bin/mp3 convert ~/Music/mp3
# 输出：Found X mp3 files in ~/Music/mp3:
# Starting to process mp3 files...
#
# ===== MP3文件信息: song1.mp3 =====
# 找到的图片类型: jpg
# 图片数据长度: XXXX 字节
# ...
# =====保存MP3文件信息: ... 完成=====
#
# ========== All mp3 files processed successfully! ==========
```

该命令会自动：
- 从文件名提取歌曲标题
- 从文件夹名提取艺术家和专辑名
- 查找并添加同名 .lrc 歌词文件（如存在）
- 查找并添加与父文件夹同名的图片文件作为专辑封面（如存在）
- 更新 MP3 文件的 ID3v2 标签，移除 ID3v1 标签
- 自动设置年份为 2026

#### 示例 3：显示帮助信息
```bash
bin/mp3 --help
# 输出：
# Usage:
#   mp3 <command> <input-dir>
# Commands:
#   ls           List all mp3 files in a directory
#   convert      Process all mp3 files and modify their metadata, cover art, and lyrics
# Example:
#   mp3 ls ~/Music/mp3
#   mp3 convert ~/Music/mp3
#
# Options:
#   -h, --help    Show this help message
```

## 📝 核心功能说明

### Main 程序 - 命令行入口

Main 对象是程序的主入口，负责处理命令行参数并分发到相应的处理逻辑。

**main 函数的工作流程：**

1. **参数验证**：
   - 检查是否提供了命令行参数
   - 支持 `-h` 或 `--help` 显示帮助信息

2. **参数解析**：
   - 第一个参数为命令（`ls` 或 `convert`）
   - 第二个参数为输入目录路径

3. **命令分发**：
   - **`ls` 命令**：
     - 调用 `listAllFiles()` 递归列出指定目录的所有文件
     - 对每个 `.mp3` 文件调用 `show()` 函数显示详细信息
     - 输出包括文件大小、时长、比特率、采样率、ID3 标签信息等
   
   - **`convert` 命令**：
     - 调用 `listAllFiles()` 递归列出指定目录的所有文件
     - 对每个 `.mp3` 文件调用 `modify()` 函数进行处理
     - 执行元数据修改、封面添加和歌词嵌入

4. **错误处理**：
   - 参数不足时显示错误提示和帮助信息
   - 未知命令时显示错误提示

### Mp3Utils 工具类

核心工具类包含以下主要方法：

| 方法 | 功能说明 | 输入 | 输出 |
|------|---------|------|------|
| `listAllFiles(folder: File)` | 递归列出指定文件夹中的所有文件 | 文件夹路径 | 文件列表 |
| `modify(file: File)` | 修改 MP3 文件的 ID3 标签、添加封面和歌词 | MP3 文件 | 修改后的 MP3 文件 |
| `show(file: File)` | 显示 MP3 文件的详细信息 | MP3 文件 | 控制台输出 |
| `setId3v2Tag(...)` | 设置 ID3v2 标签信息 | ID3v2标签对象及元数据 | 更新的ID3v2标签 |
| `printID3v2Info(tag)` | 打印 ID3v2 标签信息 | ID3v2 标签对象 | 控制台输出 |
| `printID3v1Info(tag)` | 打印 ID3v1 标签信息 | ID3v1 标签对象 | 控制台输出 |

#### listAllFiles() - 文件递归列表
```scala
def listAllFiles(folder: File): List[File]
```
- 使用 Java NIO 的 `Files.walkFileTree()` 方法递归遍历目录
- 返回指定目录及其子目录下的所有文件（包括非MP3文件）
- Main函数负责过滤出 `.mp3` 文件进行处理

#### modify() - MP3 文件处理
```scala
def modify(file: File): Unit
```

处理流程：
1. **文件格式验证**：确保是 `.mp3` 文件
2. **元数据提取**：
   - 从 MP3 文件名（去掉 .mp3 后缀）作为 **标题**
   - 从 MP3 所在的直接父文件夹名作为 **艺术家** 和 **专辑名**
3. **查找专辑封面**：
   - 遍历支持的图片格式（jpg、jpeg、png）
   - 在 MP3 文件父目录的上级目录中查找与 **艺术家/专辑名** 相同的图片文件
   - 示例：MP3 在 `/music/Artist1/song.mp3` → 查找 `/music/Artist1.jpg`
4. **查找歌词文件**：
   - 在 MP3 文件同一目录中查找 **同名 .lrc 文件**
   - 示例：MP3 为 `song.mp3` → 查找 `song.lrc`
   - 读取歌词内容作为字符串
5. **ID3 标签处理**：
   - 移除旧的 ID3v1 标签（如存在）
   - 创建或更新 ID3v2.3 标签
   - 调用 `setId3v2Tag()` 设置标签内容
6. **文件保存**：
   - 使用临时文件机制安全保存（添加 `_tmp` 后缀）
   - 临时文件保存后重命名覆盖原文件
   - 确保原文件在保存过程中不会被破坏

#### setId3v2Tag() - ID3v2 标签设置
```scala
def setId3v2Tag(id3v2: ID3v2, title: String, author: String, album: String, 
                imageType: Option[String], imageData: Option[Array[Byte]], 
                lyric: Option[String]): Unit
```

设置的标签字段：
- `Title`：歌曲标题（来自文件名）
- `Artist`：艺术家（来自文件夹名）
- `Album`：专辑（与艺术家相同）
- `AlbumArtist`：专辑艺术家（与艺术家相同）
- `Composer`：作曲家（与艺术家相同）
- `Year`：年份（固定为 2026）
- `AlbumImage`：专辑封面（如果找到）
  - 支持 JPEG：`image/jpeg`
  - 支持 PNG：`image/png`
- `Lyrics`：歌词（如果找到 .lrc 文件）

#### show() - MP3 信息查询
```scala
def show(file: File): Unit
```

显示的信息包括：
1. **音频信息**：
   - 文件路径、文件大小（字节）
   - 时长（秒）、比特率（kbps）、采样率（Hz）
   - 声道模式、帧数
   - ID3v1/v2 标签检测

2. **ID3 标签信息**（如果存在）：
   - 如果有 ID3v2 标签：显示 ID3v2 完整信息
   - 否则如果有 ID3v1 标签：显示 ID3v1 信息
   - 否则：显示 "无ID3标签，使用默认ID3v2.4标签" 提示

### 主要特性解析

#### 元数据自动提取
- **标题**：使用 MP3 文件名（去掉扩展名）
- **艺术家/专辑**：使用 MP3 所在的直接父文件夹名
- 这种设计假设 MP3 文件的目录结构为 `艺术家名/歌曲名.mp3`

#### 智能文件查找
- **专辑封面**：在上一级目录中按照 **相同的艺术家/专辑名** 查找
- **歌词文件**：在 **同一目录** 中按照 **相同的文件名** 查找
- 如果文件不存在，不报错，仅记录 "未找到"，继续处理

#### 安全的文件保存机制
- 先保存到 `文件名_tmp` 的临时文件
- 确认临时文件保存成功后，再重命名为原文件名
- 防止保存过程中原文件被破坏

## ⚙️ 配置文件

### application.conf 示例

应用配置文件在 `conf/` 目录下，包含各种应用配置示例。

## 📌 使用注意事项

1. **文件权限**：修改 MP3 文件时需要对文件有写权限
2. **文件备份**：修改操作会创建临时文件（添加 `_tmp` 后缀），请确保有足够的磁盘空间
3. **编码格式**：文件应使用 UTF-8 编码
4. **目录结构要求**：
   ```
   ~/Music/mp3/
   ├── ArtistName/
   │   ├── song1.mp3           # MP3 文件
   │   ├── song1.lrc           # 歌词文件（与 MP3 同名）
   ├── ArtistName.jpg          # 专辑封面（与父文件夹同名）
   ```

5. **专辑封面命名规范**：
   - 封面文件应与 MP3 所在文件夹的上级目录名相同
   - 支持格式：`ArtistName.jpg`, `ArtistName.jpeg`, `ArtistName.png`
   - 放置位置：MP3 文件父目录的上级目录中
   - 示例：MP3 文件在 `/Music/mp3/ArtistName/song.mp3`，封面应为 `/Music/mp3/ArtistName.jpg`

6. **歌词文件命名规范**：
   - 歌词文件应与 MP3 文件同名（不含扩展名）
   - 格式：`SongName.lrc`
   - 放置位置：与 MP3 文件同一文件夹
   - 示例：MP3 文件为 `song1.mp3`，歌词应为 `song1.lrc`

7. **ID3 标签处理**：
   - 自动移除 ID3v1 标签，使用 ID3v2.3 标签
   - 年份自动设置为 2026
   - 艺术家名作为专辑艺术家、作曲家等多个字段的值

## 🧪 运行测试

```bash
mvn test
```

## 📦 编译输出

成功编译后会生成以下文件：

- `mp3-1.0-SNAPSHOT.jar` - 包含所有依赖的可执行 JAR 文件

## 📄 文件说明

| 文件 | 说明 |
|------|------|
| `Main.scala` | 程序入口点，处理命令行参数和调用相应功能 |
| `Mp3Utils.scala` | 核心工具类，实现 MP3 文件的所有处理逻辑 |
| `Test.scala` | 单元测试文件 |
| `pom.xml` | Maven 项目配置，定义依赖和构建规则 |

## 🔄 工作流程示例

```
输入 MP3 文件目录
    ↓
listAllFiles() - 递归扫描所有文件
    ↓
对每个 MP3 文件调用 modify()
    ├─ 验证文件格式（.mp3）
    ├─ ��取元数据（标题、艺术家等）
    ├─ 查找专辑封面图片
    ├─ 查找歌词文件
    ├─ 创建/更新 ID3v2 标签
    └─ 保存修改
    ↓
完成处理
```

## 📊 输出信息示例

### 文件列表输出
```
Found 3 mp3 files in /Users/username/Music:
/Users/username/Music/artist1/song1.mp3
/Users/username/Music/artist1/song2.mp3
/Users/username/Music/artist2/song3.mp3
```

### MP3 信息查询输出
```
===== MP3文件信息: song.mp3 =====
文件路径: /path/to/song.mp3
文件大小: 5242880 字节
时长: 240 秒
比特率: 128 kbps
采样率: 44100 Hz
声道模式: Stereo
是否有ID3v1标签: false
是否有ID3v2标签: true
帧数: 9216

=== ID3v2标签信息 ===
标题: Song Title
艺术家: Artist Name
专辑: Album Name
年份: 2026
...
```

## 🎯 主要用途

1. **音乐库管理**：自动为音乐文件添加元数据
2. **元数据规范化**：统一格式、标签和信息
3. **批量处理**：快速处理大量 MP3 文件
4. **信息查询**：查看 MP3 文件的详细音频和标签信息

