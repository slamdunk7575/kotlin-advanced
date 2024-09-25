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

    Sequence 동작 원리
    - 각 단계(filter, map)가 모든 원소에 적용되지 않을 수 있다
    - 각 원소에 대해 모든 연산을 수행하고 다음 원소로 넘어간다
    - 최종 연산이 나오기 전까지 계산 자체를 미리 하지 않는다 (= 지연연산)
    - 최종 연산이 없다면 연산을 수행조차 하지 않음

    1. 200만개 사과중 각 원소 하나마다 filter, map, take 검사 (세로 연산)
    -> Iterable 과 차이점: 전체 200만개중 사과를 찾음 (가로 연산)
    2. 10,000 개가 모이면 더이상 filter, map, 을 수항하지 않고 바로 평균 계산
    -> 만약 200만개중 40만개가 사과라면 처음 1만개 제외한 39만개는 검사 하지않음

    Q. 그럼 정말 Sequence 가 Iterable 보다 빠를까?

    JMH(Java Microbenchmark Harness): OpenJDK 에서 만든 성능 측정용 라이브러리
    벤치마트 결과 (200 만건 과일 평균 테스트)
    -> Iterable: 48초, Sequence: 0.7초 (60배 정도 차이)

    ./gradlew jmh 실행 -> build/results/results.txt 파일 확인

    Benchmark                    Mode  Cnt      Score   Error  Units
    SequenceTest.kotlinIterable  avgt       48020.922          us/op
    SequenceTest.kotlinSequence  avgt         766.816          us/op

    Q. 그렇다면 항상 Sequence 가 빠를까?
    -> NO! 데이터가 많지 않다면 Sequece 는 지연연산을 위한 약간 오버헤드가 있기 때문에
    오히려 Iterable 보다 성능이 안좋을 수 있다

    벤치마트 결과 (100건 과일 평균 테스트)
    -> 크게 차이가 나지 않음
    Benchmark                    Mode  Cnt  Score   Error  Units
    SequenceTest.kotlinIterable  avgt       0.876          us/op
    SequenceTest.kotlinSequence  avgt       0.673          us/op

    (정리)
    데이터 양이 많으면 Sequence 사용하는게 유리함
    데이터 양이 적다면 Iterable 사용하는게 조금 더 유리함

    (Sequence 주의할점)
    -> 연산 순서에 따라 큰 차이가 날 수 있다
    (= Sequece 는 각 데이터마다 연산을 수행하는데, 순서가 바뀌면 처리해야될 양이 바뀔 수 있음)
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
