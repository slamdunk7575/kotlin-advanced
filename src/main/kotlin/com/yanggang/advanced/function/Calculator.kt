package com.yanggang.advanced.function

/*
코틀린의 고차함수
-> 함수 타입 파라미터에 default parameter 적용할 수 있다
*/

fun calculate(num1: Int, num2: Int, oper: Char): Int {
    return when (oper) {
        '+' -> num1 + num2
        '-' -> num1 - num2
        '*' -> num1 * num2
        '/' -> {
            if (num2 == 0) {
                throw IllegalArgumentException("0 으로 나눌 수 없습니다.")
            } else {
                num1 / num2
            }
        }

        else -> throw IllegalArgumentException("들어올 수 없는 연산자(${oper} 입니다.)")
    }
}

/*
위 코드를 함수의 default parameter 를 이용한 좀 더 객체지향적인 코드
-> 연산자라는 개념을 enum 클래스로 만들고 연산자 안에 해당 연산식을 넣게 하고
함수식을 코틀린의 고차함수를 이용해서 default parameter 를 넣어주는 방식

JAVA 에서 비슷한 기능을 사용하려면 BiFunction 인터페이스를 써야했다 (자바에서는 함수가 1급 시민이 아님)
Kotlin 에서는 함수가 1급 시민이기 때문에 함수를 바로 사용할 수 있다
*/
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
}


class Calculator {
}
