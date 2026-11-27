// Cada comprador compra y devuelve entradas.
// Implementa Runnable para ejecutarse en un HILO NUEVO en cada fase,
// porque un mismo Thread no se puede iniciar dos veces.
public class Comprador implements Runnable {

    // Máximo de entradas que un comprador puede adquirir
    // en toda la simulación (cuentan también las devueltas).
    public static final int MAXIMO_ENTRADAS = 4;

    // Fases de la simulación.
    public static final int FASE_COMPRA_INICIAL = 1;
    public static final int FASE_CANCELACION = 2;
    public static final int FASE_SEGUNDA_COMPRA = 3;

    // Nombre del comprador.
    private String nombre;

    // Todos los compradores comparten el mismo objeto VentaEntradas.
    private VentaEntradas venta;

    // Cantidades planificadas (se definen en Main antes de iniciar).
    private int compraInicial;
    private int devolucion;
    private int segundaCompra;

    // Total de entradas compradas, incluidas las que luego devolvió.
    private int entradasAdquiridas;

    // Total de entradas devueltas.
    private int entradasDevueltas;

    // Entradas que conserva actualmente.
    private int entradasConservadas;

    // Fase que ejecutará el próximo hilo.
    private int faseActual;

    // Constructor.
    public Comprador(String nombre, VentaEntradas venta,
                     int compraInicial, int devolucion, int segundaCompra) {

        // No se aceptan cantidades negativas.
        if (compraInicial < 0 || devolucion < 0 || segundaCompra < 0) {
            throw new IllegalArgumentException(
                "Las cantidades no pueden ser negativas"
            );
        }

        this.nombre = nombre;
        this.venta = venta;
        this.compraInicial = compraInicial;
        this.devolucion = devolucion;
        this.segundaCompra = segundaCompra;
        this.entradasAdquiridas = 0;
        this.entradasDevueltas = 0;
        this.entradasConservadas = 0;
    }

    // Indica qué fase realizará el comprador en su próximo hilo.
    public void setFase(int fase) {
        this.faseActual = fase;
    }

    // Método ejecutado por el hilo cuando se llama a start().
    @Override
    public void run() {

        switch (faseActual) {
            case FASE_COMPRA_INICIAL:
                comprar(compraInicial);
                break;
            case FASE_CANCELACION:
                devolver(devolucion);
                break;
            case FASE_SEGUNDA_COMPRA:
                comprar(segundaCompra);
                break;
            default:
                System.out.println(nombre + ": fase no válida");
        }
    }

    // Intenta comprar la cantidad indicada de entradas.
    private void comprar(int cantidad) {

        for (int i = 0; i < cantidad; i++) {

            // Regla: máximo 4 entradas en toda la simulación.
            // entradasAdquiridas no baja al devolver, por eso
            // también cuenta las entradas devueltas.
            if (entradasAdquiridas >= MAXIMO_ENTRADAS) {
                System.out.println(
                    nombre + " no puede comprar más: alcanzó el máximo de "
                    + MAXIMO_ENTRADAS + " entradas"
                );
                return;
            }

            // La compra se hace en el recurso compartido (synchronized).
            if (!venta.comprarEntrada()) {
                System.out.println(
                    nombre + " no pudo comprar: entradas agotadas"
                );
                return;
            }

            entradasAdquiridas++;
            entradasConservadas++;

            System.out.println(
                nombre + " compró una entrada (adquiridas: "
                + entradasAdquiridas + ")"
            );

            if (!pausar()) {
                return;
            }
        }
    }

    // Intenta devolver la cantidad indicada de entradas.
    private void devolver(int cantidad) {

        if (cantidad == 0) {
            System.out.println(nombre + " no realiza devoluciones");
            return;
        }

        for (int i = 0; i < cantidad; i++) {

            // Regla: no se puede devolver más de lo que conserva.
            // Cada devolución resta una entrada conservada, así una
            // misma entrada no puede devolverse dos veces.
            if (entradasConservadas == 0) {
                System.out.println(
                    nombre + " no puede devolver: no conserva entradas"
                );
                return;
            }

            // La cancelación se hace en el recurso compartido (synchronized).
            if (!venta.cancelarCompra()) {
                System.out.println(
                    nombre + " no pudo cancelar la compra"
                );
                return;
            }

            entradasConservadas--;
            entradasDevueltas++;

            System.out.println(
                nombre + " devolvió una entrada (conserva: "
                + entradasConservadas + ")"
            );

            if (!pausar()) {
                return;
            }
        }
    }

    // Pequeña pausa para ver cómo se intercalan los hilos.
    // Devuelve false si el hilo fue interrumpido.
    private boolean pausar() {
        try {
            Thread.sleep(100);
            return true;
        } catch (InterruptedException e) {
            // Restauramos el estado de interrupción.
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public String getNombre() {
        return nombre;
    }

    public int getEntradasAdquiridas() {
        return entradasAdquiridas;
    }

    public int getEntradasDevueltas() {
        return entradasDevueltas;
    }

    public int getEntradasConservadas() {
        return entradasConservadas;
    }
}
