// Clase para ejecutar pruebas automatizadas.
public class PruebaVentaEntradas {

    public static void main(String[] args)
            throws InterruptedException {

        // PRUEBA 1:
        // Comprobar que no se vende cuando el stock es cero.
        VentaEntradas ventaVacia = new VentaEntradas(0);

        if (ventaVacia.comprarEntrada()) {
            throw new AssertionError(
                "ERROR: se vendió una entrada sin stock"
            );
        }

        System.out.println("PRUEBA 1: OK");


        // PRUEBA 2:
        // Comprobar que no se aceptan stocks negativos.
        boolean errorDetectado = false;

        try {
            new VentaEntradas(-1);

        } catch (IllegalArgumentException e) {
            errorDetectado = true;
        }

        if (!errorDetectado) {
            throw new AssertionError(
                "ERROR: se permitió un stock negativo"
            );
        }

        System.out.println("PRUEBA 2: OK");


        // PRUEBA 3:
        // Repetimos la simulación 20 veces
        // para comprobar el comportamiento concurrente.
        for (int intento = 1; intento <= 20; intento++) {

            // Creamos una nueva venta con 10 entradas.
            VentaEntradas venta = new VentaEntradas(10);

            // Creamos 15 compradores concurrentes.
            Comprador[] compradores = new Comprador[15];

            for (int i = 0; i < compradores.length; i++) {

                compradores[i] = new Comprador(
                    "Cliente " + (i + 1),
                    venta
                );
            }

            // Iniciamos todos los compradores.
            for (Comprador comprador : compradores) {
                comprador.start();
            }

            // Esperamos a que todos terminen.
            for (Comprador comprador : compradores) {
                comprador.join();
            }

            // Sumamos las compras realizadas.
            int totalCompras = 0;

            for (Comprador comprador : compradores) {
                totalCompras += comprador.getComprasExitosas();
            }

            // Verificamos las reglas del sistema.
            if (
                venta.getEntradasVendidas() != 10
                || venta.getEntradasDisponibles() != 0
                || totalCompras != 10
            ) {

                throw new AssertionError(
                    "ERROR en simulación " + intento
                );
            }
        }

        System.out.println("PRUEBA 3: OK");

        System.out.println(
            "\nTODAS LAS PRUEBAS FUERON SUPERADAS"
        );
    }
}
