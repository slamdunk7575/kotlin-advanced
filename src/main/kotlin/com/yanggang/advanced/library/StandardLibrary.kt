package com.yanggang.advanced.library

import java.lang.IllegalStateException
import kotlin.system.measureNanoTime


fun main() {
    /*
    repeat 함수
    - 무언가 반복해야 할때 유용
    - 어떤 행동을 할지 함수 블록, 람다를 넣어줌
    */
    repeat(3) {
        println("Hello Yanggang")
    }

    /*
    투두 함수
    - 무언가 해야할 일을 적어 놓을때 사용
    - 주석으로 표현할 수 있지만, 코틀린에서 표준 라이브러리 함수 제공
    public inline fun TODO(reason: String): Nothing
    */
    // TODO("main 함수 구현")

    /*
    measureTimeMillis
    - 가볍게 성능을 확인 해볼때 사용

    예:
    아래 코드는 System.currentTimeMillis() 를 두번 사용해야 하는 번거로움이 있음
    */
    val start = System.currentTimeMillis()
    val num1 = 1
    val num2 = 2
    val result = num1 + num2
    println("소요시간: ${System.currentTimeMillis() - start}  ms")

    /*
    measureNanoTime() 함수를 사용하면 아래처럼 소요시간을 구할 수 있음

    public inline fun measureNanoTime(block: () -> Unit): Long {
    -> 함수를 함수 파라미터로 넣을 수 있게 되면서 유용한 기능들을 함수로 많이 만들 수 있게됨
    */
    val measureTime = measureNanoTime {
        val num1 = 1
        val num2 = 2
        val result = num1 + num2
    }

    /*
    runCatching 함수
    - 예외처리를 간단히 구성할 수 있다
    - 성공 or 실패 한 경우 모두 Result 라는 객체를 반환한다
    - 예외를 Result 객체로 감싸서 넘기다 보니 구체적인 예외처리를 호출부에 넘길 수 있다

    예:
    caller - callee 관계에서 callee 에서 바로 예외를 던지면
    caller 는 다시 한번 catch 를 하거나,
    caller 의 요구사항(A exception 은 이렇게, b exception 은 이렇게 등)을 callee 에게 반영하도록 함

    반면, runCatching() 함수를 사용하면
    caller 는 Result 객체를 받아서 그 안에 exception 을 보고 caller 가 원하는대로 처리함
    -> 여러 종류의 caller 들이 있을 수 있고 각각 원하는 예외처리가 다를 수 있기 때문에 장점

    public inline fun <R> runCatching(block: () -> R): Result<R> {
        return try {
            Result.success(block())
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
    */

    val result2 = runCatching { 1 / 0 }
    /*
    예:
    Failure(java.lang.ArithmeticException: / by zero)

    Result 객체에 여러 프로퍼티와 함수가 있음
    - isSuccess
    - isFailure
    - T 를 가져오는데 실패한 경우 null 을 가져옴
    public inline fun getOrNull(): T? =
    - exception 을 가져오는데 실패한 경우 null 을 가져옴
    public fun exceptionOrNull(): Throwable? =

    - result 객체의 여러 함수들을 이용해서 체이닝을 할 수 있다
    result2.map {  }.getOrNull()?.let {  }
    */

    result2.map {  }.getOrNull()?.let {  }
    println(result2)
}

fun acceptOnlyTwo(num: Int) {
    /*
    require 함수
    - 파라미터를 검증할때 유용
    - IllergalArgumentException 을 던진다
    - 원하는 값을 조건식에 적어줘야함

    (아래 코드와 동일)
    */
    require(num == 2) { "2만 허용" }

    /*
    if (num != 2) {
        throw IllegalArgumentException("2만 허용")
    }
    */
}

class Person {
    val status = PersonStatus.PLAYING

    fun sleep() {
        /*
        check 함수
        - 상태 혹은 멤버 변수를 검증하기 유용하다
        - IllegalStateException 을 던진다

        (아래 코드와 동일)
        -> require(), check() 같은 inline 함수를 만들어서 유틸성 함수로 사용할 수 있다
        */
        check(this.status == PersonStatus.PLAYING) {
            "현재 PLAYING 상태가 아닙니다"
        }

        /*
        예:
        sleep() 함수는 현재 상태가 PersonStatus.PLAYING 일때만 호출 가능
        if (this.status != PersonStatus.PLAYING) {
            throw IllegalStateException("현재 PLAYING 상태가 아닙니다")
        }
        */
    }

    enum class PersonStatus {
        PLAYING, SLEEPING
    }
}
