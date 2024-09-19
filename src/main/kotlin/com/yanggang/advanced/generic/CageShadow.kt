package com.yanggang.advanced.generic

fun main() {
    val cageShadow = CageShadow<GoldFish>()
    cageShadow.addAnimal(GoldFish("금붕어"))
    // cageShadow.addAnimal(Carp("잉어"))
}

/*
타입 파라미터 섀도잉: 클래스의 타입 파라미터를 함수의 타입 파라미터가 덮어쓴것

똑같은 T 이지만, 클래스에서 T 와 함수에서 T 가 다른 것으로 간주함
-> 때문에 위의 예시처럼 GoldFish 타입 CageShadow 이지만
Carp 를 넣을 수 있게 되는 오류가 발생함

타입 파라미터 섀도잉은 피해야 하고 만약 쓰고 싶다면, 함수 타입 파라미터에 다른 이름을 써서 겹치지 않도록 사용
 */
class CageShadow<T : Animal> {
    /*
    fun <T : Animal> addAnimal(animal: T) {
    }
    */
    fun addAnimal(animal: T) {
    }
}
