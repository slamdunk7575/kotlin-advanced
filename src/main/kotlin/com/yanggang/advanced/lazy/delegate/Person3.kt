package com.yanggang.advanced.lazy.delegate

import kotlin.reflect.KProperty

class Person3 {

    /* backing property 사용
    객체 인스턴스 시점과 초기화 시점을 분리하고
    최초로 변수를 한번 사용하려고 할때, 지정된 로직을 1회 실행시켜 값을 할당

    private var _name: String? = null
    val name: String
        get() {
            if (_name == null) {
                Thread.sleep(2_000)
                this._name = "김수한무"
            }
            return this._name!!
        }
    */

    // 위 코드를 템플릿화
    /*
    private val delegateProperty = LazyInitProperty {
        Thread.sleep(2_000)
        "김수한무"
    }
    */

    /*
    Person 의 getter 가 호출되면, 곧바로 LazyInitProperty 의 getter 가 호출됨
    -> 이런 패턴을 위임패턴 이라고 함

    val name: String by lazy {
     ...
    }
    by lazy 역시 완전히 동일한 원리로 동작함!
    by: name 의 getter 를 Lazy 객체의 getter 로 이어줌 (초기화 함수가 담겨있는 Lzay 객체가 생기고 이 객체와 name 이 이어지는 것)
    lazy: lazy 함수는 Lazy<T> 객체를 반환하고 있다 (Lazy<T>)
    예: public actual fun <T> lazy(initializer: () -> T): Lazy<T> = SynchronizedLazyImpl(initializer)

    Q. Lazy 객체의 getter 를 어떻게 알 수 있을까?
    -> by 뒤에 위치한 클래스는 약속된 함수(getValue 또는 setValue) 를 가지고 있어야 함

    name 을 '위임 프로퍼티' 라고 하고
    뒤에 String by lazy 를 '위임 객체' 라고 함
    */

    /*
    val name: String
        get() = delegateProperty.value
    */

    /*
    getValue & setValue 정리

    operator fun getValue(thisRef: R, property: KProperty<*>): T {
    }

    thisRef: R 는 위임 프로퍼티를 갖고 있는 클래스의 인스턴스 (예: Person3 클래스)
    property: KProperty<*> 는 실제 위임 프로퍼티 정보 (예: name 정보)
    operator fun 라는 특별한 지시어로 작성

    operator fun setValue(thisRef: R, property: KProperty<*>, value: T){
    }

    setter 에서는 값이 변경되어야 하기 때문에, 변경될 값을 추가로 받음 (value: T)
    */

    // LazyInitProperty 에 getValue 함수를 작성하여 Lazy 대신 사용
    val name: String by LazyInitProperty {
        Thread.sleep(2_000)
        "김수한무"
    }

    /*
    위 코드를 decompile 해보면, Lazy name$delegate 가 생성되고
    이 객체의 (String)var1.getValue(); 함수를 호출함

    public final class Person3 {
        @NotNull
        private final Lazy name$delegate;

        @NotNull
        public final String getName() {
            Lazy var1 = this.name$delegate;
            Object var3 = null;
            return (String)var1.getValue();
   }
   */
}


class LazyInitProperty<T>(val init: () -> T) {
    private var _value: T? = null
    private val value: T
        get() {
            if (_value == null) {
                this._value = init()
            }
            return _value!!
        }

    /*
    Lazy 객체의 getValue 는 thread-safe 하게 작성되어 있음
    연습으로 만든 아래 코드는 위험함
    */
    operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        return value
    }
}
