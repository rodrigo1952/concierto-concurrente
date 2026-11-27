// Clase para ejecutar pruebas automatizadas (sin JUnit).
public class PruebaVentaEntradas {

    public static void main(String[] args) throws InterruptedException {

        // PRUEBA 1: no se vende cuando el stock es cero.
        VentaEntradas ventaVacia = new VentaEntradas(0);
        verificar(!ventaVacia.comprarEntrada(),
            "se vendió una entrada sin stock");
        System.out.println("PRUEBA 1: OK (no vende sin stock)");

        // PRUEBA 2: no se aceptan stocks negativos.
        boolean errorDetectado = false;
        try {
            new VentaEntradas(-1);
        } catch (IllegalArgumentException e) {
            errorDetectado = true;
        }
        verificar(errorDetectado, "se permitió un stock negativo");
        System.out.println("PRUEBA 2: OK (rechaza stock negativo)");

        // PRUEBA 3: cancelarCompra() no funciona sin ventas
        // y devuelve la entrada al stock cuando sí hay ventas.
        VentaEntradas venta3 = new VentaEntradas(1);
        verificar(!venta3.cancelarCompra(),
            "se canceló una compra sin ventas");
        venta3.comprarEntrada();
        verificar(venta3.cancelarCompra(), "no se pudo cancelar una venta");
        verificar(venta3.getEntradasDisponibles() == 1
            && venta3.getEntradasVendidas() == 0,
            "la cancelación no devolvió la entrada al stock");
        System.out.println("PRUEBA 3: OK (cancelarCompra)");

        // PRUEBA 4: no se puede devolver más de lo que se conserva.
        // Compra 2 e intenta devolver 5: solo debe devolver 2.
        VentaEntradas venta4 = new VentaEntradas(6);
        Comprador c4 = new Comprador("Prueba 4", venta4, 2, 5, 0);
        ejecutar(c4, Comprador.FASE_COMPRA_INICIAL);
        ejecutar(c4, Comprador.FASE_CANCELACION);
        verificar(c4.getEntradasDevueltas() == 2
            && c4.getEntradasConservadas() == 0
            && venta4.getEntradasDisponibles() == 6,
            "se devolvieron más entradas de las que se conservaban");
        System.out.println("PRUEBA 4: OK (no devuelve más de lo que conserva)");

        // PRUEBA 5: no se pueden devolver las mismas entradas dos veces.
        // Compra 1, la devuelve y vuelve a intentar devolver.
        VentaEntradas venta5 = new VentaEntradas(6);
        Comprador c5 = new Comprador("Prueba 5", venta5, 1, 1, 0);
        ejecutar(c5, Comprador.FASE_COMPRA_INICIAL);
        ejecutar(c5, Comprador.FASE_CANCELACION);
        ejecutar(c5, Comprador.FASE_CANCELACION);
        verificar(c5.getEntradasDevueltas() == 1
            && venta5.getEntradasCanceladas() == 1,
            "una misma entrada se devolvió dos veces");
        System.out.println("PRUEBA 5: OK (no devuelve dos veces)");

        // PRUEBA 6: máximo 4 entradas, contando las devueltas.
        // Compra 3, devuelve 2 e intenta comprar 3 más: solo puede 1.
        VentaEntradas venta6 = new VentaEntradas(10);
        Comprador c6 = new Comprador("Prueba 6", venta6, 3, 2, 3);
        ejecutar(c6, Comprador.FASE_COMPRA_INICIAL);
        ejecutar(c6, Comprador.FASE_CANCELACION);
        ejecutar(c6, Comprador.FASE_SEGUNDA_COMPRA);
        verificar(c6.getEntradasAdquiridas() == 4,
            "un comprador superó el máximo de 4 entradas");
        System.out.println("PRUEBA 6: OK (máximo 4 entradas)");

        // PRUEBA 7: simulación completa del enunciado, repetida 10 veces
        // con hilos concurrentes. El resultado debe ser siempre el mismo.
        for (int intento = 1; intento <= 10; intento++) {

            VentaEntradas venta = new VentaEntradas(6);
            Comprador[] compradores = {
                new Comprador("Comprador 1", venta, 3, 2, 1),
                new Comprador("Comprador 2", venta, 2, 1, 1),
                new Comprador("Comprador 3", venta, 1, 0, 1)
            };

            Main.ejecutarFase(Comprador.FASE_COMPRA_INICIAL,
                "FASE 1 (prueba " + intento + ")", venta, compradores);
            Main.ejecutarFase(Comprador.FASE_CANCELACION,
                "FASE 2 (prueba " + intento + ")", venta, compradores);
            Main.ejecutarFase(Comprador.FASE_SEGUNDA_COMPRA,
                "FASE 3 (prueba " + intento + ")", venta, compradores);

            verificar(venta.getComprasRealizadas() == 9
                && venta.getEntradasCanceladas() == 3
                && venta.getEntradasVendidas() == 6
                && venta.getEntradasDisponibles() == 0,
                "resultado final incorrecto en la simulación " + intento);

            for (Comprador comprador : compradores) {
                verificar(comprador.getEntradasAdquiridas()
                    <= Comprador.MAXIMO_ENTRADAS,
                    comprador.getNombre() + " superó el máximo");
            }
        }
        System.out.println("PRUEBA 7: OK (simulación completa: 9 / 3 / 6 / 0)");

        // PRUEBA 8: 10 hilos compran y cancelan al mismo tiempo
        // sobre el mismo objeto. Gracias a synchronized el stock
        // nunca se descuadra.
        VentaEntradas venta8 = new VentaEntradas(6);
        Thread[] hilos = new Thread[10];

        for (int i = 0; i < hilos.length; i++) {
            hilos[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    if (venta8.comprarEntrada()) {
                        venta8.cancelarCompra();
                    }
                }
            });
        }
        for (Thread hilo : hilos) {
            hilo.start();
        }
        for (Thread hilo : hilos) {
            hilo.join();
        }
        verificar(venta8.getEntradasDisponibles() == 6
            && venta8.getEntradasVendidas() == 0
            && venta8.getComprasRealizadas() == venta8.getEntradasCanceladas(),
            "el stock se descuadró con compras y cancelaciones simultáneas");
        System.out.println("PRUEBA 8: OK (compras y cancelaciones simultáneas)");

        System.out.println("\nTODAS LAS PRUEBAS FUERON SUPERADAS");
    }

    // Ejecuta una fase de un comprador en el hilo actual.
    private static void ejecutar(Comprador comprador, int fase) {
        comprador.setFase(fase);
        comprador.run();
    }

    // Lanza AssertionError si la condición no se cumple.
    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError("ERROR: " + mensaje);
        }
    }
}
