package example

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@State(Scope.Benchmark) // 벤치마크에 사용되는 매개변수의 상태 지정 (예: fruits 변수를 각 벤치마크가 공유한다)
@BenchmarkMode(Mode.AverageTime) // 벤치마크 방식 (예: 평균시간 측정, 최소시간 측정, 처리량 측정 등)
@OutputTimeUnit(TimeUnit.MICROSECONDS) // 벤치마크 결과 표시 단위 (예: 초단위, 마이크로초단위 등)
open class SequenceTest {
    private val fruits = mutableListOf<Fruit>()

    // 각 벤치마크 함수(Iterable 사용, Sequence 사용) 전에 호출할 메소드
    @Setup
    fun init() {
        // 200 만개 과일 생성
        // (1..2_000_000).forEach { _ -> fruits.add(Fruit.random()) }
        (1..50).forEach { _ -> fruits.add(Fruit.random()) }
    }

    // 실제 벤치마크 대상 함수
    @Benchmark
    fun kotlinSequence() {
        val average = fruits.asSequence()
            .filter { it.name == "사과" }
            .map { it.price }
            .take(10_000)
            .average()
    }

    @Benchmark
    fun kotlinIterable() {
        val average = fruits
            .filter { it.name == "사과" }
            .map { it.price }
            .take(10_000)
            .average()
    }
}

data class Fruit(
    val name: String,
    val price: Long // 1000원 ~ 20000원 사이
) {
    companion object {
        private val NAME_CANDIDATES = listOf("사과", "바나나", "수박", "토마토", "오렌지")
        fun random(): Fruit {
            val randomNum1 = Random.nextInt(0, 5)
            val randomNum2 = Random.nextLong(0, 20001)

            return Fruit(
                name = NAME_CANDIDATES[randomNum1],
                price = randomNum2
            )
        }
    }
}
