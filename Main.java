public class Main {

    public static void main(String[] args) {

        // ===== CONFIGURACIÓN (antes de iniciar la simulación) =====

        // El sistema inicia con 6 entradas.
        final int ENTRADAS_INICIALES = 6;

        // Cantidades de cada comprador (posición 0 = Comprador 1, etc.).
        int[] compraInicial = {3, 2, 1};   // Fase 1
        int[] devolucion    = {2, 1, 0};   // Fase 2
        int[] segundaCompra = {1, 1, 1};   // Fase 3

        // Recurso compartido.
        VentaEntradas venta = new VentaEntradas(ENTRADAS_INICIALES);

        // Tres compradores que comparten el mismo objeto venta.
        Comprador[] compradores = new Comprador[3];

        for (int i = 0; i < compradores.length; i++) {
            compradores[i] = new Comprador(
                "Comprador " + (i + 1),
                venta,
                compraInicial[i],
                devolucion[i],
                segundaCompra[i]
            );
        }

        // ===== SIMULACIÓN =====

        System.out.println(
            "INICIO DE LA SIMULACIÓN - Entradas disponibles: "
            + venta.getEntradasDisponibles()
        );

        try {
            ejecutarFase(Comprador.FASE_COMPRA_INICIAL,
                "FASE 1: COMPRAS INICIALES", venta, compradores);

            ejecutarFase(Comprador.FASE_CANCELACION,
                "FASE 2: CANCELACIONES", venta, compradores);

            ejecutarFase(Comprador.FASE_SEGUNDA_COMPRA,
                "FASE 3: SEGUNDA COMPRA", venta, compradores);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Proceso interrumpido");
            return;
        }

        mostrarResumenFinal(venta, compradores);
    }

    // Ejecuta una fase: crea un hilo nuevo por comprador,
    // espera a que todos terminen y muestra el estado.
    public static void ejecutarFase(int fase, String titulo,
                                    VentaEntradas venta,
                                    Comprador[] compradores)
            throws InterruptedException {

        System.out.println("\n=== " + titulo + " ===");

        Thread[] hilos = new Thread[compradores.length];

        for (int i = 0; i < compradores.length; i++) {
            compradores[i].setFase(fase);
            hilos[i] = new Thread(compradores[i]);
        }

        // Iniciamos los hilos de esta fase.
        for (Thread hilo : hilos) {
            hilo.start();
        }

        // Esperamos a que todos terminen antes de pasar a la siguiente fase.
        for (Thread hilo : hilos) {
            hilo.join();
        }

        System.out.println(
            "-> Stock disponible: " + venta.getEntradasDisponibles()
            + " | Ventas netas: " + venta.getEntradasVendidas()
        );
    }

    // Muestra los resultados al finalizar la simulación.
    private static void mostrarResumenFinal(VentaEntradas venta,
                                            Comprador[] compradores) {

        System.out.println("\n=== VENTA FINALIZADA ===");
        System.out.println("Compras realizadas: " + venta.getComprasRealizadas());
        System.out.println("Entradas canceladas: " + venta.getEntradasCanceladas());
        System.out.println("Entradas vendidas netas: " + venta.getEntradasVendidas());
        System.out.println("Entradas disponibles: " + venta.getEntradasDisponibles());

        System.out.println("\nDetalle por comprador:");

        boolean nadieSuperaMaximo = true;

        for (Comprador comprador : compradores) {
            System.out.println(
                comprador.getNombre()
                + " -> adquiridas: " + comprador.getEntradasAdquiridas()
                + ", devueltas: " + comprador.getEntradasDevueltas()
                + ", conserva: " + comprador.getEntradasConservadas()
            );

            if (comprador.getEntradasAdquiridas() > Comprador.MAXIMO_ENTRADAS) {
                nadieSuperaMaximo = false;
            }
        }

        System.out.println(
            "\nNingún comprador superó las " + Comprador.MAXIMO_ENTRADAS
            + " entradas: " + (nadieSuperaMaximo ? "SÍ" : "NO")
        );
    }
}
