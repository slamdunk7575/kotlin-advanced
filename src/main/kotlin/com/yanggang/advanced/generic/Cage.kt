package com.yanggang.advanced.generic

fun main() {
    val cage = Cage()
    // cage.put(Carp("잉어킹"))

    /*
    val carp: Carp = cage.getFirst()
    문제점: cage.getFirst() 로 Carp 타입으로 꺼내 오려고 하면 Type Missmatch 발생
    */

    /*
    간단한 해결방법
    val carp: Carp = cage.getFirst() as Carp
    as 라는 키워드를 써서 Type Casting 을 할 수 있지만, 이 코드는 위험하다!
    */

    /*
    Q. 왜? 컴파일시 오류가 나지 않지만, 런타임이 되어서야 cannot be cast to class.. 발생하기 때문에
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
    Fish(물고기) 와 GoldFish(금붕어) 는 상속 관계이다
    물고기 Cage 로 금붕어 Cage 를 옮기려고 하면 Type Missmatch 발생

    val goldFishCage = Cage2<GoldFish>()
    val fishCage = Cage2<Fish>()
    fishCage.moveFrom(goldFishCage)

    하지만 그냥 금붕어를 물고기 Cage 로 넣는 것은 가능하다!
    fishCage.put(GoldFish("금붕어"))

    즉, Fish - GoldFish 는 상속 관계로 상위 클래스(Fish) 대신 하위 클래스(GoldFish) 가 올 수 있지만
    -> Cage2<Fish> - Cage2<GoldFish> 는 아무 관계를 가지고 있지 않다 (= 무공변 or 불공변, in-variant)
    -> 제네릭 클래스의 <타입 파라미터> 가 상속 관계가 있더라도 제네릭 클래스까지 유지되지는 않는다

    Q. 왜 제네릭은 이런 관계를 선택한 것인가?
    -> Java 의 배열은 기본타입의 상속관계가 배열까지 유지된다 (= 공변, co-variant)
    예: Object[] - String[]

    문제는 아래 코드는 타입 안전하지 않은 위험한 코드이다!
    obj 는 사실 String[] 이기 때문에 int(1) 을 넣을 수 없다
    컴파일 시에는 에러가 나지 않고 런타임시에야 ArrayStoreException 예외가 발생함
    예:
    String[] strs = new String[]{"A", "B", "C"};
    Object[] objs = strs;
    objs[0] = 1;

    하지만, Java 에도 List는 제네릭을 사용하기 때문에 공변인 Array 와 다르게 무공변하다!
    아래 코드는 컴파일시 Type Missmatch 불가능하다
    List<Object> 와 List<String> 은 아무 관계가 아니기 때문에
    예:
    List<String> strs = List.of("A", "B", "C",);
    List<Object> objs = strs;

    Effective Java #28
    "배열보다는 리스트를 사용하라" 의 이유가 이것 때문임
    -> 배열보다 리스트가 타입 안전한 코드이고 타입 안전성을 유지하기 위해 제네릭은 무공변하게 만들 수 밖에 없었음

    (추가) Q. 그럼 코드를 동작하게 할 수는 없을까?
    -> 제네릭까지 상속관계가 유지될 수 있도록 해줘야함
    -> moveFrom() 함수를 호출할때, Cage2<Fish> 와 Cage2<GoldFish> 사이의 관계를 만들어주자
    */

    val fishCage = Cage2<Fish>()
    val goldFishCage = Cage2<GoldFish>()
    // fishCage.moveFrom(goldFishCage)
    goldFishCage.put(GoldFish("금붕어"))
    goldFishCage.moveTo(fishCage)
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

    /*
    타입 파라미터의 상속관계(Fish-GoldFish)가 제네릭까지 어어지게 하는것 -> 변성(공변, 반공변)을 주다
    out 을 변성(variance) 어노테이션 이라고함

    out 변성 어노테이션의 효과
    1. 타입 파리미터 상속관계가 제네릭까지 이어지게됨
    2. otherCage 즉, 함수 파라미터는 생산자(데이터를 꺼내는) 역할만 할 수 있다
    예:
        otherCage.getFirst() -> O
        otherCage.put(this.getFirst()) -> X

    Q. 왜 otherCage 는 생산자 역할만 할 수 있을까?
    otherCage 가 소비자(데이터를 넣는) 역할도 할 수 있다고 하면
    this.getFirst() 는 잉어 리턴 -> 잉어를 Cage2<GoldFish> 금붕어 케이지에 넣게되면 타입 안정성이 깨지게됨 (런타임 에러)
    */
    fun moveFrom(otherCage: Cage2<out T>) {
        /*otherCage.getFirst()
        otherCage.put(this.getFirst())*/
        this.animals.addAll(otherCage.animals)
    }

    /*
    반대의 경우는? (함수 파라미터에 내가 가진 데이터를 전부 넣어주는 경우)
    예:
    val goldFishCage = Cage2<GoldFish>()
    goldFishCage.put(GoldFish("금붕어"))
    goldFishCage.moveTo(fishCage)

    금붕어를 물고기 케이지로 옮기는 것은 문제가 없다 -> 그런데 Type Missmatch 에러 발생

    Q. 그런데 왜 Type Missmatch 에러가 발생?
    moveTo(otherCage: Cage2<GoldFish>) 함수에 Cage2<Fish> 를 넣고싶다는 것
    즉, 이번엔 반대로 Cage2<Fish> 가 Cage2<GoldFish> 의 하위타입이 되어야함
    -> 타입 파리미터 상속관계가 제네릭까지 이어지게 되는데 반대로 되어야함 (반공변, contra-variant)
    예:
    상위 클래스  Fish         Cage2<GlodFish>
                |               |
    하위 클래스 GoldFish      Cage2<Fish>

    in 이 붙은 otherCage(Cage2<GoldFish>) 는 소비자(데이터를 받는) 역할만 할 수만 있다

    (정리)
    out: (함수 파라미터 입장에서) 생산자, 공변 -> 타입 파라미터의 상속관계가 제네릭 클래스에서 유지되는것
    in: (함수 파라미터 입장에서) 소비자, 반공변 -> 타입 파라미터의 상속관계가 유지되지만 제네릭에서 반대로 되는것

    특정 변수(공변, 반공변)에도 변성을 줄 수 있다
    val cage: Cage2<out Fish> = Cage2<GoldFish>()
     */

    fun moveTo(otherCage: Cage2<in T>) {
        otherCage.animals.addAll(this.animals)
    }
}
