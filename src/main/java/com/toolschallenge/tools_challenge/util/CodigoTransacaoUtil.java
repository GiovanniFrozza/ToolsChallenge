package com.toolschallenge.tools_challenge.util;

import java.util.concurrent.ThreadLocalRandom;

public final class CodigoTransacaoUtil {

    private CodigoTransacaoUtil() {
    }

    public static String gerarNsu() {
        return String.format("%010d", ThreadLocalRandom.current().nextLong(0, 10_000_000_000L));
    }

    public static String gerarCodigoAutorizacao() {
        return String.format("%09d", ThreadLocalRandom.current().nextInt(0, 1_000_000_000));
    }
}
