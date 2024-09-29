package com.yanggang.advanced.function

fun add3(a: Int, b: Int) = a + b

fun main() {

    /*
    변수에 함수를 할당하는 방법 -> 람다식, 익명함수

    Q. 만약 존재하는 함수에 대해 변수를 할당하고 싶다면?
    -> 함수 이름 앞에 콜론을 두개 붙임 (JAVA 와 비슷)
    이러한 기능을 호출 가능 참조(callable reference) 라고 부름
    */

    val add1 = { a:Int, b:Int -> a + b }

    val add2 = fun (a:Int, b:Int) = a + b

    val add3 = ::add3

    // 클래스 이름에 대한 호출 가능 참조를 만들면, 생성자에 대한 호출 가능 참조를 얻음
    val person3Constructor = ::Person3

    // 프로퍼티에 대한 호출 가능 참조도 얻을 수 있음
    val person3Name = Person3::name.getter

    /*
    인스턴스화된 클래스에 대한 호출 가능 참조를 얻을 수 있음
    -> 어떤 값에 바인딩 되어 있기 때문에 '바인딩된 호출 가능 참조' 라고 함
    */
    val p3 = Person3("yanggang", 100)
    val p3Nmae = p3::name.getter

    /*
    확장 함수의 호출 가능 참조를 얻을 수 있음
    메소드 or 생성자에 대한 호출 가능 참조 외에는 쓸일이 거의 없기 때문에 이런게 있구나 알아만 두자

    (정리)
    JAVA 에서는 호출 가능 참조 결과값이 Consumer, Supplier 같은 함수형 인터페이스 이지만
    Kotlin 에서는 리플렉션 객체이다
    */
    val plusOne = Int::addOne
}

fun Int.addOne(): Int {
    return this + 1
}

class Person3(
    val name: String,
    val age: Int
) {
}
