package edu.unisabana.dyas.samples.services.client;

import edu.unisabana.dyas.sampleprj.dao.mybatis.mappers.ClienteMapper;
import edu.unisabana.dyas.samples.entities.Cliente;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MyBatisExample {

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
        SqlSessionFactory sessionfact = getSqlSessionFactory();
        SqlSession sqlss = sessionfact.openSession();

        // Obtener el mapper de Cliente generado por MyBatis
        ClienteMapper cm = sqlss.getMapper(ClienteMapper.class);

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

        sqlss.commit();
        sqlss.close();
    }
}
