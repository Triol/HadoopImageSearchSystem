# HadoopImageSearchSystem

## 配置HBASE数据库
```
  create 'Images', 'Img_Info'
```
## 执行
  ### 入口程序位于src/java/entrance/Main.java
  
```
Usages1: java -jar <program> <SearchType> <inputFile(file)> <outputPath(dir)> <taskName>
<SearchType>: SearchPart SearchImage SearchForgery
<SearchType>: 0          1           2
<inputFile> : inputFile: must be a image file
<outputPath>: outputPath: must be a directory
Usages2: java -jar <program> UploadImage <Path(dir)>
```
