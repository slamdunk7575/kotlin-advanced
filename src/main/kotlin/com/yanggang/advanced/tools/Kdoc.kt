package com.yanggang.advanced.tools

/*
kdoc (코틀린 코드의 문서화를 위해 작성되는 언어)
- javadoc 과 유사함
- 마크다운 문법을 사용할 수 있다
*/
class Kdoc {
}


/**
 * Box 클래스. 생성자에 name 이 들어간다.
 * (문제점) 주석만 사용하게 되면 줄글을 모두 읽고 원하는 부분을 찾아야 한다
 *
 * (해결) kdoc 의 block tag 를 사용
 * - 블록 태그는 렌더링 했을때 좀 더 정리되서 나옴
 * - 원하는 위치로 이동할 수 있다
 *
 * Box 클래스
 *
 * **강조** (마크 다운 문법을 지원한다)
 *
 * @param T Box 아이템 타입 (@param: 함수의 파라미터 또는 타입 파라미터)
 * @property name Box 이름 (@property: 클래스의 특정 프로퍼티)
 *
 * - @constructor: 클래스의 주생성자
 * - @receiver: 확장 함수의 수신객체
 * - @throws, @exception: 함수를 통해 발생할 수 있는 예외를 의미
 * - @sample: 주어진 qualified name 을 이용해서 함수의 body 를 임베딩
 *   (qualified name 이란? 아래처럼 패키지를 포함한 전체 경로를 의미)
 * @sample com.yanggang.advanced.tools.abc.def.helloWorld
 * - @see: 특정 클래스, 특정 함수로 링크를 연결
 * - @author: 저자를 명시
 * - @since: 소프트웨어 버전을 명시
 * - @suppress: API 문서에는 담지 않을 주석을 작성할때 사용
 */
class Box<T>(val name: String) {

    /**
     * @param item Box 에 들어갈 item
     */
    fun add(item: T): Boolean {
        println("테스트")
        return true
    }
}

/*
dokka (위에 작성한 kdoc 을 문서로 만들수 있다)
1. id("org.jetbrains.dokka") version "1.9.20" 추가
2. gradle -> tasks -> documentation - dokkaHtml 빌드
3. ./build/dokka/html/index.html 문서로 생성됨

dokka 관련 설정은 공식 홈페이지 참고
https://kotlinlang.org/docs/dokka-introduction.html
*/
