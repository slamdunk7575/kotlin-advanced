package com.yanggang.advanced.generic
/*
Cage4 는 소비만 하는 클래스 (클래스 자체를 반공변)

(정리)
코틀리에서는 변성을 주는 위치에 따라
1. 클래스 선언 지점에 변성을 주는 경우 (선언 지점 변성, declaration-site variance) -> Java 에 존재안함

코틀린의 in 선언지점 변경 예:
public interface Comparable<in T> {
    // 데이터를 받고만 있음
    public operator fun compareTo(other: T): Int
}

코틀린의 out 선언지점 변경 예:
-> 코틀린의 List 는 자바의 List 와 달리 불변 컬렉션이라 데이터를 커낼 수만 있다
public interface List<out E> : Collection<E> {

    override val size: Int
    override fun isEmpty(): Boolean
    override fun contains(element: @UnsafeVariance E): Boolean
    override fun iterator(): Iterator<E>

그런데, 생각해보면 contains() 또는 containsAll() 은 타입 파라미터 E를 받아야 한다?
-> 즉, 데이터를 생산만 or 소비만 하는 경우는 드물다 (여러 객체와 상호작용해야 하니까)
원래는 out 선언지점변성을 이용해 E 를 함수 파라미터에 쓸 수 없지만 @UnsafeVariance 를 이용해 함수 파라미터에 사용할 수 있음
(contains() 같은 경우, List 에 데이터를 실제 넣지는 않고 비교하기 때문에 에러 발생하지 않음)

2. 함수나 변수 지점에 변성을 주는 경우 (사용 지점 변성, use-site variance) -> Java 의 와일드 카드와 대응
*/
class Cage4<in T> {
    private val animals: MutableList<T> = mutableListOf()

    fun put(animal: T) {
        this.animals.add(animal)
    }

    fun putAll(animals: List<T>) {
        this.animals.addAll(animals)
    }
}
