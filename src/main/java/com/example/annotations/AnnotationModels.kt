package com.example.annotations

import java.lang.IllegalArgumentException
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

class AnnotationModels {
}
//这是第①种方法，实现映射
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class FiledName(val name:String)
//这是第②种方法，添加注解策略
@Target(AnnotationTarget.CLASS)
annotation class MappingStrategy(val klass:KClass<out NameStrategy >)
//把一个name，从一个String类型，转换成另一个String类型
interface NameStrategy{
    fun mapTo(name:String):String
}
//下划线转驼峰。例如：html_url ->htmlUrl
object UnderLTransformHump:NameStrategy{
    override fun mapTo(name: String): String {
        /*fold是一个迭代元素进行累加的方法
        第一个参数是初始值，第二个参数是累加函数。
        累计函数里面中的acc是累加器，c是当前元素。*/
        return name.toCharArray().fold(StringBuilder()){
            acc, c ->
            when(acc.lastOrNull()){
                '_'  -> acc[acc.lastIndex]= c.uppercaseChar()
                else -> acc.append(c)
            }
            acc
        }.toString()
    }
}
//同理，驼峰转下划线
object HumpTransformUnderL:NameStrategy{
    override fun mapTo(name: String): String {
        return name.toCharArray().fold(StringBuilder()){
            acc, c ->
            when{
                c.isUpperCase() ->acc.append('_').append(c.lowercaseChar())
                else -> acc.append(c)
            }
            acc
        }.toString()
    }
}


data class  UserVO(val login:String,
                   @FiledName("avatar_url")
                   val avatarUrl:String

)


data class UserDTO(
    val id:Int,
    val login: String,
    val avatar_url: String,
    val url: String,
    val htmlURL: String
)


inline fun <reified T:Any>Map<String,Any?>.mapAs():T{
    return T::class.primaryConstructor!!.let{
        //拿到T类型中的所有属性
        it.parameters.map {paramenter->
            //检查属性的值是否在Map中存在，不存在返回空。如果有不可空类型又为空就报错
            paramenter to (this[paramenter.name]
                ?:(paramenter.findAnnotations<FiledName>().firstOrNull()?.name?.let (this::get))
                ?: T::class.findAnnotation<MappingStrategy>()?.klass?.objectInstance?.mapTo(paramenter.name!!)
                ?:if (paramenter.type.isMarkedNullable) null
            else throw  IllegalArgumentException("${paramenter.name}is required but missing.")       )
        }.toMap()
            .let (it::callBy)
    }
}
inline  fun <reified T:Any,reified To:Any> T.mapAS():To{
    return T::class.memberProperties.map { it.name to it.get(this) }
        .toMap().mapAs()

}
fun main(){
    val userDTO = UserDTO(
        0,
        "Bennyhuo",
        "https://avatars2.githubusercontent.com/u/30511713?v=4",
        "https://api.github.com/users/bennyhuo",
        "https://github.com/bennyhuo"

    )
    //隐式推导mapAS<UserDTO,UserVO>
    val userVO:UserVO = userDTO.mapAS()
    println(userVO)

    val userMap = mapOf(
        "id" to 0,
        "login" to "AidenAn",
        "avatarUrl" to "https://api.github.com/users/bennyhuo",
        "url" to "https://api.github.com/users/bennyhuo"
    )
    val userVoFromMap:UserVO= userMap.mapAs()
    println(userVoFromMap)


}