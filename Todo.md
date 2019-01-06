远程数据库：

- Folders表

  | name(text) |
  | ---------- |
  | Folder1    |
  | ...        |

- Userfavor表  
  | name(text) | value(text) | Folder1 (integer) | …(其他收藏夹) |
  | ---------- | ----------- | ----------------- | ------------- |
  | 玫瑰红     | ff973444    | 1                 |               |

实现：http请求？还是什么请求？我那本书里只讲了web服务器，网上也大部分是http的，自带的还有用[Socket](https://shoewann0402.github.io/2016/06/02/Android-%E7%BD%91%E7%BB%9C%E9%80%9A%E4%BF%A1%E4%B9%8B%E2%80%94%E2%80%94Socket/)通信的。

- 查询Folders的所有行
- 为Folders添加/删除行
- 在Userfavor表里查询条目，主要查询条件是where folder1 = 1类似的。这用于文件夹分类
- 为Userfavor添加/删除列
- 为Userfavor添加/删除/修改行
- [可选] 同时修改Folders的行和Userfavor的列名（用于文件夹重命名）

