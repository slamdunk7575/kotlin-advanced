package com.yanggang.advanced.lazy

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test

class PersonTest {

    private val person = Person()

    @Test
    fun isKimTest() {
        // given
        val person = person.apply { name = "김수한무" }

        // when & then
        assertThat(person.isKim).isTrue()
    }

    @Test
    fun maskingNameTest() {
        // given
        val person = person.apply { name = "양갱준" }

        // when
        assertThat(person.maskingName).isEqualTo("양**")
    }

}
