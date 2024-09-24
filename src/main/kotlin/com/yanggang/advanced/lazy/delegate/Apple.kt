package com.yanggang.advanced.lazy.delegate

/*
위임 클래스는 위임 프로퍼티와 원리가 조금 다르다
예: 사과와 모든 기능은 동일하지만, 색깔만 다른 청사과(GreenApple)를 만든다면?
*/

interface Fluit {
    val name: String
    val color: String
    fun bite()
}

open class Apple : Fluit {
    override val name: String
        get() = "사과"
    override val color: String
        get() = "빨간색"

    override fun bite() {
        println("사과 아삭아삭~")
    }
}

/*
방법1. Fluit 구현한 GreenApple
단점: 중복 코드는 마음이 불편하다
*/
class GreenApple : Fluit {
    override val name: String
        get() = "사과"
    override val color: String
        get() = "초록색"

    override fun bite() {
        println("사과 아삭아삭~")
    }
}

/*
방법2. Apple 을 상속받은 GreenApple
단점: Apple 을 open 으로 열어줘야 한다 -> 다른 누군가가 상속 받을수도 있고 의도치 않은 사이드이펙트 발생
 */
class GreenApple2 : Apple() {
    override val color: String
        get() = "초록색"
}


/*
방법3. 상속 보다는 합성
장점: apple 만 변경해주면 GreenApple 도 변경되는 장점이 있음
단점: 중복은 피할 수 있었지만 방법2 보다 코드의 양이 늘어났다
*/
class GreenApple3(
    // 합성: private 한 변수로 들고 있으면서 중복되는 부분에 호출
    private val apple: Apple
) : Fluit {
    override val name: String
        get() = apple.name
    override val color: String
        get() = "초록색"

    override fun bite() {
        apple.bite()
    }
}

/*
방법4. 위임 클래스 사용 (위의 방법3 합성 코드와 동일)
-> 같은 인터페이스를 상속받은 두개의 클래스간에 그대로 사용할 기능은 그대로 쓰고
새로운 기능만 덮어쓰고 싶을때 사용

예: GreenApple 의 name 또는 bite() 를 부를때, Apple 의 name 이나 bite() 를 부른다는 것이
마치 위임 프로퍼티의 getter 를 부를때 위임 객체의 getValue 를 부르는 것과 비슷함

코틀린의 위임이 상속 보다는 합성이라는 객체지향 설계 원칙을 장려하는 언어적 느낌
*/
class GreenApple4(
    private val apple: Apple
) :Fluit by apple {
    override val color: String
        get() = "초록색"
}
