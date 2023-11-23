# coeasyes-spring-boot-starter
## 1.简介
coeasyes-spring-boot-starter是一个基于co.elastic.clients 8.X的二次封装，主要是为了简化es的使用，提供了一些常用的方法，让开发者更加专注于业务开发。
# 2.特性
- 能像mybatis一样简单使用封装好的方法，也可以使用原生的方法以及工具类中的方法进行操作。
# 3.使用
## 3.1 添加依赖
```xml
<dependency>
    <groupId>com.coeasyes</groupId>
    <artifactId>coeasyes-spring-boot-starter</artifactId>
    <version>0.9-BATE</version>
</dependency>
```
## 3.2 配置
```properties
spring.coeasyes.hosts= es地址(ip+端口号或者域名+端口号)
spring.coeasyes.username= es用户名
spring.coeasyes.password= es密码
spring.coeasyes.connectTimeout= 连接超时时间
spring.coeasyes.socketTimeout= socket超时时间
spring.coeasyes.connectionRequestTimeout= 连接请求超时时间
spring.coeasyes.enableSSL= 是否启用ssl (true/false)
spring.coeasyes.crtFile= 证书文件路径（如果启用enableSSL会读取此配置）
spring.coeasyes.enable= 是否启用(true/false)
```
## 3.3 注解说明
### 3.3.1 @EsIndex
- 作用：用于标记实体类对应的索引
### 3.3.2 @EsId
- 作用：用于标记实体类对应的主键
### 3.3.3 @EsField
- 作用：用于标记实体类对应的字段
- 属性：
    - fieldName：es中的字段名
    - fieldType：es中的字段查询方式默认精确查询
    - sortType：排序方式
### 3.4 实体类说明
- 对于写入数据的实体类，需要继承EsBaseData,参考以下代码
```java
@Data
@EsIndex(indexName = "my_index_name")
public class MyTestEntity extends EsBaseData implements Serializable {

    private static final long serialVersionUID = -7811580976504699680L;

    /**
     * 主键id
     */
    @EsId
    private String id;
    
    //....
}
```
- 对于查询数据的实体类，需要继承EsBaseDto,参考以下代码
```java
@Data
@EsIndex(indexName = "my_index_name")
public class MyTestDto extends EsBaseDto implements Serializable {

    private static final long serialVersionUID = 1438771901038831457L;

    /**
     * 主键id
     */
    @EsId
    @EsFieldCondition(fieldName = "id", fieldType = FieldTypeEnum.PRECISE_QUERY)
    private String id;
    /**
     * 名称
     */
    @EsFieldCondition(fieldName = "name", fieldType = FieldTypeEnum.VAGUE_QUERY)
    private String name;
    /**
     * 时间
     */
    @EsFieldCondition(fieldName = "createTime", fieldType = FieldTypeEnum.PRECISE_QUERY,sortType = SortTypeEnum.ORDER_DESC)
    private Date createTime;
}
```
- 对于接收es返回数据的实体类，需要继承EsBaseVo,参考以下代码
```java
@Data
public class EsSysHttppostLogVo extends EsBaseVo implements Serializable {
    private static final long serialVersionUID = 1259862573161927153L;
    //....
}
```
## 3.5 使用说明
- 通过继承EsBaseMapper接口，可以直接使用里面的方法，参考以下代码
```java
    /**
     * 列表查询
     * @param esBaseDto es基础dto
     * @param clazz es实体类
     * @param <T> es基础dto类型
     * @return 列表数据
     */
    <T extends EsBaseDto> List<?> select(T esBaseDto, Class<?> clazz);

    /**
     * 分页查询
     * @param esBaseDto es基础dto
     * @param clazz es实体类
     * @param <T> es基础dto类型
     * @return 分页数据
     */
    <T extends EsBaseDto> EsPage<?> selectPage(T esBaseDto, Class<?> clazz);

    /**
     * 新增数据
     * @param esBaseEntity es实体
     * @param <T> es实体类型
     * @return 是否成功
     */
    <T extends EsBaseData> boolean insert(T esBaseEntity);

    /**
     * 批量新增数据
     * @param esBaseEntityList
     * @param <T>
     * @return
     */
    <T extends EsBaseData> boolean insertBatch(List<T> esBaseEntityList);

    /**
     * 修改单条数据
     * @param esBaseEntity  es实体
     * @param clazz es实体类
     * @param <T> es实体类型
     * @return 是否成功
     */
    <T extends EsBaseData> boolean update(T esBaseEntity, Class<?> clazz);
```
## 3.6 jar包下载
- 下载链接
[0.9-Bate](https://github.com/John117abc/coeasyes-spring-boot-starter/files/13448588/please-decompression.zip)


