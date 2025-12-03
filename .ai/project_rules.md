# 项目规则

- 这个项目里的代码是以Java8为基础的，接口的基本实现也是Java8，但是，这个项目是个多版本Java的项目。这个项目的某些接口，存在以
  `ImplByJxx`(xx为JDK版本号)结尾的实现类，这些实现类是针对不同JDK版本的实现。多个不同版本的java代码最终会编译成对应版本的class文件，
  并打包到同一个jar包里。运行时通过
  `space.sunqian.common.FsLoader`根据当前环境选择最高兼容版本的实现类加载；
- 生成代码时，要考虑到.editorconfig、.gitattributes、.gitignore等文件的配置；注意.idea这类IDEA自己的目录下的文件不要改；
- 这个项目非测试代码是0依赖的。有些类会依赖`asm`、`protobuf`等库，这些库会在运行时被尝试加载，环境中有就会正常加载，不是强制依赖的。
