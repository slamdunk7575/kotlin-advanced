package com.yanggang.advanced.tools

/*
Lint 란?
코딩을 할때 들여쓰기, 탭, 코드 컨벤셔을 자동으로 검사하거나 맞춰주는 도구를 의미

1. id("org.jlleitschuh.gradle.ktlint") version "12.1.1" 플러그인 추가
2. gradle -> tasks -> formatting -> ktlintFormat
   gradle -> tasks -> verification -> ktlintCheck

ktlintCheck: 프로젝트에서 코드 컨벤션이 틀린 부분을 찾아준다
예:
/Users/yanggang/GIT/kotlin-advanced/src/test/kotlin/com/yanggang/advanced/lazy/PersonTest.kt:27:1 Unexpected blank line(s) before "}"

ktlintFormat: 틀린 부분을 찾아주고 설정된 ktlint 규칙에 따라 코드를 수정한다
만약 코드 수정이 불가능 하면 에러가 발생한다


*/
class Ktlint {
}
