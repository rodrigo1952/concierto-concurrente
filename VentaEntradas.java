// Clase que administra las entradas del concierto.
// Es el RECURSO COMPARTIDO por todos los compradores.
public class VentaEntradas {

    // Cantidad de entradas disponibles para la venta.
    private int entradasDisponibles;

    // Entradas vendidas netas (compras menos cancelaciones).
    private int entradasVendidas;

    // Total de compras realizadas, incluidas las que luego se cancelaron.
    private int comprasRealizadas;

    // Total de entradas devueltas mediante cancelación.
    private int entradasCanceladas;

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
        this.comprasRealizadas = 0;
        this.entradasCanceladas = 0;
    }

    // Compra una entrada.
    // synchronized: solo un hilo a la vez puede comprobar
    // el stock y descontarlo sobre el mismo objeto.
    public synchronized boolean comprarEntrada() {

        // Si no hay entradas, la compra no se realiza.
        if (entradasDisponibles == 0) {
            return false;
        }

        entradasDisponibles--;
        entradasVendidas++;
        comprasRealizadas++;

        return true;
    }

    // Cancela una compra: la entrada vuelve al stock.
    // synchronized: evita que una cancelación y una compra
    // modifiquen el stock al mismo tiempo.
    public synchronized boolean cancelarCompra() {

        // No se puede cancelar si no hay entradas vendidas.
        if (entradasVendidas == 0) {
            return false;
        }

        entradasVendidas--;
        entradasDisponibles++;
        entradasCanceladas++;

        return true;
    }

    // Consulta segura del stock disponible.
    public synchronized int getEntradasDisponibles() {
        return entradasDisponibles;
    }

    // Consulta segura de las ventas netas.
    public synchronized int getEntradasVendidas() {
        return entradasVendidas;
    }

    // Consulta segura del total de compras realizadas.
    public synchronized int getComprasRealizadas() {
        return comprasRealizadas;
    }

    // Consulta segura del total de entradas canceladas.
    public synchronized int getEntradasCanceladas() {
        return entradasCanceladas;
    }
}
