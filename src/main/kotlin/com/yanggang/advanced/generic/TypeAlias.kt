package com.yanggang.advanced.generic

class PersonDtoKey {
}

class PersonDto {
}

/*
제네릭을 사용하면 타입 이름이 길어짐
typealias 를 사용해서 긴 제네릭 클래스 타입의 별칭을 지정해서
함수나 변수의 타입을 적은 타이핑으로 선언할 수 있다
*/
typealias PersonDtoStore = Map<PersonDtoKey, MutableList<PersonDto>>

class TypeAlias {
    fun handleCacheStore(store: PersonDtoStore) {
    }
}
