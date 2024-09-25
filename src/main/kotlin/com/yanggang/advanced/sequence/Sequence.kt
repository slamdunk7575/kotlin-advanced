package com.yanggang.advanced.sequence

fun main() {
    val fruits = listOf(
        MyFruit("사과", 1000L),
        MyFruit("바나나", 3000L)
    )

    /* Iterable 원리
    -> 연산의 각 단계마다 중간 Collection 이 임시로 생성된다
    예:
    1. 200만개 과일중 사과를 골라 임시 List<Fruit> 생성
    2. 앞에서 만든 임시 List<Fruit> 에서 가격만 골라 List<Long> 생성
    3. 마지막 List<Long> 에서 10,000 개를 골라 평균 계산

    Q. 중간 Collection 을 만들지 않는 방법이 없을까?
    -> 코틀린은 대용량 데이터를 처리할때 중간 Collection 을 만들지 않는 Sequence 제공
    */
    val avg = fruits.asSequence()
        .filter { it.name == "사과" }
        .map { it.price }
        .take(10_000)
        .average()
}

data class MyFruit(
    val name: String,
    val price: Long
)
