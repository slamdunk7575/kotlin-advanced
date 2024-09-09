package com.yanggang.advanced.generic

fun main() {
    val cage = Cage()
    // cage.put(Carp("잉어킹"))

    // val carp: Carp = cage.getFirst()
    // 문제점: cage.getFirst() 로 Carp 타입으로 꺼내 오려고 하면 Type Missmatch 발생

    /*
    간단한 해결방법
    val carp: Carp = cage.getFirst() as Carp
    as 라는 키워드를 써서 Type Casting 을 할 수 있지만, 이 코드는 위험하다!
    */

    /*
    왜냐면? 컴파일시 오류가 나지 않지만, 런타임이 되어서야 cannot be cast to class.. 발생하기 때문에
    cage.put(GoldFish("금붕어"))
    val carp: Carp = cage.getFirst() as Carp
    */

    /*
    또 다른 해결방법
    Safe Type Casting 과 Elvis 연산자
    하지만, 이 방법도 예외가 발생한다
    실수로 GoldFish 를 cage 에 넣게 되면 런타임시에야 예외를 알 수 있다
    cage.put(GoldFish("금붕어"))
    val carp: Carp = cage.getFirst() as? Carp
        ?: throw IllegalArgumentException()
    */

    /*
    제네릭(Generic) 사용
    (즉, 같은 Cage 이지만 잉어만 넣을 수 있는 Cage 와 금붕어만 넣을 수 있는 Cage 를 구분하는것)
    - 제네릭을 사용한 덕분에, Type Casting 없이 바로 잉어를 가져올 수 있음
    */
    val carpCage = Cage2<Carp>()
    carpCage.put(Carp("잉어킹"))
    val carp: Carp = carpCage.getFirst()

    /*
    Fish(물고기) 와 GoldFish(금붕어) 는 상속 관계
    물고기 Cage 로 금붕어 Cage 들을 옮기려고 하면 Type Missmatch 발생
    val goldFishCage = Cage2<GoldFish>()
    val fishCage = Cage2<Fish>()
    fishCage.moveFrom(goldFishCage)
    */
}

class Cage {

    private val animals: MutableList<Animal> = mutableListOf()

    fun getFirst(): Animal {
        return animals.first()
    }

    fun put(animal: Animal) {
        animals.add(animal)
    }

    fun moveFrom(cage: Cage) {
        this.animals.addAll(cage.animals)
    }
}


class Cage2<T> {

    private val animals: MutableList<T> = mutableListOf()

    fun getFirst(): T {
        return animals.first()
    }

    fun put(animal: T) {
        animals.add(animal)
    }

    fun moveFrom(cage: Cage2<T>) {
        this.animals.addAll(cage.animals)
    }
}
