package com.yanggang.advanced.di

import org.reflections.Reflections
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.cast

/*
DI(Dependency Injection) 컨테이너
- 클래스 정보를 관리한다
- 컨테이너에 등록된 클래스를 대신 인스턴스화 해준다
- 컨테이너에 등록된 클래스끼리 연결을 시켜준다

*/

/*
1단계
- 파라미터가 없는 생성자(=기본 생성자)를 가진 클래스를 컨테이너에 등록할 수 있다
- 등록한 클래스의 인스턴스를 컨테이너로부터 받아올 수 있다
*/

class Di

// 싱글턴으로 만들기 위해 object 키워드(클래스 정의와 동시에 객체를 생성) 사용
object DiContainerV1 {

    // 등록한 클래스 보관 = KClass 를 보관
    private val registeredClasses = mutableSetOf<KClass<*>>()

    fun register(clazz: KClass<*>) {
        registeredClasses.add(clazz)
    }

    fun <T: Any> getInstance(type: KClass<T>): T {
        return registeredClasses.firstOrNull { clazz -> clazz == type }
            /*
            clazz.constructors 는 KFunction<T> 타입이고
            그 상위 타입인 KCallable<R> 의 parameters 갯수를 가져와
            생성자의 파라미터 갯수를 체크하고 파라미터 갯수가 0개인 생성자를 찾을 수도 있음
            ?.let { clazz -> clazz.constructors.firstOrNull { it.parameters.isEmpty() } ?.call() as T }
            */
            ?.let { clazz -> clazz.constructors.first().call() as T }
            ?: throw IllegalArgumentException("해당 인스턴스 타입을 찾을 수 없습니다")
    }
}

fun start(clazz: KClass<*>) {
    // clazz.qualifiedName 을 통해 패키지를 가지고 옴 (예: com.yanggang.advanced.di.AService)
    val reflections = Reflections(clazz.packageName)
    val jClasses = reflections.getTypesAnnotatedWith(MyAutowired::class.java)
    // jClass 는 JAVA 리프렉션 객체라서 .kotlin 으로 KClass 로 변경
    jClasses.forEach { jClass -> DiContainerV2.register(jClass.kotlin) }
}

private val KClass<*>.packageName: String
    get() {
        val qualifiedName: String = this.qualifiedName
            // 클래스 이름이 없는 익명 클래스도 있기 때문에
            ?: throw IllegalArgumentException("익명 객체입니다")
        val hierarchy = qualifiedName.split(".") // 예: com yanggang advanced di AService
        // 클래스 이름만 제외하고 다시 . 으로 합쳐서 패키지 이름 가려옴 (예: com.yanggang.advanced.di)
        return hierarchy.subList(0, hierarchy.lastIndex).joinToString(".")
    }

/*
2단계
- 다른 클래스와 연결되어 있는 클래스를 컨테이너에 등록할 수 있다
- 연결되어 있는 클래스가 모두 등록되어 있다면, 인스턴스를 컨테이너로부터 받아올 수 있다
*/
object DiContainerV2 {

    private val registeredClasses = mutableSetOf<KClass<*>>()
    private val cachedInstances = mutableMapOf<KClass<*>, Any>()

    fun register(clazz: KClass<*>) {
        registeredClasses.add(clazz)
    }

    fun <T: Any> getInstance(type: KClass<T>): T {
        if (type in cachedInstances) {
            // cachedInstances[type] 는 뭐든지 들어갈 수 있는 Any 타입이니까
            // type.cast() 를 통해 우리가 반환하려는 T 타입을 cast 해준다
            return type.cast(cachedInstances[type])
        }

        val instance = registeredClasses.firstOrNull { clazz -> clazz == type }
            /*
            clazz.constructors 는 KFunction<T> 타입이고
            그 상위 타입인 KCallable<R> 의 parameters 갯수를 가져와
            생성자의 파라미터 갯수를 체크하고 파라미터 갯수가 0개인 생성자를 찾을 수도 있음
            ?.let { clazz -> clazz.constructors.firstOrNull { it.parameters.isEmpty() } ?.call() as T }
            */
            ?.let { clazz -> instantiate(clazz) as T }
            ?: throw IllegalArgumentException("해당 인스턴스 타입을 찾을 수 없습니다")
        cachedInstances[type] = instance
        return instance
    }

    private fun <T : Any> instantiate(clazz: KClass<T>): T {
        /*
        컨테이너에 등록된 클래스가 여러 생성자를 가질 수 있는데,
        사용 가능한 생성자(생성자의 파라미터가 모두 컨테이너에 등록된 생성자)를 찾아서 가져옴
        */
        val constructor = findUsableConstructor(clazz)
        /*
        그 생성자의 파라미터를 하나씩 인스턴스화 시켜서 배열로 묶어준 후에
        */
        val params = constructor.parameters
            .map { param -> getInstance(param.type.classifier as KClass<*>) }
            .toTypedArray()
        /*
        그 파라미터들을 생성자에 직접 넣어주어 인스턴스화 한다

        Q. 하지만 이렇게 하면 매번 새로운 객체를 생성하게 되므로 비효율적이다?
        -> 위에 cachedInstances 통해 한번만 생성하도록 처리 (싱글톤)
        */

        return constructor.call(*params)
    }

    /*
    clazz 의 constructor 들 중, 사용할 수 있는 constructor 를 찾음
    Q. 사용할 수 있는 constructor 란?
    -> constructor 에 넣어야 하는 타입들이 모두 컨테이너에서 관리하는 경우 (= 모두 컨테이너에 등록된 경우)
    */
    private fun <T : Any> findUsableConstructor(clazz: KClass<T>): KFunction<T> {
        // clazz 의 생성자를 모두 불러와서
        // 그 생성자의 파라미터 타입들이 컨테이너가 가지고 있는 타입들에 모두 포함되는 경우
        return clazz.constructors.firstOrNull {
            constructor -> constructor.parameters.isAllRegistered }
            ?: throw IllegalArgumentException("사용할 수 있는 생성자가 없습니다")
    }

    private val List<KParameter>.isAllRegistered: Boolean
        get() = this.all { it.type.classifier in registeredClasses }
}


fun main() {
    /*
    DiContainerV1.register(AService::class)
    val aService: AService = DiContainerV1.getInstance(AService::class)
    aService.print()
    */

    /*
    컨테이너에서 BService 를 가져올때
    컨테이가 알아서 AService 까지 인스턴스화 해서 BService 와 연결시켜줌
    -> 의존성 주입을 대신 해주는 것
    */
    /*
    DiContainerV2.register(AService::class)
    DiContainerV2.register(BService::class)
    val bService = DiContainerV2.getInstance(BService::class)
    bService.print()
    */

    /*
    3단계
    클래스에 어노테이션을 붙이면 그 클래스들이 자동으로 컨테이너에 등록된다
    -> 만약 DI 컨테이너에서 수십개 수백개 클래스를 관리한다면 한땀한땀 코드를 작성해줘야함

    예:
    val reflections = Reflections("com.yanggang.advanced.di")
    // MyAutowired::class.java 는 자바 리플렉션 객체
    val jClasses = reflections.getTypesAnnotatedWith(MyAutowired::class.java)
    println(jClasses)
    */

    /*
    예: Di 클래스가 있는 패키지를 기준으로 하위 클래스까지 모두
    컨테이너에 자동 등록되게 됨
    */
    start(Di::class)
    val bService = DiContainerV2.getInstance(BService::class)
    bService.print()
}

annotation class MyAutowired

@MyAutowired
class AService {

    fun print() {
        println("A 서비스 입니다")
    }
}

@MyAutowired
class BService (
    private val aService: AService,
    private val cService: CService?,
) {

    /*
    AService, CService 서비스가 필요한 생성자가 하나 있고
    부생성자로 AService 만 필요한 생성자가 있다면

    -> 주생성자가 있지만, 컨테이너는 사용 가능한 부생성자를 생성자로 간주하여
    BService 와 AService 연관관계를 맺어준뒤 BService 의 print() 호출됨
    */
    constructor(aService: AService): this(aService, null)

    fun print() {
      this.aService.print()
    }
}

class CService
