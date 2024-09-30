package com.yanggang.advanced.overloading

import java.time.LocalDate
import java.time.LocalDateTime

/* '연산자 오버로딩' 은 어려운 기능이 아니다
예: 위임 객체를 만들때, 사용했던 getValue, setValue 함수

- operator 라는 키워드가 fun 앞에 붙는다
- 함수 이름, 함수 파라미터가 정해져 있다
*/
class OperatorOverloading {
}

data class Point(
    val x: Int,
    val y: Int,
) {
    // 예: 원점을 기준으로 대칭 이동시킨 좌표를 구하는 함수
    fun zeroPointSymmetry(): Point = Point(-x, -y)

    // 연산자 오버로딩을 사용하면 좀 더 간결하게 만들 수 있다
    operator fun unaryMinus(): Point {
        return Point(-x, -y)
    }

    /*
    예: 좌표를 1씩 증가시키는 함수
    이런 오버로딩 메소드 이름은 코틀린 공식 문서에 정리되어 있다
    https://kotlinlang.org/docs/operator-overloading.html#augmented-assignments

    (정리) + 와 ++ 비교
    -> unaryPlus(+), unaryMinus(-) 는 다른 타입을 반환할 수 있지만
    inc(++), dec(--) 는 같은 타입만 반환할 수 있다 (++, -- 는 값 자체가 바껴야 하기 때문에)

    예: 아래 코드는 에러 발생
    operator fun: Int {
    }
    */
    operator fun inc(): Point {
        return Point(x + 1, y + 1)
    }
}

fun main() {
    var point = Point(20, -10)

    println(point.zeroPointSymmetry())
    println(-point)
    println(++point)

    // 예: 날짜에 3일을 더할때
    LocalDate.of(2024, 10, 1).plusDays(3)

    // 더하기 사칙연산 연산자 오버로딩을 활용
    LocalDate.of(2024, 10, 1) + Days(3)

    // 확정 프로퍼티를 사용하면 코드가 더 간결해짐
    LocalDate.of(2024, 10, 1) + 3.d


    /* 복합 대입 연산자는 조금 복잡함
    예: += 연산자를 사용한다면
    1. 복합 대입 연산자(plusAssign) 오버로딩이 있는지 확인
        1-1. 만약 오버로딩 있다면, 바로 적용
    2. 만약 오버로딩 없다면,
        2-1. 먼저 var 변수 인지 확인 -> 산술 연산자를 적용해 변수 업데이트
        2-2 val 변수라면 -> 변경할 수 없으니가 에러 발생
    */

    /*
    예: MutableCollection 은 plusAssign() 이라는 복합 대입 연산자가 구현되어 있음 -> 따라서 오버로딩 있다면, 바로 적용

    public inline operator fun <T> MutableCollection<in T>.plusAssign(element: T) {
    this.add(element)
    }
    */
    val list1 = mutableListOf("A", "B", "C")
    list1 += "D"

    /*
    예: 그냥 list 에는 복합 대입 연산자에 오버로딩이 없고
    산술 대입 연산자에만 오버로딩이 적용되어 있다 -> 새로운 리스트를 만들고 값을 더해서 반환 (불변 리스트니까 이렇게 동작)

    public operator fun <T> Collection<T>.plus(element: T): List<T> {
    val result = ArrayList<T>(size + 1)
    result.addAll(this)
    result.add(element)
    return result
    }

    결과적으로, plus 결과를 val 변수에 덮어 쓸 수 없기 때문에 에러 발생함
    */
    val list2 = listOf("A", "B", "C")
    // list2 += "D"

    /*
    List, Map 의 편의 기능도 get / set 연산자이다

    예: get 도 하나의 연산자
    public operator fun get(index: Int): E
    */
    list1[2]

    /*

    예: set 도 하나의 연산자
    public inline operator fun <K, V> MutableMap<K, V>.set(key: K, value: V): Unit {
    put(key, value)
    }
    */
    val map = mutableMapOf(1 to "A")
    map[2] = "B"
}

data class Days(val day: Long)

val Int.d: Days
    get() = Days(this.toLong())

operator fun LocalDate.plus(days: Days): LocalDate {
    return this.plusDays(days.day)
}


/* 함수 호출도 하나의 연산자이다
-> invoke 라는 함수 호출 연산을 오버로딩 했기 때문에 oper(Operator 타입) 자체가 함수콜이 된다

calcFun 을 노출하지 않고, Enum 자체를 함수처럼 사용한다
예: Operator.PLUS(1, 2)

-> enum 뿐만 아니라 어떤 클래스든 invoke 라는 함수 콜 연산을 오버로딩 하면 가능하다

(정리)
연산자 오버로딩은 그 의미에 맞게 사용하는 것이 좋다 (예: 날짜에 + 연산인데 - 된 날짜가 결과로 나오는 경우)
-> 이런 주의점도 Kotlin DSL 에서는 무시될 수 있음
*/
// fun calculate(num1: Int, num2: Int, oper:Operator) = oper.calcFun(num1, num2)
fun calculate(num1: Int, num2: Int, oper:Operator): Int = oper(num1, num2)

enum class Operator(
    private val open: Char,
    private val calcFun: (Int, Int) -> Int,
) {
    PLUS('+', { a, b -> a + b}),
    MINUS('-', { a, b -> a - b}),
    MULTIPLY('*', { a, b -> a * b}),
    DIVIDE('/', { a, b ->
        if (b == 0) {
            throw IllegalArgumentException("0 으로 나눌 수 없습니다.")
        } else {
            a / b
        }
    }),
    ;

    operator fun invoke(num1: Int, num2: Int): Int {
        return this.calcFun(num1, num2)
    }
}
