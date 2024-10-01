package com.yanggang.advanced.function

fun main() {
    // compute1(1, 2) { num1, num2 -> num1 * num2 }

    var num = 5
    num += 1
    val plusOne: () -> Unit = { num += 1 }
}


/* 1. 복잡한 함수 타입
파라미터로 다른 함수를 받는 compute1 함수 타입
: 파라미터로 Int, Int, Int 2개를 받아 Int 를 반환하는 함수이고 반환타입은 Int
(Int, Int, (Int, Int) -> Int) -> Int

다른 함수를 반환하는 opGenerate 함수 타입
:파라미터는 없고 반환타입이 Int 2개를 받아 Int 를 반환하는 함수
() -> (Int, Int) -> Int

코틀린에는 일반함수만 있는게 아니라 확장함수도 있음
예: fun Int.add(other: Long): Int = this + other.toInt()
여기서 Int 를 수신객체타입, this 를 수신객체 라고함

이런 함수 리터럴을 '수신 객체가 있는 함수 리터럴' 이라고 함
Int.(Long) -> Int
-> 수신 객체가 있는 함수 리터럴은 DSL 을 만들때 유용하게 사용할 수 있음
*/

fun compute1(num1: Int, num2: Int, op: (Int, Int) -> Int): Int {
    return op(num1, num2)
}

fun opGenerator(): (Int, Int) -> Int {
    return { a, b -> a + b }
}

/* 2. 함수 리터럴 호출

일반함수 호출
예:
add(1, 2)
add.invoke(1, 2)

확장함수도 동일하게 호출 가능
예:
add(1, 2L)
add.invoke(1, 2L)

-> 수신 객체가 있는 함수 리터럴을 확장 함수를 호출하는 것과 동일하게 사용할 수 있음
예: 1.add(2L)
*/
// val add1 = { a:Int, b:Int -> a + b }
// val add2 = fun Int.(other: Long): Int = this + other.toInt()


/* Q. 고차 함수는 컴파일 되었을때 어떻게 처리될까?

- 고차 함수에서 함수를 넘기면, FunctionN 클래스로 변환됨 -> N 은 파라미터 갯수를 의미
(함수를 변수처럼 사용할 때마다 FunctionN 객체가 만들어짐)

   예:
    public static final void main() {
      compute1(1, 2, (Function2)null.INSTANCE);
   }

   // $FF: synthetic method
   public static void main(String[] var0) {
      main();
   }

   public static final int compute1(int num1, int num2, @NotNull Function2 op) {
      Intrinsics.checkNotNullParameter(op, "op");
      return ((Number)op.invoke(num1, num2)).intValue();
   }

   - 클로저를 사용하면 조금 더 복잡해진다
   (클로저: 람다식에서 외부에 있는 변수에 접근하기 위해 일시적으로 밖의 정보를 포획해두는 개념)

   코틀린의 람다식이 외부 변수를 가리키며 Ref 라는 객체로 감싸진다
   람다식은 파라미터가 0개 이므로 Funtion0 클로스로 변환됨

   예:
   final Ref.IntRef num = new Ref.IntRef();
      num.element = 5;
      ++num.element;
      Function0 plusOne = (Function0)(new Function0() {
         // $FF: synthetic method
         // $FF: bridge method
         public Object invoke() {
            this.invoke();
            return Unit.INSTANCE;
         }

         public final void invoke() {
            ++num.element;
         }

   (고차함수 정리)
   1. 고차함수를 사용하게 되면 FunctionN 클래스가 만들어지고 인스턴스화 되어야한다 -> 오버헤드가 발생할 수 있음
   2. 함수에서 변수를 포획할 경우, 해당 변수를 Ref 라는 객체로 감싸야한다 -> 오버헤드가 발생할 수 있음

   하지만, 고차함수는 편리한 기능이 많다
   - 함수를 다른 함수로 보내서 더 유연한 프로래밍이 가능
   - 함수의 Depth 를 낮추거나 어떤 기능을 추상화 하는데 사용

   Q. 고차함수를 쓰지만, 성능 부담을 없앨 수 없을까?
   -> inline 함수
 */
