# Project Structure

## 一、文件简介

### 1. 界面Acivity:

MainAcivtiy.java（程序开启界面，展示中国色）
FoldersActivity.java（收藏夹名称界面）
FolderItemAcivity.java（收藏条目界面）

### 2. 数据库相关：

- DATABASEINFO.java：用于储存数据库名称等相关信息。
- Color.java：自定义的Color类，可以初始化不同的Color对象
- Colors.db：用于存放颜色信息，包括名字和RGB十六进制信息
  - 相关文件：ColorHelper.java, ColorProvider.java
- Folders.db：用于存放文件夹信息，只有一个列，为文件夹名字
  - 相关文件：FolderHelper.java, Folder.java
- UserFavor.db：用于存放用户收藏的颜色条目。该表需要经常插入和删除列（文件夹）
  - 相关文件：UserFavorHelper.java



Colors表：

| name(text) | value(text) | favorite(integer) |
| ---------- | ----------- | ----------------- |
| 玫瑰红     | ff973444    | 1                 |

Folders表：

| name(text) |
| ---------- |
| Folder1    |

Userfavor表

| name(text) | value(text) | 收藏夹1 (integer) | …(其他收藏夹) |
| ---------- | ----------- | ----------------- | ------------- |
| 玫瑰红     | ff973444    | 1                 |               |



### 3. 其他文件：

- Folder.java：用于静态储存从Folders.db中读取的文件夹名称，以及封装了使用dialog新建文件夹操作。
- Popuplist.java：继承了View的一个控件，用于长按删除条目。该控件为唯一从网上找来的文件。
- ColorAdapter：加载颜色gridview的适配器
- FolderItemAdapter：加载收藏夹内颜色的适配器
- FolderAdapter：加载文件夹标题列表的适配器

## 二、MainAcivity：中国色的展示与收藏

1. 界面使用文件：MainAciviti.java, acivity_main.xml, menu_main.xml,color_item.xml
2. 界面简介：
顶部有一个Actionbar，下面是固定当前选中的颜色名称、当前选中颜色的RGB值、是否添加到Favorite收藏夹小星星。再往下是展示色板的Gridview，点击不同颜色可以做到渐变更换整个界面的背景色和名称。
3. 显示颜色：
开始收集了不同的颜色名称和RGB值，存到了整形数组colorValue和字符串数组colorname，在使用两个数组初始化一个List＜Color＞ colorList。使用该List填入ColorAdapter适配器，再使用适配器初始化gridview。
4. 背景色和标题渐变：
		在程序开始时创建currentpos储存当前选中**颜色位置(currentcolorpos)**，默认值值为0。在gridviewitem的点击事件中，先使用自己事先封装好的crossfade函数进行背景色渐变，同时检测新的颜色有没有被收藏（若被收藏小星星变黄色），然后将当前的点击位置传给currentpos。 
	其中crossfade函数的主要功能是实现背景色、标题的渐变显示和十进制RGB值的改变。
