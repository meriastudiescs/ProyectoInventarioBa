import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Scanner;

public class SistemaInventario {

    public static void main(String[] args) {
        HashMap<String, String> inventario = new HashMap<>();
        HashMap<String, Integer> cantidades = new HashMap<>();
        HashMap<String, Boolean> categorias = new HashMap<>();
        Scanner sc = new Scanner(System.in);

        cargarInventarioDesdeArchivo(inventario, cantidades, categorias);

        boolean continuar = true;

        while (continuar) {
            System.out.println("\n--- Menú Principal ---");
            System.out.println("1. Agregar producto");
            System.out.println("2. Visualizar stock");
            System.out.println("3. Actualizar inventario");
            System.out.println("4. Salir");
            System.out.print("Elija una opción: ");
            int opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    agregarProducto(inventario, cantidades, categorias, sc);
                    guardarInventarioEnArchivo(inventario, cantidades);
                    break;
                case 2:
                    visualizarStock(inventario, cantidades, sc);
                    break;
                case 3:
                    actualizarInventario(inventario, cantidades, sc);
                    guardarInventarioEnArchivo(inventario, cantidades);
                    break;
                case 4:
                    guardarInventarioEnArchivo(inventario, cantidades);
                    continuar = false;
                    System.out.println("Saliendo del sistema... Inventario guardado en archivo.");
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }

            if (continuar) {
                System.out.print("¿Desea realizar otra operación? (sí/no): ");
                String respuesta = sc.nextLine();
                if (!respuesta.equalsIgnoreCase("sí") && !respuesta.equalsIgnoreCase("si")) {
                    guardarInventarioEnArchivo(inventario, cantidades);
                    continuar = false;
                    System.out.println("Saliendo del sistema... Inventario guardado en archivo.");
                }
            }
        }

        sc.close();
    }

    public static void agregarProducto(HashMap<String, String> inventario, HashMap<String, Integer> cantidades,
                                       HashMap<String, Boolean> categorias, Scanner sc) {
        System.out.print("Ingrese el nombre del producto (o 'salir' para terminar): ");
        String producto = sc.nextLine();

        if (producto.equalsIgnoreCase("salir")) {
            return;
        }

        System.out.print("Ingrese la categoría del producto: ");
        String categoria = sc.nextLine();

        if (!categorias.containsKey(categoria)) {
            System.out.print("La categoría no existe. ¿Desea crearla? (sí/si/no): ");
            String respuesta = sc.nextLine();

            if (respuesta.equalsIgnoreCase("sí") || respuesta.equalsIgnoreCase("si")) {
                categorias.put(categoria, true);
                System.out.println("Categoría '" + categoria + "' creada.");
            } else {
                System.out.println("Categoría no creada. Intente nuevamente.");
                return;
            }
        }

        System.out.print("Ingrese la cantidad del producto: ");
        int cantidad = sc.nextInt();
        sc.nextLine();

        inventario.put(producto, categoria);
        cantidades.put(producto, cantidad);

        System.out.println("Producto agregado: " + producto + " - " + categoria + ": " + cantidad);
    }

    public static void visualizarStock(HashMap<String, String> inventario, HashMap<String, Integer> cantidades, Scanner sc) {
        System.out.println("1. Visualizar todo el stock");
        System.out.println("2. Visualizar por categoría");
        System.out.print("Elija una opción: ");
        int opcion = sc.nextInt();
        sc.nextLine();

        switch (opcion) {
            case 1:
                System.out.println("\n--- Stock Total ---");
                for (String producto : inventario.keySet()) {
                    System.out.println(producto + " (" + inventario.get(producto) + "): " + cantidades.get(producto) + " unidades");
                }
                break;
            case 2:
                System.out.print("Ingrese la categoría a visualizar: ");
                String categoriaBuscada = sc.nextLine();
                System.out.println("\n--- Productos en la categoría '" + categoriaBuscada + "' ---");
                boolean categoriaEncontrada = false;

                for (String producto : inventario.keySet()) {
                    if (inventario.get(producto).equalsIgnoreCase(categoriaBuscada)) {
                        System.out.println(producto + ": " + cantidades.get(producto) + " unidades");
                        categoriaEncontrada = true;
                    }
                }

                if (!categoriaEncontrada) {
                    System.out.println("No se encontraron productos en la categoría '" + categoriaBuscada + "'.");
                }
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }


    public static void actualizarInventario(HashMap<String, String> inventario, HashMap<String, Integer> cantidades, Scanner sc) {
        System.out.println("\n--- Actualizar Inventario ---");
        System.out.println("1. Compraron (disminuir stock)");
        System.out.println("2. Llego más stock (aumentar stock)");
        System.out.print("Elija una opción: ");
        int opcion = sc.nextInt();
        sc.nextLine();

        System.out.print("Ingrese el nombre del producto: ");
        String producto = sc.nextLine();

        if (!inventario.containsKey(producto)) {
            System.out.println("El producto '" + producto + "' no existe en el inventario.");
            return;
        }

        System.out.print("Ingrese la cantidad a actualizar: ");
        int cantidad = sc.nextInt();
        sc.nextLine();

        int stockActual = cantidades.get(producto);

        switch (opcion) {
            case 1:
                if (stockActual >= cantidad) {
                    cantidades.put(producto, stockActual - cantidad);
                    System.out.println("Se vendieron " + cantidad + " unidades de " + producto + ". Stock actual: " + (stockActual - cantidad));
                } else {
                    System.out.println("No hay suficiente stock para vender esa cantidad.");
                }
                break;
            case 2:
                cantidades.put(producto, stockActual + cantidad);
                System.out.println("Llegaron " + cantidad + " unidades de " + producto + ". Stock actual: " + (stockActual + cantidad));
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }

    public static void guardarInventarioEnArchivo(HashMap<String, String> inventario, HashMap<String, Integer> cantidades) {
        LocalDateTime fechaHoraActual = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fechaHoraFormateada = fechaHoraActual.format(formato);

        try (FileWriter writer = new FileWriter("inventario.txt")) {
            writer.write("Inventario actualizado el: " + fechaHoraFormateada + "\n\n");
            writer.write("Inventario:\n");
            for (String producto : inventario.keySet()) {
                writer.write(producto + " (" + inventario.get(producto) + "): " + cantidades.get(producto) + " unidades\n");
            }
            System.out.println("Inventario guardado exitosamente en 'inventario.txt'. Fecha y hora: " + fechaHoraFormateada);
        } catch (IOException e) {
            System.out.println("Error al guardar el inventario en el archivo: " + e.getMessage());
        }
    }

    public static void cargarInventarioDesdeArchivo(HashMap<String, String> inventario, HashMap<String, Integer> cantidades, HashMap<String, Boolean> categorias) {
        File archivo = new File("inventario.txt");
        if (!archivo.exists()) {
            System.out.println("El archivo 'inventario.txt' no existe, se creará uno nuevo al guardar.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;

            reader.readLine();

            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] partes = linea.split(":");

                if (partes.length < 2) {
                    System.out.println("Línea inválida, omitiendo: " + linea);
                    continue;
                }

                String productoYCategoria = partes[0].trim();

                String cantidadStr = partes[1].trim().replace(" unidades", "");
                int cantidad;
                try {
                    cantidad = Integer.parseInt(cantidadStr);
                } catch (NumberFormatException e) {
                    System.out.println("Formato inválido en la línea: " + linea);
                    continue;
                }

                String producto = productoYCategoria.split("\\(")[0].trim();
                String categoria = productoYCategoria.split("\\(")[1].replace(")", "").trim();

                inventario.put(producto, categoria);
                cantidades.put(producto, cantidad);
                categorias.put(categoria, true);
            }
            System.out.println("Inventario cargado desde 'inventario.txt'.");
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de inventario: " + e.getMessage());
        }
    }

}
