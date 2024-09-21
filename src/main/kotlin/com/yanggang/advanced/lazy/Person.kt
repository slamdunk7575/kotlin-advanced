package com.yanggang.advanced.lazy

fun main() {
    val person = Person()
    person.isKim
}

/*
Q. 만약 인스턴스화 시점과 프로퍼티 초기화 시점을 분리하고 싶다면?

현재 PersonTest 코드 특징
1. 두개의 테스트 메소드가 Person 을 각각 인스턴스화 하고 있다
2. 두개의 테스트 메소드는 Person 의 name 에 대한 초기값이 다르다

-> 인스턴스화를 한번만 하고 테스트 할때 변수를 초기화 하고 싶다
또한, 인스턴스화 할때 초기값을 넣어주고 싶지 않다

방법 1. name 프로퍼티에 기본값을 넣어주기
단점:
- 누군가 Person 객체를 인스턴스화 한 후, name 을 초기화 하지 않더라도 예외가 발생하지 않아 알 수 없음
- 기본값으로 쓰인 예: 홍길동 값은 누군가의 이름일 수 있다

방법 2. name 을 nullable 로 만들자
(방법 1의 단점 보완)
초기화를 하지 않으면 null 아님 단언에 의해 에러가 발생할 수 있고
누군가의 이름이 null 인 경우는 없다

단점:
- 실제 null 이 될 수 없기 때문에, 계속 null 처리(?. / ?: / !!) 가 들어가게 된다

방법 3. lateinit 을 사용한다
-> 핵심은 인스턴스화 시점과 변수 초기화 시점을 분리하는것
초기값이 지정되지 않았는데 변수를 사용하려 하면 예외가 발생
Exception in thread "main" kotlin.UninitializedPropertyAccessException: lateinit property name has not been initialized

Q. lateinit 은 어떤 식으로 만들어 질까?
-> lateinit 변수는 컴파일 단계에서 nullable 변수로 바뀌고
변수에 접근하려 할때, null 이면 예외가 발생함

예: decomplie 코드
public final class Person {
   public String name;

   @NotNull
   public final String getName() {
      String var10000 = this.name;
      if (var10000 == null) {
         Intrinsics.throwUninitializedPropertyAccessException("name");
      }

      return var10000;
   }

단점:
- lateinit 을 primitive type 에 사용할 수 없다
코틀린의 Int / Long 은 자바의 int / long 로 변환된다 (Int? -> null 이 들어 갈 수 있기 때문에 자바의 Integer 로 변환)
그런데 lateinit 은 nullable 변수로 변환되어야 하기 때문에
*/

class Person {
    // 기본값이 있기 때문에 생성자에 있을 필요가 없음
    // var name: String = "홍길동"

    // var name: String? = null

    lateinit var name: String

    val isKim: Boolean
        get() = this.name.startsWith("김")

    // 양갱준 -> 양**
    val maskingName: String
        get() = name[0] + (1 until name.length).joinToString("") { "*" }
}
