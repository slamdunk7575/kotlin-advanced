package com.yanggang.advanced.lazy.delegate

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class Person5 {

    /* 위임과 관련된 몇가지 추가 기능
    (직접 위임 객체를 만들일이 드물기 때문에 이해만 하자)

    1-2. ReadOnlyProperty 와 ReadWriteProperty 사용
    -> 익명 클래스(일시적으로 사용할 위임 객체)에 사용할 수 있다
     */
    val status: String by object : ReadOnlyProperty<Person5, String> {
        private var isGreen: Boolean = false

        override fun getValue(thisRef: Person5, property: KProperty<*>): String {
            return if (isGreen) {
                isGreen = false
                "Happy"
            } else {
                isGreen = true
                "Sad"
            }
        }
    }
}

class LazyInitProperty2<T>(val init: () -> T) : ReadOnlyProperty<Any, T> {
    private var _value: T? = null
    private val value: T
        get() {
            if (_value == null) {
                this._value = init()
            }
            return _value!!
        }

    /* 1-1. ReadOnlyProperty 와 ReadWriteProperty 사용
    -> getValue(), setValue() 시그니처를 기억하지 않아도 된다

    위임 객체를 만들때 getValue(), setValue() 를 직접 정의할 수 있지만
    메소드 시그니처(예: getValue(thisRef: Any, property: KProperty<*>)) 를 항상 외우고 있기는 어렵다
    -> 이를 위해 코틀린은 ReadOnlyProperty, ReadWriteProperty 인터페이스를 만들어 놓음
    예: : ReadOnlyProperty<위임 객체가 사용될 수 있는 프로퍼티 클래스, 프로퍼티 타입>
    */

    /*
    operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        return value
    }
    */

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return value
    }
}
