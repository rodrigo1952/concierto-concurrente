public class Main {

    public static void main(String[] args) {

        // Creamos un sistema con 10 entradas.
        VentaEntradas venta = new VentaEntradas(10);

        // Creamos tres compradores.
        // Todos utilizan el mismo objeto venta.
        Comprador comprador1 = new Comprador(
            "Comprador 1", venta
        );

        Comprador comprador2 = new Comprador(
            "Comprador 2", venta
        );

        Comprador comprador3 = new Comprador(
            "Comprador 3", venta
        );

        System.out.println(
            "INICIO DE VENTA DE ENTRADAS"
        );

        // Iniciamos los tres hilos.
        comprador1.start();
        comprador2.start();
        comprador3.start();

        try {

            // Esperamos a que todos finalicen.
            comprador1.join();
            comprador2.join();
            comprador3.join();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            System.out.println("Proceso interrumpido");
            return;
        }

        // Sumamos las compras de los tres hilos.
        int totalCompras =
            comprador1.getComprasExitosas()
            + comprador2.getComprasExitosas()
            + comprador3.getComprasExitosas();

        // Mostramos los resultados finales.
        System.out.println("\nVENTA FINALIZADA");

        System.out.println(
            "Entradas vendidas: "
            + venta.getEntradasVendidas()
        );

        System.out.println(
            "Entradas disponibles: "
            + venta.getEntradasDisponibles()
        );

        System.out.println(
            "Total registrado por compradores: "
            + totalCompras
        );
    }
}
