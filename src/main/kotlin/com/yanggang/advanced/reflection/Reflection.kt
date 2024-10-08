package com.yanggang.advanced.reflection

import kotlin.reflect.*
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.createType
import kotlin.reflect.full.hasAnnotation

@Target(AnnotationTarget.CLASS)
annotation class Executable

@Executable
class Reflection {
    fun a() {
        println("A 함수 입니다")
    }

    fun b(n: Int) {
        println("B 함수 입니다")
    }
}

/*
예:
obj 가 @Executable 어노테이션을 가지고 있다면,
obj 에서 파라미터가 없고 반환 타입이 Unit 인 함수를 모두 실행한다
-> a 함수가 실행되고 b 함수는 실행되면 안됨
*/
fun executeAll(obj: Any) {
    val kClass: KClass<out Any> = obj::class

    // kClass 에 Executable 어노테이션이 붙어 있는지 확인
    if (!kClass.hasAnnotation<Executable>()) {
        return
    }

    /*
    1. kClass 에서 멤버들을 가져오고 함수(KFunction)인 것만 필터링
    2. 함수의 반환 타입이 Unit 것만 가져온다
       예: returnType 은 KType 이기 때문에 Unit KClass 의 createType() 을 해서 똑같이 KType 으로 맞춤
    3. 파라미터가 없는 함수도 멤버함수인 경우 자기자신을 파라미터로 가지고 있기 때문에 (예: GoldFish2 클래스의 print() 함수)
       예: 함수의 파라미터가 1개이고 그 파라미터가 자신의 타입과 같은 경우를 가져옴
    */
    val callableFunctions: List<KFunction<*>> = kClass.members.filterIsInstance<KFunction<*>>()
        .filter { it.returnType == Unit::class.createType() }
        .filter { it.parameters.size == 1 && it.parameters[0].type == kClass.createType() }

    callableFunctions.forEach {
        /*
        함수 호출시 인스턴스를 넘겨줘야 하기 때문에 .call(kClass.createInstance()) 라고 넘겨주거나
        createInstance() 하면 기본 생성자를 찾기 때문에 obj 를 넘겨주는 것도 더 좋은 방법임
        -> Reflection 클래스의 주생성자에 매개변수가 없어 에러나지 않음

        예: 아래쪽 코드 참고
        goldFish2::class.members.first { it.name == "print" }.call(goldFish2)
        */
        // function -> function.call(kClass.createInstance())
        function -> function.call(obj)
    }
}


/*
리플렉션 객체(KClass)
-> 임의로 작성한 클래스의 정보를 나타내는 클래스

예: KClass<GoldFish>
- name 이라는 프로퍼티를 가지고 있다 (KProperty)
- swim 이라는 함수를 가지고 있다 (KFunction)

*/
class GoldFish(val name: String) {
    fun swim() {
    }
}

fun main() {

    // KClass 얻는 방법 1
    val kClass: KClass<Reflection> = Reflection::class

    // KClass 얻는 방법 2
    val ref = Reflection()
    val kClass2: KClass<out Reflection> = ref::class // KClass 이지만 공변을 만들어줘야함

    /*
    KClass 얻는 방법 3
    타입을 KClass<out Reflection> 으로 못하는 이유
    -> 실제 문자열이 Reflection 인지 모르기 때문에, 우선 Any 의 하위타입으로 가지고 온후에 적절하게 변형하여 사용
    */
    val kClass3: KClass<out Any> = Class.forName("com.yanggang.advanced.reflection.Reflection").kotlin

    kClass.java // Class<Reflection>
    kClass.java.kotlin // KClass<Reflection>
    /*
    위의 Class.forName("com.yanggang.advanced.reflection.Reflection") 이 자바 Reflection 의 기능이고
    .kotlin 으로 다시 KClass 로 변환한것

    Q. 그럼 Kotlin 리플렉션(KClass<T>)과 Java 리플렉션(Class<T>)의 차이는 무엇일까?
    - 코틀린도 JVM 위에서 동작한다
    : 즉, 우리는 적절한 곳에 kotlin 리플렉션 객체를 쓸 수도 있고 Java 리플렉션 객체를 쓸 수도 있다

    - 두 객체는 공통된 기능도 있지만 다른 기능도 있다
    예:
    Class.forName(..) 은 자바 클래스에 있는 기능이다
    코틀린에 isInner 라는 필드가 자바에는 없다
    */

    /*
    KClass 정리
    - simpleName: 클래스의 이름
    - qualifiedName: package 까지 추가된 클래스 이름
    - members: 클래스가 가지고 있는 멤버
        예: 자바에서는 getFields(), getDeclaredFields() 두가지로 나눠져 있지만
        코틀린은 members 를 통해 구분하지 않고 다 가져옴
        getFields() -> pubic 멤버만 가져옴
        getDeclaredFields() -> private 멤버도 가져옴
    - constructors: 클래스가 가지고 있는 생성자
    */

    executeAll(Reflection())


    /*
    KClass 알아두면 좋은 기능 (KType: 타입을 표현)
    예:
    어떤 객체가 null 이 들어갈 수 있는 타입인지 or 없는 타입인지
    그냥 타입인지 제네릭 클래스에 들어가는 타입 파라미터인지
    제네릭 클래스에 들어가는 타입 파라미터라면 변성 어노테이션이 붙어 있는지 등
    */
    val kType: KType = GoldFish2::class.createType()

    /*
    KClass 알아두면 좋은 기능 (KParameter)
    예:
    파라미터가 몇번째 인지, 파라미터의 이름은 무엇인지, 어떤 타입을 가지고 있는지 등
    */

    /*
    KClass 알아두면 좋은 기능 (KTypeParameter)
    예:
    제네릭 클래스에 들어가는 타입 파라미터 정보 나타냄
    */

    /*
    KClass 알아두면 좋은 기능 (KCallable)
    예:
    함수 호출을 구현한 -> public actual interface KFunction<out R> : KCallable<R>, Function<R> {
    프로퍼티 호출을 구현한 -> public actual interface KProperty<out V> : KCallable<V> {
    둘다 KCallable 을 구현하고 있음
    */

    val goldFish2 = GoldFish2("양갱")
    goldFish2::class.members.first { it.name == "print" }.call(goldFish2)

    /*
    print 함수는 아래와 같은 GoldFish2 의 멤버함수 이다
    예:
    fun GoldFish2.print() {
    println("금붕어 이름은 ${name} 입니다")
    }

    즉, 어떤 KClass 를 특정 인스턴스화된 객체로 부터 가져오더라도
    결국 그 인스턴스에서 KClass 를 가져오면(예: goldFish2::class) 특정 인스턴스에 대한 KClass 가 아니라
    그냥 GoldFish2 에 대한 KClass 라서 call(goldFish2) 처럼 넘겨주어야 함
    */


    /*
    KClass 알아두면 좋은 기능 (KAnnotatedElement)
    -> 이 interface 를 구현하고 있으면 어노테이션을 그 언어 요소에 붙일 수 있다는 의미

    KClass 알아두면 좋은 기능 (KClassifier)
    -> 어떠한 대상이 클래스인지 or 타입 파라미터 인지 알려주는 인터페이스
    */

    /*
    (참고)
    함수형 프로그래밍에서 reference 역시 Refelection 객체이다
    예:
    add 함수를 변수로 가져오기 위해 ::add 사용
    이게 KFunction2(KFunction 하위타입) 이고 즉, Refelection 객체임
    */
    val callable: KFunction2<Int, Int, Int> = ::add
}

/*
KClass 알아두면 좋은 기능 (cast 함수)
예: obj 를 GoldFish2 로 cast 한다
*/
class GoldFish2(val name: String) {
    fun print() {
        println("금붕어 이름은 ${name} 입니다")
    }
}

fun castToGoldFish2(obj: Any): GoldFish2 {
    // return obj as GoldFish2

    return GoldFish2::class.cast(obj)
}

fun add(a: Int, b: Int) = a + b
