package com.yanggang.advanced.function

fun main() {
    val filter1: StringFilter = object : StringFilter {
        override fun predicate(str: String?): Boolean {
            return str?.startsWith("A") ?: false
        }
    }

    /*
    코틀린에서 SAM 을 람다식(코틀린의 람다식)으로 인스턴스화 할 수 없다
    -> SAM + 람다식 = 'SAM 생성자' 를 통해 인스턴스화 할 수 있음
    */
    val filter2: StringFilter = StringFilter { s: String -> s.startsWith("A") }

    /*
    변수에 넣는게 아니라, 파라미터에 넣을거라면 바로 람다식을 쓸 수 있다

    하지만, 이렇게 암시적인 SAM 인스턴스화를 할 경우
    의도하지 않은 SAM 이 호출될 수 있다

    예:
    추상화된 타입(Filter) 과 좀 더 구체화된 타입 (StringFilter)이 있다고 했을때
    아래처럼 람다식으로 호출하게 되면 좀 더 구체화된 타입(StringFilter) 이 호출된다
    Filter 가 호출될거라 생각했지만, 혹시나 실수 할 수 있음
    -> 이를 막기위해 SAM 생성자를 직접 사용

    */
    consumeFilter(Filter<String> { s -> s.startsWith("A") })

    /*
    코틀린에서 SAM Interface 를 만들고 싶으면
    추상 메소드가 1개인 인터페이스 앞에 fun 을 붙이면 된다

    코틀린만 사용하때는 SAM Interface 를 사용할 일이 거의 없다
    -> 함수를 1급 시민으로 간주해서 여기저기 옮길 수 있기 때문에
    */
    KStringFilter { it.startsWith("A") }
}

fun consumeFilter(stringFilter: StringFilter) {
}

fun <T> consumeFilter(filter: Filter<T>) {
}

fun interface KStringFilter {
    fun predicate(str: String): Boolean
}
