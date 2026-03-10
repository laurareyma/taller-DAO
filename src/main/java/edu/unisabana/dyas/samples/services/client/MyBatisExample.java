package edu.unisabana.dyas.samples.services.client;

import edu.unisabana.dyas.sampleprj.dao.mybatis.mappers.ClienteMapper;
import edu.unisabana.dyas.sampleprj.dao.mybatis.mappers.ItemMapper;
import edu.unisabana.dyas.samples.entities.Cliente;
import edu.unisabana.dyas.samples.entities.Item;
import edu.unisabana.dyas.samples.entities.TipoItem;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MyBatisExample {

    private static final String WRITE_DEMO_FLAG = "--demo-write";

    /**
     * Construye una fábrica de sesiones de MyBatis a partir del archivo
     * de configuración ubicado en src/main/resources
     */
    public static SqlSessionFactory getSqlSessionFactory() {
        SqlSessionFactory sqlSessionFactory = null;
        if (sqlSessionFactory == null) {
            InputStream inputStream;
            try {
                inputStream = Resources.getResourceAsStream("mybatis-config.xml");
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            } catch (IOException e) {
                throw new RuntimeException(e.getCause());
            }
        }
        return sqlSessionFactory;
    }

    public static void main(String args[]) throws SQLException {
        boolean runWriteDemo = args != null && args.length > 0 && WRITE_DEMO_FLAG.equals(args[0]);
        SqlSessionFactory sessionfact = getSqlSessionFactory();
        SqlSession sqlss = sessionfact.openSession();

        // Obtener el mapper de Cliente generado por MyBatis
        ClienteMapper cm = sqlss.getMapper(ClienteMapper.class);
        ItemMapper im = sqlss.getMapper(ItemMapper.class);

        // --- Consultar TODOS los clientes con sus items rentados ---
        System.out.println("=== Lista de todos los clientes ===");
        List<Cliente> clientes = cm.consultarClientes();
        for (Cliente c : clientes) {
            System.out.println(c);
        }

        // --- Consultar UN cliente por documento ---
        System.out.println("\n=== Consulta de cliente individual (documento: 123456789) ===");
        Cliente clienteUno = cm.consultarCliente(123456789);
        System.out.println(clienteUno);

        if (runWriteDemo) {
            // --- Insertar y consultar un item ---
            System.out.println("\n=== Insertar/consultar Item ===");
            int nuevoItemId = (int) (System.currentTimeMillis() % 100000000);
            TipoItem tipo = new TipoItem(1, "Electrónico");
            Item nuevoItem = new Item();
            nuevoItem.setId(nuevoItemId);
            nuevoItem.setNombre("Item Demo " + nuevoItemId);
            nuevoItem.setDescripcion("Item de prueba para validar mapper");
            nuevoItem.setFechaLanzamiento(new java.util.Date());
            nuevoItem.setTarifaxDia(3500);
            nuevoItem.setFormatoRenta("DIARIO");
            nuevoItem.setGenero("DEMO");
            nuevoItem.setTipo(tipo);

            im.insertarItem(nuevoItem);
            Item itemConsultado = im.consultarItem(nuevoItemId);
            System.out.println(itemConsultado);

            // --- Consultar todos los items ---
            List<Item> items = im.consultarItems();
            System.out.println("Total items: " + items.size());

            // --- Agregar item rentado al cliente ---
            Date hoy = new Date(System.currentTimeMillis());
            Date manana = new Date(System.currentTimeMillis() + 86400000L);
            cm.agregarItemRentadoACliente(123456789, nuevoItemId, hoy, manana);

            // Volver a consultar cliente para validar relación agregada
            Cliente clienteActualizado = cm.consultarCliente(123456789);
            System.out.println("\n=== Cliente actualizado ===");
            System.out.println(clienteActualizado);
        } else {
            System.out.println("\nModo solo lectura: no se realizaron inserciones/actualizaciones.");
            System.out.println("Para ejecutar la demo completa con escrituras use: --demo-write");
        }

        sqlss.commit();
        sqlss.close();
    }
}
