package com.yanggang.advanced.extra

import kotlin.reflect.KClass

/*
꼬리 재귀 함수(재귀함수 호출 이후 추가적인 연산이 없는 형태)
-> factorial 함수는 재귀함수 이지만, 꼬리 재귀함수는 아니다
왜? 꼬리 재귀함수가 되려면, 재귀함수 호출 이후에 추가 연산이 없어야 한다
factorial 함수는 n * factorial(n - 1) 이라는 n 을 곱하는 추가적인 연산이 있다

-> factorialV2 는 꼬리 재귀함수이다


이런 꼬리 재귀함수에 tailrec 이라는 키워드를 붙이면 컴파일 과정에서 최적화가 이루어진다

예: tailrec 붙이기 전
public final class ExtraKt {
   public static final int factorial(int n) {
      return n <= 1 ? 1 : n * factorial(n - 1);
   }
   ...

tailrec 붙이면 최적화를 통해 스택이 쌓이는 재귀함수 호출을 루프로 변경해 컴파일 된다
재귀함수의 경우 지속적으로 함수 호출이 일어나면 함수를 호출할때마다 스택이 쌓여 메모리에 부담이 갈 수 있는데
이걸 루프로 바꾸면 함수 콜이 일이나지 않기 때문에 스택이 쌓일 일이 없고 상대적으로 메모리를 덜 사용하게 됨

예:
public static final int factorialV2(int n, int curr) {
      while(n > 1) {
         int var10000 = n - 1;
         curr = n * curr;
         n = var10000;
      }

      return curr;
   }
*/
fun factorial(n: Int): Int {
    return if (n <= 1) {
        1
    } else {
        n * factorial(n - 1)
    }
}

tailrec fun factorialV2(n: Int, curr: Int = 1): Int {
    return if (n <= 1) {
        curr
    } else {
        factorialV2(n - 1, n * curr)
    }
}


fun main() {
    val key = Key("비밀번호")
    println(key)

    val userId = 1L
    val bookId = 2L
    /*
    Q. 인라인 클래스는 어디에 사용할 수 있을까?

    (문제점)
    handle 함수를 호출할때 파라미터 순서가 바뀌어도 컴파일 단계에서는 알 수 없다

    해결책으로 named argument 를 사용할 수도 있다
    하지만 지금은 변수 이름이 짧고 파라미터 갯수가 별로 없기 때문에 괜찮지만
    길어지면 문제가 발생할 수 있다 -> 이럴때 인라인 클래스를 활용할 수 있음

    사용 1.
    여러 객체들 or 엔티티들이 있을때 그 엔티티들이 같은 타입의 식별자를 가지고 있고
    그 식별자를 키로써 여기저기 전달해야 한다면 인라인 클래스를 활용하여
    각각의 식별자를 같은 클래스이만 다른 타입 파라미터를 갖는 별도의 타입으로 구분할 수 있음

    사용 2.
    객체지향적 프로그래밍을 위해 원시값 포장 객체를 사용하게 되는데
    이때 인라인 클래스로 만들어 볼 수 있음

     */
    handle(bookId = bookId, userId = userId)

    val inlineUserId = Id<User>(1L)
    val inlineBookId = Id<Book>(2L)

    /*
    인라인 클래스 Id 를 사용하면 혹시라도 바꿔서 사용하면 컴파일에서 에러 발생한다
    handle2(inlineBookId, inlineUserId)

    decompile 해보면 long 타입으로 변경됨 (오버헤드도 크게 없음)
    long inlineUserId = Id.constructor-impl(1L);
    long inlineBookId = Id.constructor-impl(2L);

    */
    handle2(inlineUserId, inlineBookId)


    /*
    when expression 활용하는 방법

    (문제점)
    - JAVA 처럼 문법이 깔끔하지 않음
    - when 절로 depth 가 한번 더 들어가게 됨
     */
    try {

    } catch(e: Exception) {
        when(e) {
            is AException,
            is BException -> println("A 또는 B 예외가 발생했습니다")
            is CException -> println("C 예외가 발생했습니다")
        }
        throw e
    }

    runCatching { logic(10) }
        .onError(AException::class, BException::class) {
            println("A 또는 B 예외가 발생했습니다")
        }
        .onError(CException::class) {
            println("C 예외가 발생했습니다")
        }
        .onError(AException::class) {
            /*
            다시.onError(AException::class) 를 써도 내부에 knownException 을 통해 또 타지 않음
            */
        }

}

/*
인라인 클래스

예: 인라인 클래스 만들기 전
public static final void main() {
      Key key = new Key("비밀번호");
      System.out.println(key);
   }

인라인 함수 or 프로퍼티는 함수 본문 또는 프로퍼티 본문이 컴파일 과정에 인라이닝 되는 의미였음
비슷하게 클래스가 프로퍼티가 하나인 경우에 @JvmInline, value 키워드를 붙이면 인라인 클래스가 됨
컴파일 해보면 클래스의 프로퍼티가 인라인 클래스를 사용한 곳에 그대로 들어가게 된다

-> 인라인 클래스는 프로퍼티를 1개만 가지고 있어야 한다

예: String 타입의 key 로 변환 (String 타입의 내부 프로퍼티로 인라이닝 된것)
public static final void main() {
      String key = Key.constructor-impl("비밀번호");
      Key var1 = Key.box-impl(key);
      System.out.println(var1);
   }
*/
@JvmInline
value class Key(val key: String)

class User(
    val id: Long,
    val name: String,
)

class Book(
    val id: Long,
    val name: String,
)

fun handle(userId: Long, bookId: Long) {
}

fun handle2(userId: Id<User>, bookId: Id<Book>) {
}

@JvmInline
value class Id<T>(val id: Long)

@JvmInline
value class Number(val num: Long) {
    init {
        require(num in 1..10)
    }
}

/*
try catch 구문에서 여러 예외를 받고 싶은 경우

JAVA 에서는 아래처럼 여러 예외를 버티컬(|) 하나로 구분해서 받을 수 있음
코틀린에서는 이런 문법이 불가능하다

예:
try {

} catch (AException | BException e) {

}

Q. 그럼 코틀린에서는 어떻게 이런 문법을 사용할 수 있을까?

방법1. runCaching 과 Result<T> 객체를 확장해서 원하는 형태의 체이닝으로 만들 수 있음
(아래 ResultWrapper, Result<T>.onError 함수 참고)

방법2. when expression 을 활용
(위에 코드 참고)
*/

fun logic(n: Int) {
    when {
        n > 0 -> throw AException()
        n == 0 -> throw BException()
    }
    throw CException()
}

class AException : RuntimeException()
class BException : RuntimeException()
class CException : RuntimeException()


class ResultWrapper<T>(
    private val result: Result<T>,
    private val knownException: MutableList<KClass<out Throwable>>,
) {
    fun toResult(): Result<T> {
        return this.result
    }

    fun onError(vararg exceptions: KClass<out Throwable>, action: (Throwable) -> Unit): ResultWrapper<T> {
        this.result.exceptionOrNull()?.let {
            if (it::class in exceptions && it::class !in this.knownException) {
                action(it)
            }
        }
        return this
    }
}

/*
코틀린 표준 라이브러리 runCatching 에서 얻을 수 있는 Result 의 확장함수를 만듦
내부적으로 예외가 존재할때, exceptions 에 존재하는 유형이면 action 을 실행시킴
ResultWrapper 를 반환하고 체이닝을 통해 onError 함수를 써서 다시 원하는 예외를 필터링 할 수 있다
최종적으로 체이닝이 끝나면 toResult() 를 통해 Result 를 다시 가져올 수 있음

ResultWrapper 를 사용한 이유?
-> 상위 exception 이 하위 exception 으로 내려오지 않게 하기 위해서
 */
fun <T> Result<T>.onError(vararg exceptions: KClass<out Throwable>, action: (Throwable) -> Unit): ResultWrapper<T> {
    exceptionOrNull()?.let {
        if (it::class in exceptions) {
            action(it)
        }
    }
    return ResultWrapper(this, exceptions.toMutableList())
}
