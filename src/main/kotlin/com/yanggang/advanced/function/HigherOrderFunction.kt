package com.yanggang.advanced.function

/* 함수형 프로그래밍

고차함수: 함수를 파라미터로 받거나 함수를 반환하는 함수

*/
fun main() {
    // Q. 파라미터에 함수를 어떻게 넣어줄 수 있을까?

    /*
    방법1. 람다식
    - { } 를 활용해 람다식을 만들 수 있다
    - 마지막 파라미터가 람다식인 경우, () 밖으로 뺄 수 있다
     */
    compute(7, 5) { a, b -> a + b }

    /*
    방법2, 익명함수
    - fun 키워드를 사용하지만, 이름이 없는 익명함수
    - return 과 함수 body 를 축약하지 않을 수 있다
    - 문맥 파악이 가능한 경우, 파라미터 타입도 생략 가능하다

    이런 람다식, 익명함수를 함숫값, 함수 리터럴 이라고 한다
    함수 리터럴: 소스코드의 고정된 값을 나타내는 표기법 (예: fun(a, b) = a + b)

    (용어 정리)
    함숫값, 함수 리터럴: 일반 함수와 달리 변수로 간주하거나 파라미터에 넣을 수 있는 함수
    람다: (일반적인 프로그래밍 용어) 이름이 없는 함수
    람다식: (코틀린 용어) 함숫값, 함수 리터럴을 표현하는 방법1
    익명함수: (코틀린 용어) 함숫값, 함수 리터럴을 표현하는 방법2
    */
    compute(7, 5, fun(a: Int, b: Int) = a + b)
    compute(7, 5, fun(a, b): Int {
        return a + b
    })
    compute(7, 5, fun(a, b) = a + b)


    /*
    람다식 vs 익명함수 차이점
    - 람다식은 반환타입을 적을 수 없다, 익명함수는 반환타입을 적을 수 있다
    - 람다식 안에서는 return 을 쓸 수 없다, 익명함수는 return 을 쓸 수 있다
    */
    iterate(listOf(1, 2, 3, 4, 5), fun(num) {
        if (num == 3) {
            return
        }
        println(num)
    })

    iterate(listOf(1, 2, 3, 4, 5)) { num ->
        if (num == 3) {
            // return 람다식에서 쓸 수 없음
            /*
            return 이라는 키워드는 가장 가까운 fun 키워드를 종료하는 기능
            (현재 return 을 가장 가까운 함수는 main 함수 -> 즉, 뒤에 추가 코드가 있을 수도 있는데 main 을 종료해버리면 안되기 때문)
            이런 return 비지역적 반환 (= non-local return 이라고 함)
            */
        }
        println(num)
    }

}

fun compute(num1: Int, num2: Int, op: (Int, Int) -> Int): Int {
    return op(num1, num2)
}

fun iterate(numbers: List<Int>, exec: (Int) -> Unit) {
    for (number in numbers) {
        exec(number)
    }
}
