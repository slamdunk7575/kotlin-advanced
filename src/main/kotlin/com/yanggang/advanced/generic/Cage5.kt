package com.yanggang.advanced.generic

/*
제네릭 제약(Generic Constraints)
-> 타입 파라미터 T 에 Animal 과 Animal 의 하위 타입만 들어오도록 하는 것
예:
class Cage5<T : Animal>(

Q. 만약 제한 조건을 여러개 두고 싶다면?
예: T 에 Animal 만 들어올 수 있고 Comparable 을 구현하고 있어야함
class Cage5<T>(
  private val animals: MutableList<T> = mutableListOf()
) where T : Animal, T : Comparable<T> {

이런 경우, Cage 클래스 안에 있는 동물들을 순서대로 정렬해서 출력해주는 함수를 만들 수 있음
예:
fun printAfterSorting() {
        return this.animals.sorted()
            .map { it.name }
            .let { println(it) }
}

제네릭 제약을 non-null 제약한정에 사용할 수 있음
예:
class Cage2<T : Any> {
이렇게 하면, null 타입이 들어오는걸 막아줌

sorted() 함수 역시 제네릭을 이용한 함수
public fun <T : Comparable<T>> Iterable<T>.sorted(): List<T> {
-> Iterable 의 확장함수 이고 타입 파라미터 T 를 받는데 Comparable 을 구현하고 있어야 한다
-> 클래스와 차이점은 fun (타입 파리미터) (수신객체타입 or 함수이름) 이런 형태로 되어있음


제네릭 함수를 이용하면 유연한 코딩이 가능하다
예: 두 리스트에 겹치는 원소가 하나라도 있는지 확인하는 함수

fun <T> List<T>.hasInterSection(other: List<T>): Boolean {
    return (this.toSet() intersect other.toSet()).isNotEmpty()
}

만약, 제네릭을 사용하지 않는다면 String 타입, Int 타입에 대해 각각 함수를 만들어야함
fun List<String>.hasInterSection(other: List<String>): Boolean {
...

fun List<Int>.hasInterSection(other: List<Int>): Boolean {
...

-> 제네릭 함수는 확장함수 뿐만 아니라 일반함수 에도 적용가능하다
*/

fun main() {
    // Cage5<Int>()
    // Cage5<String>()
    // Cage5<Carp>()

    val cage = Cage5(mutableListOf(Eagle(), Sparrow()))
    cage.printAfterSorting()

    // Cage2<GoldFish?>()
}

abstract class Bird(
    name: String,
    private val size:Int
) : Animal(name), Comparable<Bird> {

    override fun compareTo(other: Bird): Int {
        return this.size.compareTo(other.size)
    }
}

class Sparrow : Bird("참새", 100)
class Eagle : Bird("독수리", 500)


class Cage5<T>(
  private val animals: MutableList<T> = mutableListOf()
) where T : Animal, T : Comparable<T> {

    fun printAfterSorting() {
        return this.animals.sorted()
            .map { it.name }
            .let { println(it) }
    }

    fun getFirst(): T {
        return animals.first()
    }

    fun put(animal: T) {
        this.animals.add(animal)
    }

    fun moveFrom(otherCage: Cage5<T>) {
        this.animals.addAll(otherCage.animals)
    }

    fun moveTo(otherCage: Cage5<T>) {
        otherCage.animals.addAll(this.animals)
    }
}

fun <T> List<T>.hasInterSection(other: List<T>): Boolean {
    return (this.toSet() intersect other.toSet()).isNotEmpty() // intersect 는 중위함수
}
