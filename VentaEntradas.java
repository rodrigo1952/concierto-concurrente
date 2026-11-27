// Clase que administra las entradas del concierto.
public class VentaEntradas {

    // Cantidad de entradas disponibles.
    private int entradasDisponibles;

    // Cantidad de entradas vendidas.
    private int entradasVendidas;

    // Constructor.
    public VentaEntradas(int cantidad) {

        // Evitamos crear un sistema con stock negativo.
        if (cantidad < 0) {
            throw new IllegalArgumentException(
                "La cantidad no puede ser negativa"
            );
        }

        this.entradasDisponibles = cantidad;
        this.entradasVendidas = 0;
    }

    // Método sincronizado para comprar una entrada.
    // Solo un hilo puede ejecutar este método
    // a la vez sobre el mismo objeto VentaEntradas.
    public synchronized boolean comprarEntrada() {

        // Si no hay entradas, la compra no se realiza.
        if (entradasDisponibles == 0) {
            return false;
        }

        // Descontamos una entrada disponible.
        entradasDisponibles--;

        // Registramos una venta exitosa.
        entradasVendidas++;

        return true;
    }

    // Consulta segura del stock disponible.
    public synchronized int getEntradasDisponibles() {
        return entradasDisponibles;
    }

    // Consulta segura de las entradas vendidas.
    public synchronized int getEntradasVendidas() {
        return entradasVendidas;
    }
}
