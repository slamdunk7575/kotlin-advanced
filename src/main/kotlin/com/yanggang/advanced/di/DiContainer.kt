package com.yanggang.advanced.di

import kotlin.reflect.KClass

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

// 싱글턴으로 만들기 위해 object 키워드(클래스 정의와 동시에 객체를 생성) 사용
object DiContainerV1 {
    // 등록한 클래스 보관 = KClass 를 보관
    private val registeredClasses = mutableSetOf<KClass<*>>()

    fun register(clazz: KClass<*>) {
        registeredClasses.add(clazz)
    }

    fun <T: Any> getInstance(type: KClass<T>): T {
        return registeredClasses.firstOrNull { clazz -> clazz == type }
            ?.let { clazz -> clazz.constructors.first().call() as T }
            ?: throw IllegalArgumentException("해당 인스턴스 타입을 찾을 수 없습니다")
    }
}

fun main() {
    DiContainerV1.register(AService::class)
    val aService: AService = DiContainerV1.getInstance(AService::class)
    aService.print()
}

class AService {

    fun print() {
        println("A 서비스 입니다")
    }
}
