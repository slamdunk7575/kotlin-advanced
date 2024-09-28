package com.yanggang.advanced.function

/* inline 함수
-> 함수를 호출하는 쪽에 함수 본문을 넣게 된다

예:
public static final void main() {
      int num1 = 1;
      int num2 = 2;
      int $i$f$add = false;
      int var10000 = num1 + num2;
}

add 함수 호출 대신 덧셈 자체가 main 함수 안으로 들어왔다
-> 이런 특성을 활용해서 고차함수(다른 함수를 받는 함수)를 인라이닝 하면
그때마다 함수 콜이 발생하지 않기 때문에 조금 더 성능을 끌어올릴 수 있었음

*/
inline fun add(num1:Int, num2:Int): Int {
    return num1 + num2
}

/*
inline 함수는 나자신(예: repeat 함수) 외에도
파라미터로 받고 있는 함수(예: exec 함수) 까지도 인라이닝 시킨다

예:
public static final void main() {
      int times$iv = 2;
      int $i$f$repeat = false;
      int i$iv = 1;
      byte var6 = times$iv;

      while(true) {
         int var7 = false;
         String var8 = "Hello Yanggang";
         System.out.println(var8);
         if (i$iv == var6) {
            return;
         }

         ++i$iv;
      }

하지만, 모든 경우 다른 함수를 인라이닝 시킬 수 있는 것은 아니다

예: main 에서 exec 함수를 받도록 수정해서 repeat 함수로 그대로 넘김
-> 이런 경우 exec 함수를 확실히 알 수가 없음
fun main(exec: () -> Unit) {
    repeat(2, exec)
}

디컴파일 해보면, exec 함수를 알 수 없기 때문에 인라이닝 되지 않고 Function0 exec 함수를 호출하도록 바뀜
public static final void repeat(int times, @NotNull Function0 exec) {
      int $i$f$repeat = 0;
      Intrinsics.checkNotNullParameter(exec, "exec");
      int i = 1;
      int var4 = times;
      if (i <= times) {
         while(true) {
            exec.invoke();
            if (i == var4) {
               break;
            }

            ++i;
         }
      }

   }

강제로 인라이닝을 막을 수 있다 -> noinline 키워드를 붙이면 인라이닝 되지 않는다

예:
inline fun repeat(times:Int,
                  noinline exec: () -> Unit
)

디컴파일 해보면, 함수 파라미터의 인라이닝을 막음
public static final void repeat(int times, @NotNull Function0 exec) {
      int $i$f$repeat = 0;
      Intrinsics.checkNotNullParameter(exec, "exec");
      int i = 1;
      int var4 = times;
      if (i <= times) {
         while(true) {
            exec.invoke();
            if (i == var4) {
               break;
            }
            ++i;
         }
      }

   }
*/
inline fun repeat(times:Int,
                  noinline exec: () -> Unit
) {
    for (i in 1..times) {
        exec()
    }
}

/* inline 함수는 인라이닝에만 관여하는게 아니다 -> non-local return(비지역적 리턴)을 가능하게 해준다

예:
interate2 함수에 inline 키워드를 붙이게 되면, non-local return 이 가능하게됨
하지만, 의도치 않은 결과가 나오게 된다
-> 3이 아닌 1, 2, 4, 5 가 출력되기를 원하지만 1, 2 만 출려된다

왜? non-local return 으로 main 함수가 리턴되어 종료되기 때문
-> 이말은 inline 함수로 비지역적 리턴을 같이 쓸때 문제가 생길 수 있다는 얘기

Q. inline 함수의 함수 파라미터에서 non-local return 을 금지시킬 수 있을까?
-> crossinline 키워드 사용하면 비지역적 반환이 금지된다

예:
inline fun iterate2(
    numbers: List<Int>,
    crossinline exec: (Int) -> Unit
) {
*/

inline fun iterate2(
    numbers: List<Int>,
    crossinline exec: (Int) -> Unit
) {
    for (num in numbers) {
        exec(num)
    }
}

fun main() {
    val num1 = 1
    val num2 = 2
    // val result = add(num1, num2)

    // repeat(2) { println("Hello Yanggang") }

    iterate2(listOf(1, 2, 3, 4, 5)) { num ->
        if (num == 3) {
            // return
        }
        println(num)
    }
}

/* inline 프로퍼티
커스텀 getter 를 써서 함수로 만들 수 있는걸 프로퍼티로 만들기도 하는 경우가 있는데
이때 inline 을 써주면, 프로퍼티를 호출할때 (예: upperCaseName) getter 본문이 인라이닝 된다
*/
class Person(val name: String) {
    inline val upperCaseName: String
        get() = this.name.uppercase()
}
