package br.pucrs.verival.estacionamento;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculadoraTarifaComplementarTest {

    private final CalculadoraTarifa calculadora = new CalculadoraTarifa();

    @ParameterizedTest(name = "{index} => {4}")
    @MethodSource("casosValidosComplementares")
    void calcularValorTarifaComplementar(LocalDateTime entrada, LocalDateTime saida, boolean vip, double esperado, String descricao) {
        assertEquals(esperado, calculadora.calcular(entrada, saida, vip), 0.0001, descricao);
    }

    static Stream<Arguments> casosValidosComplementares() {
        return Stream.of(
            Arguments.of(dt(2026, 3, 10, 8, 0), dt(2026, 3, 10, 11, 0), false, 25.0, "CC01 180min duas horas adicionais exatas"),
            Arguments.of(dt(2026, 3, 10, 8, 0), dt(2026, 3, 10, 11, 1), false, 30.0, "CC02 181min tres horas adicionais"),
            Arguments.of(dt(2026, 3, 10, 8, 0), dt(2026, 3, 10, 23, 59), false, 90.0, "CC03 permanencia maxima no mesmo dia"),
            Arguments.of(dt(2026, 3, 10, 22, 0), dt(2026, 3, 10, 23, 59), false, 20.0, "CC04 saida limite 23:59 no mesmo dia"),
            Arguments.of(dt(2026, 3, 10, 23, 0), dt(2026, 3, 11, 0, 0), false, 15.0, "CC05 saida a meia-noite com 60min"),
            Arguments.of(dt(2026, 3, 10, 23, 59), dt(2026, 3, 11, 1, 59), false, 20.0, "CC06 entrada 23:59 e saida 01:59 com 120min"),
            Arguments.of(dt(2026, 3, 10, 23, 59), dt(2026, 3, 11, 8, 0), false, 50.0, "CC07 entrada 23:59 e saida 08:00 e pernoite"),
            Arguments.of(dt(2026, 3, 10, 20, 0), dt(2026, 3, 11, 8, 1), false, 50.0, "CC08 saida 08:01 dia seguinte e pernoite"),
            Arguments.of(dt(2026, 3, 10, 20, 0), dt(2026, 3, 12, 1, 59), false, 50.0, "CC09 saida 01:59 dois dias depois conta uma pernoite"),
            Arguments.of(dt(2026, 3, 10, 10, 0), dt(2026, 3, 13, 10, 0), false, 150.0, "CC10 tres pernoites"),
            Arguments.of(dt(2026, 3, 31, 20, 0), dt(2026, 4, 1, 8, 0), false, 50.0, "CC11 pernoite na virada de mes"),
            Arguments.of(dt(2026, 12, 31, 20, 0), dt(2027, 1, 1, 8, 0), false, 50.0, "CC12 pernoite na virada de ano"),
            Arguments.of(dt(2028, 2, 28, 20, 0), dt(2028, 3, 1, 8, 0), false, 100.0, "CC13 duas pernoites atravessando 29 de fevereiro"),
            Arguments.of(dt(2026, 3, 10, 8, 0), dt(2026, 3, 10, 9, 0), true, 7.5, "CC14 vip limite 60min"),
            Arguments.of(dt(2026, 3, 10, 8, 0), dt(2026, 3, 10, 9, 1), true, 10.0, "CC15 vip 61min uma hora adicional"),
            Arguments.of(dt(2026, 3, 10, 8, 0), dt(2026, 3, 10, 10, 1), true, 12.5, "CC16 vip 121min duas horas adicionais"),
            Arguments.of(dt(2026, 3, 10, 8, 0), dt(2026, 3, 10, 8, 20), true, 0.0, "CC17 vip cortesia limite 20min"),
            Arguments.of(dt(2026, 3, 10, 20, 0), dt(2026, 3, 12, 8, 0), true, 50.0, "CC18 vip duas pernoites"),
            Arguments.of(dt(2026, 3, 10, 10, 0), dt(2026, 3, 13, 10, 0), true, 75.0, "CC19 vip tres pernoites")
        );
    }

    @ParameterizedTest(name = "{index} => {3}")
    @MethodSource("casosInvalidosComplementares")
    void rejeitarCasosInvalidosComplementares(LocalDateTime entrada, LocalDateTime saida, boolean vip, String descricao) {
        assertThrows(IllegalArgumentException.class, () -> calculadora.calcular(entrada, saida, vip), descricao);
    }

    static Stream<Arguments> casosInvalidosComplementares() {
        return Stream.of(
            Arguments.of(dt(2026, 3, 10, 20, 0), dt(2026, 3, 11, 2, 1), false, "CI01 saida 02:01 invalida"),
            Arguments.of(dt(2026, 3, 10, 20, 0), dt(2026, 3, 11, 7, 58), false, "CI02 saida 07:58 invalida"),
            Arguments.of(dt(2026, 3, 10, 20, 0), dt(2026, 3, 12, 5, 0), false, "CI03 saida 05:00 dois dias depois invalida"),
            Arguments.of(dt(2026, 3, 10, 7, 59), dt(2026, 3, 10, 9, 0), true, "CI04 entrada 07:59 invalida para vip"),
            Arguments.of(dt(2026, 3, 10, 20, 0), dt(2026, 3, 11, 3, 0), true, "CI05 saida 03:00 invalida para vip"),
            Arguments.of(dt(2026, 3, 10, 10, 0), dt(2026, 3, 10, 9, 59), true, "CI06 saida anterior a entrada para vip"),
            Arguments.of(dt(2026, 3, 10, 10, 0), dt(2026, 3, 10, 10, 0), true, "CI07 saida igual a entrada para vip"),
            Arguments.of(dt(2026, 3, 10, 10, 0), dt(2026, 3, 9, 12, 0), false, "CI08 saida em dia anterior a entrada")
        );
    }

    private static LocalDateTime dt(int ano, int mes, int dia, int hora, int minuto) {
        return LocalDateTime.of(ano, mes, dia, hora, minuto);
    }
}
