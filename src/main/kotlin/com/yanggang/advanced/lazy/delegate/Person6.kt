package com.yanggang.advanced.lazy.delegate

import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class Person6 {
    // val name by DelegateProperty("양갱준", "name")
    // val country by DelegateProperty("대한민국", "country")

    val name by DelegateProvider("양갱준")
    val country by DelegateProvider("대한민국")
}

/*
2-1. 위임 프로퍼티와 객체를 연결할때 로직을 끼워 넣기
예: 프로퍼티 이름이 "name" 일때만 정상 동작해야 하는 위임객체

직관적인 방법으로, 프로퍼티 이름을 직접 넣어주어야 하기 때문에 번거롭다
비슷한 요구사항이 나올때마다 추가적인 작업을 해줘야함

-> 코틀린에서는 provideDelegate 함수를 제공함
: 어떤 객체가 provideDelegate 함수를 가지고 있으면, 위임 객체 대신 by 뒤에 사용할 수 있음

위임 프로퍼티 - (연결요청) -> ProvideDelegate - (proviteDelgate 함수 실행후 위임 객체 반환) -> 위임 객체
위임 프로퍼티 <- (위임 객체 반환) - ProvideDelegate <- 위임 객체

즉, 위임 프로퍼티 와 위임 객체가 직접 연결되지 않고 제 3자(ProvideDelegate 함수) 를 통해서 연결됨

operator fun ProvideDelegate(thisRef: R, prop: KProperty<*>): D
thisRef: R -> 객체 인스턴스
prop: KProperty<*> -> 위임 프로퍼티
D -> 위임 객체 반환

Q. 근대 ProvideDelegate 함수도 외우고 있어야 할까?
-> 코틀린에서는 PropertyDelegateProvider 를 사용할 수 있다
*/

class DelegateProperty(
    private val initValue: String,
    // private val propertyName: String
) : ReadOnlyProperty<Any, String> {

    /*
    init {
        if (propertyName != "name") {
            throw IllegalArgumentException("${ propertyName } 대신 name 에만 사용 가능합니다")
        }
    }
    */

    override fun getValue(thisRef: Any, property: KProperty<*>): String {
        return initValue
    }
}

class DelegateProvider(
    private val initValue: String
) : PropertyDelegateProvider<Any, DelegateProperty> {

    /*
    operator fun provideDelegate(thisRef: Any, property: KProperty<*>): DelegateProperty {

        if (property.name != "name") {
            throw IllegalArgumentException("${ property.name } 대신 name 에만 사용 가능합니다")
        }

        return DelegateProperty(initValue)
    }
    */

    override fun provideDelegate(thisRef: Any, property: KProperty<*>): DelegateProperty {
        if (property.name != "name") {
            throw IllegalArgumentException("${ property.name } 대신 name 에만 사용 가능합니다")
        }

        return DelegateProperty(initValue)
    }
}

fun main() {
    val person6 = Person6()
}
