package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class DemoApplicationTests {

	Calculater underTest = new Calculater();

	@Test
	void itShouldAddNumbers() {
		//given
		int numOne = 20;
		int numTwo = 30;

		//when
		int result = underTest.add(numOne, numTwo);

		//then
		int expected = 50;
		 assertThat(result).isGreaterThan(22);
	}

	class Calculater {
		int add(int a, int b) {
			return a + b;
		}
	}

}
