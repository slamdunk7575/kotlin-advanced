package com.yanggang.advanced.dsl

import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.text.StringBuilder

/* 코틀린 DSL
Domain Specific Language
: HTML 이나 SQL 처럼 특정 목적을 위해 존재하는 언어

예: 코틀린을 이용해서 HTML 생성
fun result() =
    html {
        head {
            title {+"XML encoding with Kotlin"}
        }
        body {
            h1 {+"XML encoding with Kotlin"}
            p  {+"this format can be used as an alternative markup to XML"}

            ...
        }
    }

-> DSL 을 이용해서 객체 구조를 만들고 그 구조를 출력하는 방식

예: 아래 YAML 구조를 코틀린 DSL로 만들어보자
(YAML 형식)
version: '3'
services:
  db:
    image: mysql
    environment:
      - USER: myuser
      - PASSWORD: mypassword
    port:
      - "9999:3306"

(Kotlin DSL 변경후)
val yml = dockerCompose {
    version { 3 }
    service(name = "db") {
        image { "mysql" }
        env("USER" - "myuser")
        env("PASSWORD" - "mypassword")
        port(host = 9999, container = 3306)
    }
}


코틀린 DSL 로 표현했을때 장점
- 가독성이 좀 더 좋아진다 (예: 9999 가 host 포트인지? 3306 이 컨테이너 포트인지?)
- 오타를 줄일 수 있다
- 반복적인 코드를 줄일 수 있다


어떤 방식으로 만들어 지는지 이해해보자! (다양한 방식으로 나만의 DSL 을 만들 수 있다)
- 함수를 받는 함수
- 함수 파라미터를 받을때, 확장 함수를 사용하는 방법
- 연산자 오버로딩을 적절하게 사용하는 방법
- 위임 객체를 적절하게 사용하는 방법


(정리) Kotlin DSl 에서 자주 사용되는 특징
- 확장함수
예)
장황한 표현(JAVA): StringUtils.startWithA(str)
간결한 표헌(Kotlin): str.startWithA()

- 중위 함수 호출
예) 원래 점과 괄호를 꼭 쳐야하지만 매개변수가 하나인 함수 같은 경우에는
파라미터 사이에 함수 이름을 쓰는것으로 함수 호출
장황한 표현: "AB".concat("DE")
간결한 표헌: "FGH" concat "IJK"

- 람다식 활용
예) 람다식 같은 경우에 어떤 함수가 또 다른 함수를 매개변수로 받는데 그 매개변수가 가장 마지막일때
소괄호를 생략하고 람다식을 위치시킬 수 있다
장황한 표현: call({ it.id })
간결한 표현: call { it.id }

- 연산자 오버로딩
예) 다양한 연산자 오버로딩을 통해 어떤 기호들을 DSL 에 활용
장황한 표현: map.get("A")
간결한 표현: map["A"]

- 수신 객체 람다
예) 어떤 함수에 매개변수로 들어가는 람다식 안에서 또 다른 함수를 호출하고 싶을때, 대상 객체가 있어야 하지만
수신 객체 람다를 활용하면 함수 안에 this 라는 키워드로 해당 객체를 가리키게 되면서 this 를 생략하고 호출할 수 있다
장황한 표현:
val data = Data()
function {
    data.call()
}

간결한 표현:
function {
    call()
}
*/

fun main() {
    /*
    예:
    dockerCompose {

    }
    -> dockerCompose 는 함수이다

    예:
    dockerCompose() {

    }
    1) 어떤 함수의 마지막 파라미터로 또 다른 함수를 호출할때
    즉, 그 함수를 람다식으로 표현해서 파라미터로 넣을때 소괄호를 생략할 수 있다
    { } 안의 함수가 아래 init 이 되고, 이 { } 함수를 사용해서 dockerCompose 함수를 호출한다

    2) dockerCompose 함수가 받는 init 을 DockerCompose.() 확장함수로 만든다
    이렇게 되면 DockerCompose 내부에 접근하여 version 함수를 실행할 수 있다
    수신 객체를 가리키는 this 는 생략 가능하다
    예: this.version { 3 }

    version 함수는 Int 를 반환하는 함수(람다식)를 파라미터로 받는다
    예: { 3 }
    */
    val yml = dockerCompose {
        version { 3 }
        /*
        service 는 String 과 또 다른 함수를 파라미터를 받는다
        여기서, 또 다른 함수는 Service.() 의 확장함수 여야한다 -> 그래야 Service 의 image 를 설정
        */
        service(name = "db") {
            image {
                "mysql"
            }
            env("USER" - "myuser")
            env("PASSWORD" - "mypassword")
            port(host = 9999, container = 3306)
        }
    }

    println(yml.render("  "))

    /* (주의할점)
    가장 바깥쪽 블록 { } 안은 바로 안쪽 or 한번 더 들어간 안쪽 두가지 모두 가능하다
    즉, 어디서든 service 함수를 호출할 수 있다 -> 문접적으로 가능하지만, 계층적으로 이상하다!

    -> 이럴때 @DslMarker 사용하면, 가장 가까운 수신객체에 대해서만 this 를 생략할 수 있다
    따라서, 가장 안쪽 service 에서 가장 가까운 this 는 Service 객체이므로
    DockerCompose.service 를 호출할때 this 를 생략할 수 없게된다

     */
    val yml2 = dockerCompose {
        service(name = "db") {
            /* @DslMarker 사용하여 컴파일 에러
            사용하고 싶다면, this@dockerCompose.service(name = "web")
            이런식으로 명시해주면 service 를 쓸 수 있다

            service(name = "web") {
            }

            */
        }
    }
}

fun dockerCompose(init: DockerCompose.() -> Unit): DockerCompose {
    val dockerCompose = DockerCompose()
    /*
    init(dockerCompose)
    위처럼 호출가능 (확장함수를 Decompile 하면 자바의 static 함수로 변환되고 첫번째 파라미터로 수신객체를 받음)
    */
    dockerCompose.init()
    return dockerCompose
}

@YamlDsl
class DockerCompose {
    /*
    version 같은 경우 null 이 될 수 없으므로
    방법 1. lateinit 은 Primitive 타입에 쓸 수 없으므로 X

    방법 2. notNull 사용 -> 위임 프로퍼티로 만듦
    예: by Delegates.notNull()

    방법 3. 우리만의 위임 객체를 만들어 사용 (아래 설명)
    예: by onceNotNull()
    */
    private var version: Int by onceNotNull()
    private val services = mutableListOf<Service>()

    fun version(init: () -> Int) {
        version = init()
    }

    fun service(name: String, init: Service.() -> Unit) {
        val service = Service(name)
        service.init()
        services.add(service)
    }

    fun render(indent: String): String {
        val builder = StringBuilder()
        builder.appendNew("version: '$version'")
        builder.appendNew("services:")

        /*
        builder.appendNew(services.joinToString("\n") { it.render(indent) }
        이 코드가 아래처럼 출력하는 코드이다
        예)
        db:
          image: mysql

        뒤에 .addIndent(indent, 1)) 붙으면서 전체 indent 를 들여쓰기 해줌
        예)
          db:
            image: mysql
         */
        builder.appendNew(services.joinToString("\n") { it.render(indent) }.addIndent(indent, 1))
        return builder.toString()
    }
}

@YamlDsl
class Service(val name: String) {
    private var image: String by onceNotNull()
    private val environments = mutableListOf<Environment>()
    private val portRules = mutableListOf<PortRule>()

    fun image(init: () -> String) {
        image = init()
    }

    fun env(environment: Environment) {
        this.environments.add(environment)
    }

    fun port(host: Int, container: Int) {
        this.portRules.add(PortRule(host = host, container = container))
    }

    /*
    예:
    db:
      image: mysql
      environment:
        - USER: myuser
        - PASSWORD: mypassword
      port:
        - "9999:3306"
    */
    fun render(indent: String): String {
        val builder = StringBuilder()
        builder.appendNew("$name:")
        builder.appendNew("image: $image", indent, 1)

        builder.appendNew("environment:")
        environments.joinToString("\n") { "- ${it.key}: ${it.value}" }
            .addIndent(indent, 1)
            .also { builder.appendNew(it) }
        builder.appendNew("port:")
        portRules.joinToString("\n") { "- \"${it.host}:${it.container}\"" }
            .addIndent(indent, 1)
            .also { builder.appendNew(it) }

        return builder.toString()
    }
}

data class Environment(
    val key: String,
    val value: String,
)

/*
"문자열" - "문자열" 을 연산자 오버로딩 통해 Environment 객체 생성
예: "USER" - "myuser"
*/
operator fun String.minus(other: String): Environment {
    return Environment(
        key = this,
        value = other
    )
}

data class PortRule(
    val host: Int,
    val container: Int
)

/*
예:
val yml = dockerCompose {
        version { 3 }
        version { 4 }
}

위처럼 코드상 중복해서 쓰는 실수를 방지하기 위해
위임 객체를 활용해서 우리만의 위임 객체를 만들 수 있다
*/

fun <T> onceNotNull() = object : ReadWriteProperty<Any?, T> {
    private var value: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (this.value == null) {
            throw IllegalArgumentException("변수가 초기화되지 않았습니다.")
        }
        return this.value!!
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (this.value != null) {
            throw IllegalArgumentException("이 변수는 한번만 값을 초기화 할 수 있습니다.")
        }
        this.value = value
    }
}

/* 유틸성 함수
특정한 indent 를 times 만큼 반복해서 str 출력하는 함수
a:
 b:
  c:
   d: "ABC"
*/
fun StringBuilder.appendNew(str: String, indent: String = "", times: Int = 0) {
    (1..times).forEach { _ -> this.append(indent) }
    this.append(str)
    this.append("\n")
}

/* 유틸성 함수
여러줄의 문자열을 한번에 들여쓰기 해주는 함수
예:
    abc
    def
    ghi
    jkl
*/

fun String.addIndent(indent: String, times: Int = 0): String {
    /*
    회수(times) 만큼 문자열(indent)이 생기고 joinToString 통해 띄어쓰기 없이 이어줌
    [1, 2, 3], indent = "  "
    [1, 2, 3] -> ["  ", "  ", "  "] -> "      "
    */
    val allIndent = (1..times).joinToString("") { indent }
    return this.split("\n")
        .joinToString("\n") { "$allIndent$it" }
}

// @DslMarker 는 우리의 다른 어노케이션에 붙일 수 있음
@DslMarker
annotation class YamlDsl
