package com.yanggang.advanced.generic

/*
Cage3 는 오직 생산만 하고 있다 (클래스 자체를 공변)
-> Cage3 로 부터 데이터를 가져올 수만 있고 Cage3 는 내보내기만 함

예:
fishCage: 금붕어, 잉어
animalCage(fishCage): 금붕어, 잉어
-> fishCage 를 animalCage 에 넣는다면 문제없음 (겉보기엔 Cage3<Animal> 타입, 실제로는 Cage3<Fish> 타입)
-> animalCage(fishCage) 에서 데이터를 가져오면(생산) Animal 로 가져오기 때문에 문제없음

하지만, Cage3 가 데이터를 받는 것도(소비) 한다면
예:
animalCage(fishCage) 는 Cage3<Animal> 타입이기 때문에 참새를 넣을 수 있는데
실제로는 Cage3<Fish> 이기 때문에 런타임시 에러 발생!

따라서 생산만 하는 Cage3 는 클래스 자체를 공변하게 만들 수 있다
예: class Cage3<out T> {
-> Cage3<Animal> 은 Cage3<Fish> 의 상위 타입으로 간주된다
-> out 을 붙이면 생산만 가능하고 소비를 위한 T를 받을 수 없다 (위처럼 타입 안정성이 깨짐)
*/

fun main() {
    val fishCage = Cage3<Fish>()
    val animalCage: Cage3<Animal> = fishCage
}

class Cage3<out T> {

    private val animals: MutableList<T> = mutableListOf()

    fun getFirst(): T {
        return animals.first()
    }

    fun getAll(): List<T> {
        return this.animals
    }
}
