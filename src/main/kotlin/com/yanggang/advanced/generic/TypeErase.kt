package com.yanggang.advanced.generic

fun main() {
    /*
    제네릭이라는 개념이 JDK 초기 버전부터 있던게 아님 (JDK 1.5 버전부터 추가)
    -> 제네릭이 없던 시절(1.1, 1.2 ..) 의 List 와 제네릭이 생긴 후의 List<String> 코드가
    서로 호환성을 유지해야 하는 문제가 발생

    호환성을 위해서 자바5 는 런타임시에 List<String> 도 타입정보를 제거하는 방법을 선택함
    이로 인해, 자바에서는 지금도 raw type 을 만들 수 있다 (권장되는 방식 아님)
    예: List list = List.of(1, 2, 3)

    코틀린은 언어 초기부터 제네릭이 고려되었기 때문에 raw type 객체를 만들 수 없다!
    하지만, 코틀린도 JVM 위에서 동작하기 때문에 런타임 때는 타입 정보가 사라짐
    이를 타입 소거(Type Erasure) 라고함
     */
    // val numbers: List = listOf(1, 2, 3)

    val num = 3
    num.toSuperString() // "Int: 3" 출력 기대

    val str = "ABC"
    str.toSuperString() // "String: ABC" 출력 기대
    println("${str::class.java.name}: $str") // 직접 작성하면 가능

    val numbers = listOf(1, 2.0f, 3.0)
    numbers.filterIsInstance<Float>() // [2.0f]
    /*
    inline 함수 + reified 키워드 사용한 함수 예:
    public inline fun <reified R> Iterable<*>.filterIsInstance(): List<@kotlin.internal.NoInfer R> {
        return filterIsInstanceTo(ArrayList<R>())
    }
     */
}

fun checkStringList(data: Any) {
    /*
    컴파일 에러: Cannot check for instance of erased type: List<String>
    data 가 List<String> 인지 확인하려고 하지만 런타임 때는 String 정보가 사라지기 때문에 List<String> 인지 알 수 없음
    */
    /*
    if (data is List<String>) {
    }
    */
}

fun checkList(data: Any) {
    /*
    star projection: 해당 타입 파라미터에 어떤 타입이 들어 있을지 모른다는 의미
    star projection 을 활용해서 최소한 List 인지는 확인할 수 있다
     */
    if (data is List<*>) {
        // 타입 정보는 모르지만 List 의 기능은 그대로 사용할 수 있음
        val element: Any? = data[0]
    }

    if (data is MutableList<*>) {
        // 하지만, 어떤 타입이 들어있는지 모르기 때문에 함부로 데이터를 넣을 수는 없도록 막음
        // data.add(3)
    }
}

fun checkMutableList(data: Collection<String>) {
    if (data is MutableList<String>) {
        data.add("hello world")
    }
}

fun <T> T.toSuperString() {
    // 제네릭 함수는 타입 정보 T 가 사라지기 때문에 클래스 정보를 가져올때 에러 발생
    // println("${T::class.java.name}: $this")
}

/*
Q. 우리는 T 정보를 가져오고 싶을 때가 있다?
예: 주어진 리스트에 T 타입을 가진 원소가 하나라도 있는지 확인하는 확장함수
T 정보를 알 수 없다면, 타입별로 함수를 만들어야 함 -> 반복코드 발생
*/
fun List<*>.hasAnyInstanceOfString(): Boolean {
    return this.any { it is String }
}

fun List<*>.hasAnyInstanceOfInt(): Boolean {
    return this.any { it is Int }
}

/*
코틀린에서 reified 키워드 + inline 함수 사용하여 T 정보를 가져올 수 있음
inline 함수는 코드 본문을 호출 지점으로 이동시켜 컴파일 됨
따라서 함수가 아니기 때문에 T 자체가 본문에 옮겨 쓰여져서 제네릭의 타입 파라미터로 간주되지 않음

reified 키워드의 한계
-> reified 키워드가 붙은 타입 T를 이용해 T의 인스턴스를 만들거나
T의 companion object 를 가져올 수는 없다
*/
inline fun <reified T> List<*>.hasAnyInstanceOf(): Boolean {
    return this.any { it is T }
}


class TypeShadow<T : Animal> {
    fun <T : Animal> addAnimal(animal: T) {
    }
}
