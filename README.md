# 数据库与类属性的映射简化框架

在数据库中，字段名通常是带有 **下划线** 的，例如 `user_name`。  
而在代码中，通常使用 **驼峰命名** 的变量，例如 `userName`。  
使用该框架，可以自动完成这两者之间的映射转换，从而简化开发工作。

该框架支持两种映射方式：

1. **使用注解标注字段名称**
2. **使用注解策略标注整个类**

## 使用方式

### 1. 直接使用注解，标注名称

你可以通过在类的字段上添加注解来指定数据库中的字段名。这种方式适用于字段与数据库中的名称不一致的情况。

```kotlin
class User {

    @FieldName("user_name")
    var userName: String = ""

    @FieldName("email_address")
    var email: String = ""

}
```
### 2.  使用注解策略，标注类

如果整个类的命名方式遵循 驼峰转下划线 的规则，可以使用注解策略来标注类。这会自动将所有字段的命名进行转换，无需为每个字段单独添加注解。
```kotlin
@MappingStrategy(SnakeToCamelStrategy::class)
class User {

    var userName: String = ""
    var emailAddress: String = ""

}
```
