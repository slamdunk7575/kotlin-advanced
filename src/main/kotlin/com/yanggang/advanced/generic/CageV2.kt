package com.yanggang.advanced.generic

open class CageV1<T: Animal> {
    open fun addAnimal(animal: T) {

    }
}

/*
제네릭 클래스 상속 (방법1)
하위 클래스를 만들때도 타입 파라미터 T 를 사용 (타입 파라미터를 받아서 상위로 다시 올려줌)
이 경우 상위 클래스의 제약조건이 하위 클래스까지 전파됨
*/
class CageV2<T : Animal> : CageV1<T>() {
    override fun addAnimal(animal: T) {
        super.addAnimal(animal)
    }
}

/*
제네릭 클래스 상속 (방법2)
특정 타입으로 명시적으로 정해주는 방법

-> 방법1, 방법2 를 상황에 따라 적절히 사용
*/
class GoldFishCageV2 : CageV1<GoldFish>() {
    // 타입 파라미터를 예: GoldFish 라고 명시적으로 넣어줬기 때문에
    // addAnimal(animal: GoldFish) 함수 파라미터에도 GoldFish 타입이 명시적으로 들어감
    override fun addAnimal(animal: GoldFish) {
        super.addAnimal(animal)
    }
}
