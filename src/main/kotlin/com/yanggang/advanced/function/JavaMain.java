package com.yanggang.advanced.function;

public class JavaMain {

    public static void main(String[] args) {

        /*
        SAM Interface: 추상 메소드를 하나만 가지고 있는 인터페이스

        자바에서 SAM Interface 를 람다(자바의 람다)로 인스턴스화 할 수 있다
        */

        // 방법 1. 익명 클래스 활용
        StringFilter stringFilter = new StringFilter() {
            @Override
            public boolean predicate(String str) {
                return false;
            }
        };

        // 방법 2. 람다 활용
        StringFilter stringFilter2 = s -> s.startsWith("A");
    }
}
