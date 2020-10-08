# HadoopImageSearchSystem

## 配置HBASE数据库
```
 hbase shell> create 'Images', 'Img_Info'
```
## 执行
  ### 入口程序位于/src/main/java/entrance/Main.java
  
```
Usages1: java -jar <program> <SearchType> <inputFile(file)> <outputPath(dir)> <taskName>
<SearchType>: SearchPart SearchImage SearchForgery
<SearchType>: 0          1           2
<inputFile> : inputFile: must be a image file
<outputPath>: outputPath: must be a directory
Usages2: java -jar <program> UploadImage <Path(dir)>
```
## 测试数据集
链接：https://pan.baidu.com/s/1q_VRJNFTSAWSqh4rQLH6vw 提取码：5pzy 


## 部分功能效果展示
### 篡改查询(SearchForgeryJob)
  #### 原图
   ![Alt](/result_show/task02-original-pic_ori.bmp)
  #### 篡改图
   ![Alt](/result_show/pic_forgery.bmp)
  #### 对比结果
  ##### task02.txt
  ##### 对比图
  ![Alt](/result_show/task02-compare-pic_ori.png)
