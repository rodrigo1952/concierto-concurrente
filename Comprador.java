// Cada comprador se ejecutará como un hilo.
public class Comprador extends Thread {

    // Nombre del comprador.
    private String nombre;

    // Todos los compradores compartirán
    // el mismo objeto VentaEntradas.
    private VentaEntradas venta;

    // Contador individual de compras exitosas.
    private int comprasExitosas;

    // Constructor.
    public Comprador(String nombre, VentaEntradas venta) {

        // Inicializamos la clase padre Thread.
        super();

        this.nombre = nombre;
        this.venta = venta;
        this.comprasExitosas = 0;
    }

    // Método ejecutado cuando llamamos a start().
    @Override
    public void run() {

        // El comprador intenta adquirir entradas
        // mientras el sistema permita venderlas.
        while (venta.comprarEntrada()) {

            // Registramos una compra exitosa.
            comprasExitosas++;

            System.out.println(
                nombre + " compró una entrada"
            );

            try {

                // Pequeña pausa para visualizar
                // cómo se intercalan los compradores.
                Thread.sleep(100);

            } catch (InterruptedException e) {

                // Restauramos el estado de interrupción.
                Thread.currentThread().interrupt();

                // Terminamos el hilo si es interrumpido.
                break;
            }
        }

        System.out.println(
            nombre + " finalizó. Compras realizadas: "
            + comprasExitosas
        );
    }

    // Devuelve las compras realizadas por este hilo.
    public int getComprasExitosas() {
        return comprasExitosas;
    }
}
