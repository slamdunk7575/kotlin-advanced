package com.yanggang.advanced.annotation

import kotlin.reflect.KClass


/*
@Target
-> 만든 어노테이션을 어디에 붙일지 선택할 수 있다

AnnotationTarget.CLASS : 클래스나 인터페이스에만 붙일 수 있다
(만약, @Target 을 명시하지 않으면 거의 대부분 요소에 붙일 수 있다)
*/
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
/*
@Retention
-> 만든 어노테이션이 저장되고 유지되는 방식을 제어

SOURCE : 어노테이션이 컴파일 때만 존재
BINARY : 어노테이션이 런타임 때 존재하고, 리플렉션을 쓸 수 없다
RUNTIME : 어노테이션이 런타임 때 존재하고, 리플렉션을 쓸 수 있다 (기본값)
*/
@Retention(AnnotationRetention.RUNTIME)
/*
어노테이션에서 필드를 받을 수 있게 생성자를 만들 수 있다

어노테이션 생성자에 들어갈 수 있는 필드
- Int 와 같은 기본 타입
- String, Enum 클래스
- 다른 어노테이션
- KClass
- 위의 가능한 타입들의 배열
- vararg (가변인자)

(참고)
KClass : 코드로 작성한 클래스의 정보를 가지고 있는 클래스

예:
class GoldFish(val name: String) {
    fun swim() {

    }
}

KClass<GoldFish> 는 코드로 작성한 GoldFish 클래스의 정보들
예:
- name 이라는 프로퍼티를 가지고 있다
- swim() 이라는 함수를 가지고 있다
등 의 정보를 가지고 있는 클래스이다
*/
annotation class Shape(
    val text: String,
    val number: Int,
    // val clazz: KClass<*>
)

annotation class Shape1
annotation class Shape2

@Repeatable
annotation class Shape3(
    val texts: Array<String>,
)

/*
어노테이션을 사용하는 방법
@Shape1
: 여러 어노테이션을 한번에 붙이고 싶은 경우 아래처럼 사용할 수 있음
*/
@[Shape1 Shape2]
class Annotation {
}

/*
어노테이션을 사용하는 방법
: name argument 를 사용하는 방법
*/
@Shape(number = 75, text = "하이")
class Hello {
}

/*
어노테이션을 사용하는 방법
: 단순히 필드를 대입해준 방법
*/
@Shape( "헬로우", 75)
class World {
}

/*
배열로 이루어진 어노테이션 필드는 [] 또는 arrayOf() 를 사용
*/
// @Shape3(texts = ["A", "B"])
// @Shape3(["A", "B"])
@Shape3(arrayOf("A", "B"))
class Annotation2 {
}

/*
어노테이션 사용하는 방법 (위치가 애매한 경우)

Q. 아래 @Shape1 은 어디에 적용된 것인가?
1. 생성자의 파라미터 name
2. name 이라는 프로퍼티 (field, getter, setter)
3. name 이라는 필드
4. name 의 getter

코틀린의 간결한 문법은 한 위치에 다양한 요소가 위치할 수 있도록 한다
-> 때문에 정확히 어떤 요소에 어노테이션을 붙였는지 알려주어야 한다

예: name 의 getter 에 어노테이션을 붙였다고 알려줌 -> 이런 문법을 'use-site target' 이라고 함

use-site target 종류는 다양하다 (주로 field, get, set 이 많이 사용됨)
class Annotation3(@get:Shape1 val name: String)

만약 use-site target 을 지정하지 않는다면 아래 순서로 우선 적용된다 (명시적으로 붙여주는게 좋다)
param(생성자의 파라미터) > property > field 순서

@Target(AnnotationTarget.FIELD)
annotation class Shape1
만약, 어노테이션이 Target 을 지정해주고 있다면 (예: Shape1 의 타깃을 FIELD 로 지정)
원래대로 라면 param 에 어노테이션이 붙지만, 이 경우 field 에만 어노테이션을 붙일 수 있으므로 field 에 붙게된다
*/
class Annotation3(@Shape1 val name: String)

/*
@Repeatable
한 요소에 어노테이션을 반복해서 붙이는 경우

JAVA 같은 경우, 여러 어노테이션을 한 요소에 붙이기 위해 어노테이션을 2개 만들어야 했음
예:
- JavaShape 어노테이션 1개 (JavaShape.java 코드 참고)
- JavaShapeContainer 어노테이션 1개 (JavaShapeContainer.java 코드 참고)
-> n개의 (@JavaShape) 어노테이션을 담고 있는 컨테이너를 만들고
@Repeatable 으로 컨테이너를 연결(두 어노테이션을 연결) 해줘야 했음


코틀린에서는 간단하게 어노테이션 위에 @Repeatable 을 붙여주면 된다

예:
@Repeatable
annotation class Shape3

*/
@Shape3(["C"])
@Shape3(["A", "B"])
class Annotation4 {
}

fun main() {
    // KClass 를 얻는 방법 (예: Annotation 클래스의 KClass)
    val clazz: KClass<Annotation> = Annotation::class
}
