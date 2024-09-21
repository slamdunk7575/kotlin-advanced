package com.yanggang.advanced.lazy

/*
Q. 변수를 초기화 할때 지정된 로직을 1회만 실행시키고 싶다
-> 값을 가져오는 비용이 크고, 해당 변수가 사용되지 않을 수도 있다면
초기화 로직을 1회만 실행시키고 싶을 수 있다

예: 파일을 읽어와야 하거나 or 네트워크 IO 를 타야하거나 등
근대 필드가 쓰일 수도 있고 쓰이지 않을 수도 있는 경우
*/

class Person2 {

    // val name: String

    /* 방법 1.
    name 을 사용하지 않으면 Thread.sleep() 이 호출되지 않지만
    name 을 쓸때마다 sleep 이 호출됨
    get() {
        Thread.sleep(2_000)
        return "김수한무"
    }
    */

    /* 방법 2.
    Thread.sleep() 은 1회만 호출되지만
    name 을 사용하지 않는 경우에도 sleep 이 호출됨

    init {
        Thread.sleep(2_000)
        name = "김수한무"
    }
    */

    /* 방법 3. backing property 사용한 방법
    -> name 이 사용되지 않으면 Thread.sleep() 이 호출되지 않을 것이고
    최초 한번 호출될때 _name 이 null 일 것이고 초기화 되고 그 이후에 반복적으로 name 이 호출되면 할당된 값을 리턴함

    단점: 만약 다른 필드가 추가된다면? 매번 긴 코드를 작성하는건 번거롭다

    private var _name: String? = null
    val name: String
        get() {
            if (_name == null ) {
                Thread.sleep(2_000)
                this._name = "김수한무"
            }
            return this._name!!
        }
    */

    /* 방법 4. by lazy 를 활용하여 간결하게 만들 수 있다
    - by lazy 는 코틀린에서 제공하는 함수이고 함수를 파라미터로 받는다
    - 이 함수는 name 의 getter 가 최초 호출될때 실행되고, 기본적으로 Thread-Safe 하다

    (정리)
    lateinit 은 초기화를 지연시킨 변수
    - 초기화 로직이 여러곳에 위치할 수 있음
    - 초기화 없이 호출하면 예외가 발생함

    by lazy 는 초기화를 get 호출 전으로 지연시킨 변수
    - 초기화 로직은 변수 선언과 동시에 한곳에만 위치할 수 있음 (지정된 초기화 로직 실행)
    */
    val name: String by lazy {
        Thread.sleep(2_000)
        "김수한무"
    }

}
