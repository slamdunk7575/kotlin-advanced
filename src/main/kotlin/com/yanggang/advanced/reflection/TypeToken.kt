package com.yanggang.advanced.reflection

import com.yanggang.advanced.generic.Animal
import com.yanggang.advanced.generic.Cage
import com.yanggang.advanced.generic.Carp
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.cast

/*
제네릭(Generic)을 사용하는 이유?

예: 제네릭을 사용하지 않는 Cage 에서 Animal 을 가져와 Carp 로 변경할때
Carp 대신 GoldFish 를 넣을 수 있는데, 컴파일 단에서 예외가 나지 않고
런타임에 가서야 예외가 발생

val cage = Cage()
cage.put(Carp("잉어킹"))
val carp: Carp = cage.getFirst() as Carp

때문에 제네릭(Generic) 을 사용했다
class Cage2<T : Any> {

    private val animals: MutableList<T> = mutableListOf()

    fun getFirst(): T {
        return animals.first()
    }

    ...

그런데 클래스 자체를 제네릭하게 만들지 않고도 타입 안전하게 동물을 가져올 수 있다
추가로 여러 타입의 동물을 하나의 Cage 에 함께 저장할 수 있다
-> '제네릭 함수' 와 '리플렉션 객체'를 활용하는 것
*/

fun main() {
    val cage = Cage()
    cage.put(Carp("잉어"))
    cage.getFirst() as Carp // 위험!

    /*
    클래스(예: TypeSafeCage)에 제네릭을 사용하지 않고도
    리플렉션 객체인 KClass 를 이용하여 여러 동물을 타입 안전하게 저장하고 가져오는 Cage 를 만들 수 있다

    예:
    typeSafeCage.putOne(Carp::class, GoldsFish("금붕어")) -> 컴파일 에서 예외발생

    TypeSafeCage 클래스의 type: KClass<T> 를 타입 토큰이라고 한다
    -> 토큰이란게 주고 받을때 인증서, key 로 사용되는 것과 같이
    TypeSafeCage 클래스에 데이터를 넣고 반환할때 타입 토큰을 줌으로써
    해당 타입의 데이터를 저장하고 반환한다는 의미
    */
    val typeSafeCage = TypeSafeCage()
    typeSafeCage.putOne(Carp::class, Carp("잉어"))

    /*
    여기서 inline 과 refine 키워드를 사용하면 코드를 깔끔하게 만들 수 있다

    이런 TypeSafeCage 같은 클래스를 '타입 안전 이종 컨테이너' 패턴이라고 부름
    Q. 타입 안전 이란?
    -> 이전처럼 위험한 형변환을 하지 않아도 된다
    Q. 이종 이란?
    -> 서로 다른 타입의 객체를 넣을 수 있다

    따라서, 서로 다른 타입의 객체를 안전하게 저장할 수 있는 패턴(= 타입 안전 이종 컨테이너 패턴)
    */
    val carp1: Carp = typeSafeCage.getOne()
    val carp2 = typeSafeCage.getOne<Carp>()

    /*
    (문제점)
    하지만 타입 안전 이종 컨테이너는 만능이 아니다!

    예:
    val cage3 = TypeSafeCage()
    cage3.putOne(listOf(GoldFish("금붕어1"), GoldFish("금붕어2")))
    val carps:List<Carp> = cage3.getOne()

    carps 에 잉어들이 들어있기를 예상하지만, 실제로 금붕어들이 들어있다
    (금붕어 List 를 저장하고 잉어 List 를 가져오려고 하지만, 잉어 대신 금붕어를 준다)

    이유는? 제네릭 타입은 소거되기 때문이다
    -> 런타임때 동작하는 코드에는 List<*> 형태로 등록하고 List<*> 형태로 가져오게 된다
    때문에 금붕어 List 대신 잉어 List 가 나오게 되는 문제가 발생한다!

    Q. 해결 방법이 있을까?
    -> 슈퍼 타입 토큰
    (핵심 아이디어)
    제네릭 타입 정보를 리플렉션으로 알아내자
    예: List<T> 를 저장하면, List 와 T(예: 금붕어) 타입을 기억하자
    */

    val superTypeToken1 = object : SuperTypeToken<List<GoldFish>>() {}
    val superTypeToken2 = object : SuperTypeToken<List<GoldFish>>() {}
    val superTypeToken3 = object : SuperTypeToken<List<Carp>>() {}
    println(superTypeToken2.equals(superTypeToken1)) // true
    println(superTypeToken3.equals(superTypeToken1)) // false


    val superTypeCage = SuperTypeSafeCage()
    superTypeCage.putOne(superTypeToken1, listOf<GoldFish>(GoldFish("금붕어1"), GoldFish("금붕어2")))
    /*
    예:
    val result = superTypeCage.getOne(superTypeToken3)

    java.lang.NullPointerException: null cannot be cast to non-null type T of com.yanggang.advanced.reflection.SuperTypeSafeCage.getOne
    -> 금붕어 List 를 넣고 잉어 List 는 넣지 않은 상태에서
    잉어 List 를 달라고 조회할때, null 을 반환하고 null 에 대해 as T 로 형변환 할때 nullPointer 예외가 발생한다

    (참고) 슈퍼 타입 토큰은 다양한 라이브러리 or 프레임워크 에서 사용된다
    예: Jackson, Spring

    Jackson 라이브러리에서 TypeReference 라는 슈퍼타입 토큰을 사용
    (내부에 Type 을 가지고 있고 저장하는 처리)
    -> -> 슈퍼타입 토큰 TypeReference 통해 다양한 T 로 데이터를 읽어 오도록 다양한 유틸성 함수를 제공하고 있음

    inline fun <reifine T> jacksonTypeRef(): TypeReference<T> = object: TypeReference<T>() {}

    public abstract class TypeReference<T> implements Comparable<TypeReference<T>>
    {
        protected final Type _type;
        ..
    */

    val result = superTypeCage.getOne(superTypeToken1)
    println(result)
}

class TypeSafeCage {

    private val animals: MutableMap<KClass<*>, Animal> = mutableMapOf()

    fun <T: Animal> getOne(type: KClass<T>): T {
        return type.cast(animals[type])
    }

    fun <T: Animal> putOne(type: KClass<T>, animal: Animal) {
        animals[type] = type.cast(animal)
    }

    /*
    reified 을 사용하면 타입 T 의 정보를 알고있기 때문에 함수 내부에서 사용할 수 있다
    */
    inline fun <reified T: Animal> getOne(): T {
        return this.getOne(T::class)
    }
}

abstract class SuperTypeToken<T> {
    /*
    SuperTypeToken 추상 클래스이기 때문에
    this::class 는 SuperTypeToken 을 상속 받은 클래스가 되고
    이 상속 받은 클래스의 슈퍼타입을 가져오게 되면 SuperTypeToken 을 반환
    여기서 .arguments[0] 로 타입 매개변수 T 를 가져오고 .type 으로 타입을 가져온다
    SuperTypeToken 을 제대로 상속했다면, 없을 수 없기 때문에 null 아님 단언 사용

    -> SuperTypeToken 을 구현한 클래스가 인스턴스화 되자마자
    T 변수를 내부 변수에 저장해버림
    예:
    T 에 List<Int> 를 넣으면 List<Int> 를 기억한다
    */
    val type: KType = this::class.supertypes[0].arguments[0].type!!

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        other as SuperTypeToken<*>
        return type == other.type
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }
}

class SuperTypeSafeCage {

    private val animals: MutableMap<SuperTypeToken<*>, Any> = mutableMapOf()

    fun <T: Any> getOne(token: SuperTypeToken<T>): T {
        return this.animals[token] as T
    }

    fun <T: Any> putOne(token: SuperTypeToken<T>, animal: T) {
        animals[token] = animal
    }
}
