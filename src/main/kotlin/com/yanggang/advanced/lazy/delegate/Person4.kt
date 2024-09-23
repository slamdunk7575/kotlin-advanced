package com.yanggang.advanced.lazy.delegate

import kotlin.properties.Delegates.notNull
import kotlin.properties.Delegates.observable
import kotlin.properties.Delegates.vetoable

class Person4(map: Map<String, Any>) {
    /*
    코틀린의 표준 위임 객체
    1. notNull()
    - lateinit 과 비슷한 역할 (Person 의 age 에 미리 값을 넣지 않고도 Person 객체를 인스턴스화)
    - notNull 이 붙은 위임 프로퍼티(예: age) 가 초기화 되지 않고 사용된다면 IllegalStateException 발생
    - primitive type 에는 lateinit 을 사용할 수 없지만, notNull() 은 사용할 수 있다
     */
    var age: Int by notNull()

    /*
    2. observable()

    public inline fun <T> observable(
    initialValue: T,
    crossinline onChange: (property: KProperty<*>, oldValue: T, newValue: T) -> Unit):

    observable 함수는 초기값, onChage 함수를 받음
    - 위임 프로퍼티의 setter 가 호출 될때마다 onChage 함수 호출됨
    - onChage 함수 파라미터로 아래 3개를 받음
    프로퍼티 정보 (property: KProperty<*>),
    프로퍼티 현재값 (oldValue),
    setter 를 통해 들어온 새로운 값 (newValue)
    */

    // 예: age2 초기값은 20 이고 onChage() 는 람다로 넣어줌
    // 사용하지 않는 프로퍼티 정보는 _ 언더바 처리
    // 현재와 같은 값으로 변경해도 setter 가 호출되어 onChage 함수가 동작함 ->
    var age2: Int by observable(20) { _, oldValue, newValue ->
        // 현재와 같은 값으로 변경해도 setter 가 호출되어 onChage 함수가 동작함
        // 같은 값인지 체크하는 로직을 아래처럼 내부에 로직을 추가해서 처리
        if (oldValue != newValue) {
            println("옛날 값: $oldValue , 새로운 값: $newValue")
        }
    }

    /*
    3. vetoable

    public inline fun <T> vetoable(
    initialValue: T,
    crossinline onChange: (property: KProperty<*>, oldValue: T, newValue: T) -> Boolean
    ):

    - observable 과 매우 유사함
    - 차이점: onChage 함수가 Boolean 을 반환함
    setter 가 호출될 때 onChange 함수가
    true 반환하면 변경 적용, false 반환하면 이전 값이 그대로 남음을 의미
    */

    // 초기값 20, 새로운 값이 1 이상일때만 true 를 반환 -> age 를 -10 으로 변경하면 적용되지 않음
    var age3: Int by vetoable(20) { _, _, newValue -> newValue >= 1 }


    /*
    4. 또 다른 프로퍼티로 위임하기
    예: 사람의 나이를 num 프로퍼티에 저장하고 있었을 경우
    num 이라는 네이밍은 나이와 어울리지 않기 때문에 바꾸고 싶은데
    만약, 기존에 여러곳에서 코드를 사용하고 있다면 호환성을 유지해야한다

    프로퍼티 앞에 :: 을 붙이면, 위임 객체로 사용할 수 있다
    -> age 를 가져올때 num 을 가져오고 age 를 변경할때 num 을 변경한다
    -> 코드 사용자들이 기존 코드를 num -> age 로 바꾸면 그때 num 을 제거하면 된다
    */
    @Deprecated("age4 를 사용하세요.", ReplaceWith("age4"))
    var num: Int = 0
    var age4: Int by this::num

    /*
    5. Map
    예: class Person4(map: Map<String, Any>)

    person4.name 을 하면 name 은 실제 Person4 의 프로퍼티가 아니라
    위임된 Map 객체에서 key 가 name 것을 찾음 -> map["name"] 또는 map["age"] 값을 찾음
    MutableMap 을 사용하면, val 대신 var 를 쓸 수 있음

    getName 호출 -> Person -> get("name") 호출 -> Map(위임 객체)
    결과 전달 <- Person <- 응답 <- Map(위임 객체)
    결과가 없으면 예외발생
    예: age5 값은 없기 때문에, Key age5 is missing in the map 예외 발생

    코틀린에서 제공하는 표준 위임 객체를 외울 필요없다 -> getVlaue, setValue 원리로 동작하는 것만 이해하자
    */

    val name: String by map
    val age5: Int by map
}

fun main() {
    /*
    val p = Person4()
    p.age2 = 30
    */

    val person = Person4(mapOf("name" to "yanggang"))
    println(person.name)
    println(person.age5)
}
