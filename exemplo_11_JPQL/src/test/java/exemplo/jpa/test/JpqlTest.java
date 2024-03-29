package exemplo.jpa.test;

import exemplo.jpa.CartaoCredito;
import exemplo.jpa.Categoria;
import exemplo.jpa.Comprador;
import exemplo.jpa.DatasLimite;
import exemplo.jpa.Item;
import exemplo.jpa.Oferta;
import exemplo.jpa.Reputacao;
import exemplo.jpa.Usuario;
import exemplo.jpa.Vendedor;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

/**
 *
 * @author MASC
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JpqlTest {

    private static EntityManagerFactory emf;
    private static final Logger logger = Logger.getGlobal();
    private EntityManager em;
    private EntityTransaction et;

    public JpqlTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        logger.setLevel(Level.INFO);
        emf = Persistence.createEntityManagerFactory("exemplo_11");
        DbUnitUtil.inserirDados();
    }

    @AfterClass
    public static void tearDownClass() {
        emf.close();
    }

    @Before
    public void setUp() {
        em = emf.createEntityManager();
        et = em.getTransaction();
        et.begin();
    }

    @After
    public void tearDown() {
        try {
            et.commit();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            et.rollback();
        } finally {
            em.close();
        }
    }

    @Test
    public void t01_categoriaPorNome() {
        logger.info("Executando t01: SELECT c FROM Categoria c WHERE c.nome LIKE :nome ORDER BY c.id");
        TypedQuery<Categoria> query = em.createQuery(
                "SELECT c FROM Categoria c WHERE c.nome LIKE :nome ORDER BY c.id",
                Categoria.class);
        query.setParameter("nome", "Instrumentos%");
        List<Categoria> categorias = query.getResultList();

        for (Categoria categoria : categorias) {
            assertTrue(categoria.getNome().startsWith("Instrumentos"));
        }
    }

    @Test
    public void t02_categoriaPorNome() {
        logger.info("Executando t02: Categoria.PorNome");
        TypedQuery<Categoria> query = em.createNamedQuery("Categoria.PorNome", Categoria.class);
        query.setParameter("nome", "Instrumentos%");
        List<Categoria> categorias = query.getResultList();

        for (Categoria categoria : categorias) {
            assertTrue(categoria.getNome().startsWith("Instrumentos"));
        }
    }

    @Test
    public void t03_quantidadeCategoriasFilhas() {
        logger.info("Executando t03: SELECT COUNT(c) FROM Categoria c WHERE c.mae IS NOT NULL");
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(c) FROM Categoria c WHERE c.mae IS NOT NULL", Long.class);
        Long resultado = query.getSingleResult();
        assertEquals(new Long(3), resultado);
    }

    @Test
    public void t04_maximaEMinimaDataNascimento() {
        logger.info("Executando t04: SELECT MAX(c.dataNascimento), MIN(c.dataNascimento) FROM Comprador c");
        Query query = em.createQuery(
                "SELECT MAX(c.dataNascimento), MIN(c.dataNascimento) FROM Comprador c");
        Object[] resultado = (Object[]) query.getSingleResult();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String maiorData = dateFormat.format((Date) resultado[0]);
        String menorData = dateFormat.format((Date) resultado[1]);
        assertEquals("21-12-1999", maiorData);
        assertEquals("11-08-1973", menorData);
    }

    @Test
    public void t05_maximaEMinimaDataNascimento() {
        logger.info("Executando t05: SELECT NEW exemplo.jpa.DatasLimite(MAX(c.dataNascimento), MIN(c.dataNascimento)) FROM Comprador c");
        TypedQuery<DatasLimite> query = em.createQuery(
                "SELECT NEW exemplo.jpa.DatasLimite(MAX(c.dataNascimento), MIN(c.dataNascimento)) FROM Comprador c",
                DatasLimite.class);
        DatasLimite datasLimite = query.getSingleResult();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dataMaxima = dateFormat.format((Date) datasLimite.getDataMaxima());
        String dataMinima = dateFormat.format((Date) datasLimite.getDataMinima());
        assertEquals("21-12-1999", dataMaxima);
        assertEquals("11-08-1973", dataMinima);
    }

    @Test
    public void t06_categoriasMaes() {
        logger.info("Executando t06: SELECT c FROM Categoria c WHERE c.filhas IS NOT EMPTY");
        TypedQuery<Categoria> query;
        query = em.createQuery(
                "SELECT c FROM Categoria c WHERE c.filhas IS NOT EMPTY",
                Categoria.class);
        List<Categoria> categorias = query.getResultList();

        for (Categoria categoria : categorias) {
            assertTrue(!categoria.getFilhas().isEmpty());
        }
    }

    @Test
    public void t07_compradoresVisa() {
        logger.info("Executando t07: SELECT c FROM Comprador c WHERE c.cartaoCredito.bandeira like ?1 ORDER BY c.nome DESC");
        TypedQuery<Comprador> query;
        query = em.createQuery(
                "SELECT c FROM Comprador c WHERE c.cartaoCredito.bandeira like ?1 ORDER BY c.nome DESC",
                Comprador.class);
        query.setParameter(1, "VISA"); //Setando parâmetro posicional.
        query.setMaxResults(20); //Determinando quantidade máxima de resultados.
        List<Comprador> compradores = query.getResultList();

        for (Comprador comprador : compradores) {
            assertEquals("VISA", comprador.getCartaoCredito().getBandeira());

        }
    }

    @Test
    public void t08_compradoresVisaMastercard() {
        logger.info("Executando t08: SELECT c FROM Comprador c WHERE c.cartaoCredito.bandeira LIKE ?1 OR c.cartaoCredito.bandeira LIKE ?2 ORDER BY c.nome DESC");
        TypedQuery<Comprador> query;
        query = em.createQuery(
                "SELECT c FROM Comprador c "
                + "WHERE c.cartaoCredito.bandeira LIKE ?1 "
                + "OR c.cartaoCredito.bandeira LIKE ?2 ORDER BY c.nome DESC",
                Comprador.class);
        query.setParameter(1, "VISA"); //Setando parâmetro posicional.
        query.setParameter(2, "MASTERCARD"); //Setando parâmetro posicional.        
        List<Comprador> compradores = query.getResultList();

        for (Comprador comprador : compradores) {
            switch (comprador.getCartaoCredito().getBandeira()) {
                case "VISA":
                    assertTrue(true);
                    break;
                case "MASTERCARD":
                    assertTrue(true);
                    break;
                default:
                    assertTrue(false);
                    break;
            }
        }
    }

    @Test
    public void t09_compradoresMastercardMaestro() {
        logger.info("Executando t09: SELECT c FROM Comprador c WHERE c.cartaoCredito.bandeira IN ('MAESTRO', 'MASTERCARD') ORDER BY c.nome DESC");
        TypedQuery<Comprador> query;
        query = em.createQuery(
                "SELECT c FROM Comprador c "
                + "WHERE c.cartaoCredito.bandeira IN ('MAESTRO', 'MASTERCARD') ORDER BY c.nome DESC",
                Comprador.class);
        List<Comprador> compradores = query.getResultList();

        for (Comprador comprador : compradores) {
            switch (comprador.getCartaoCredito().getBandeira()) {
                case "MAESTRO":
                    assertTrue(true);
                    break;
                case "MASTERCARD":
                    assertTrue(true);
                    break;
                default:
                    assertTrue(false);
                    break;
            }
        }
    }

    @Test
    public void t10_usuariosPorDataNascimento() {
        logger.info("Executando t10: SELECT u FROM Usuario u WHERE u.dataNascimento BETWEEN ?1 AND ?2");
        TypedQuery<Usuario> query;
        query = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.dataNascimento BETWEEN ?1 AND ?2",
                Usuario.class);
        query.setParameter(1, getData(21, Calendar.FEBRUARY, 1980));
        query.setParameter(2, getData(1, Calendar.DECEMBER, 1990));
        List<Usuario> usuarios = query.getResultList();

        for (Usuario usuario : usuarios) {
            System.out.println(usuario);
        }
    }

    private Date getData(Integer dia, Integer mes, Integer ano) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, ano);
        c.set(Calendar.MONTH, mes);
        c.set(Calendar.DAY_OF_MONTH, dia);
        return c.getTime();
    }

    @Test
    public void t11_categoriaMaePorFilha() {
        logger.info("Executando t11: SELECT c FROM Categoria c WHERE :categoria MEMBER OF c.filhas");
        Categoria categoria = em.find(Categoria.class, new Long(2));
        TypedQuery<Categoria> query;
        query = em.createQuery(
                "SELECT c FROM Categoria c WHERE :categoria MEMBER OF c.filhas",
                Categoria.class);
        query.setParameter("categoria", categoria);
        categoria = query.getSingleResult();
        assertEquals("Instrumentos Musicais", categoria.getNome());
    }

    /**
     * Consulta por cartões de crédito expirados.
     * JPQL: SELECT c FROM CartaoCredito c WHERE c.dataExpiracao < CURRENT_DATE
     */
    @Test
    public void t12_cartoesExpirados() {
        logger.info("Executando t12: SELECT c FROM CartaoCredito c WHERE c.dataExpiracao < CURRENT_DATE");
        TypedQuery<CartaoCredito> query = em.createQuery("SELECT c FROM CartaoCredito c WHERE c.dataExpiracao < CURRENT_DATE", CartaoCredito.class);
        List<CartaoCredito> cartoesExpirados = query.getResultList();        
        assertEquals(1, cartoesExpirados.size());
    }
    
    @Test
    public void t13_categoriasPorQuantidadeFilhas() {
        logger.info("Executando t13: SELECT c FROM Categoria c WHERE SIZE(c.filhas) >= ?1");
        TypedQuery<Categoria> query;
        query = em.createQuery(
                "SELECT c FROM Categoria c WHERE SIZE(c.filhas) >= ?1",
                Categoria.class);
        query.setParameter(1, 3);
        List<Categoria> categorias = query.getResultList();
        assertTrue(categorias.size() == 1);
    }
    
    @Test
    public void t14_bandeirasDistintas() {
        logger.info("Executando t14: SELECT DISTINCT(c.bandeira) FROM CartaoCredito c ORDER BY c.bandeira");
        TypedQuery<String> query = 
                em.createQuery("SELECT DISTINCT(c.bandeira) FROM CartaoCredito c ORDER BY c.bandeira", String.class);
        List<String> bandeiras = query.getResultList();
        assertEquals(3, bandeiras.size());
    }

    @Test
    public void t15_ordenacaoCartao() {
        logger.info("Executando t15: SELECT c FROM CartaoCredito c ORDER BY c.bandeira DESC, c.dono.nome ASC");
        TypedQuery<CartaoCredito> query;
        query = em.createQuery(
                "SELECT c FROM CartaoCredito c ORDER BY c.bandeira DESC, c.dono.nome ASC",
                CartaoCredito.class);
        List<CartaoCredito> cartoes = query.getResultList();        
        assertEquals(4, cartoes.size());
        
        for (CartaoCredito cartao : cartoes) {
            logger.log(Level.INFO, "{0}: {1}", new Object[]{cartao.getBandeira(), cartao.getDono().getNome()});
        }
    }

    @Test
    public void t16_ordenacaoCartao() {
        logger.info("Executando t16: SELECT c.bandeira, c.dono.nome FROM CartaoCredito c ORDER BY c.bandeira DESC, c.dono.nome ASC");
        TypedQuery<Object[]> query;
        query = em.createQuery(
                "SELECT c.bandeira, c.dono.nome FROM CartaoCredito c ORDER BY c.bandeira DESC, c.dono.nome ASC",
                Object[].class);
        List<Object[]> cartoes = query.getResultList();        
        assertEquals(4, cartoes.size());
        
        for (Object[] cartao : cartoes) {
            logger.log(Level.INFO, "{0}: {1}", new Object[]{cartao[0], cartao[1]});
        }        
    }
    
    @Test
    public void t17_itensPorReputacaoVendedor() {
        logger.info("Executando t17: SELECT i FROM Item i WHERE i.vendedor IN (SELECT v FROM Vendedor v WHERE v.reputacao = :reputacao");
        TypedQuery<Item> query;
        query = em.createQuery(
                "SELECT i FROM Item i WHERE i.vendedor IN (SELECT v FROM Vendedor v WHERE v.reputacao = :reputacao)",
                Item.class);
        query.setParameter("reputacao", Reputacao.EXPERIENTE);
        List<Item> itens = query.getResultList();    
        assertEquals(3, itens.size());
        
        for (Item item : itens) {
            logger.log(Level.INFO, "{0}: {1}", new Object[]{item.getTitulo(), item.getDescricao()});
        }
    }
    
    @Test
    public void t18_itensVendidos() {
        logger.info("Executando t18: SELECT i FROM Item i WHERE EXISTS (SELECT o FROM Oferta o WHERE o.item = i AND o.vencedora = true)");
        TypedQuery<Item> query;
        query = em.createQuery(
                "SELECT i FROM Item i WHERE EXISTS (SELECT o FROM Oferta o WHERE o.item = i AND o.vencedora = true)",
                Item.class);
        List<Item> itens = query.getResultList();
        assertEquals(3, itens.size());
        
        for (Item item : itens) {
            logger.log(Level.INFO, "{0}: {1}", new Object[]{item.getTitulo(), item.getDescricao()});
        }        
    }

    @Test
    public void t19_ultimaOferta() {
        logger.info("Executando t19: SELECT o FROM Oferta o WHERE o.data >= ALL (SELECT o1.data FROM Oferta o1))");
        TypedQuery<Oferta> query;
        query = em.createQuery(
                "SELECT o FROM Oferta o WHERE o.data >= ALL (SELECT o1.data FROM Oferta o1)",
                Oferta.class);
        List<Oferta> ofertas = query.getResultList();
        assertEquals(1, ofertas.size());
        Oferta oferta = ofertas.get(0);
        logger.log(Level.INFO, "{0}: {1}", new Object[]{oferta.getData().toString(), oferta.getItem().getTitulo()});
    }
    
    @Test
    public void t20_todasOfertasExcetoAMaisAntiga() {
        logger.info("Executando t20: SELECT o FROM Oferta o WHERE o.data > ANY (SELECT o1.data FROM Oferta o1)");
        TypedQuery<Oferta> query;
        query = em.createQuery(
                "SELECT o FROM Oferta o WHERE o.data > ANY (SELECT o1.data FROM Oferta o1)",
                Oferta.class);
        List<Oferta> ofertas = query.getResultList();
        assertEquals(7, ofertas.size());

        for (Oferta oferta : ofertas) {
            logger.log(Level.INFO, "{0}: {1}", new Object[]{oferta.getData().toString(), oferta.getId()});
        }
    }

    @Test
    public void t21_compradoresComCartao() {
        logger.info("Executando t21: SELECT c FROM Comprador c JOIN c.cartaoCredito cc ORDER BY c.dataCriacao DESC");
        TypedQuery<Comprador> query;
        query = em.createQuery(
                "SELECT c FROM Comprador c JOIN c.cartaoCredito cc ORDER BY c.dataCriacao DESC",
                Comprador.class);
        List<Comprador> compradores = query.getResultList();
        assertEquals(4, compradores.size());
        
        for (Comprador comprador : compradores) {
            logger.log(Level.INFO, "{0}: {1}", new Object[]{comprador.getId(), comprador.getLogin()});
        }
    }

    @Test
    public void t22_compradoresCartoes() {
        logger.info("Executando t22: SELECT c.cpf, cc.bandeira FROM Comprador c LEFT OUTER JOIN c.cartaoCredito cc ORDER BY c.cpf");
        TypedQuery<Object[]> query;
        query = em.createQuery(
                "SELECT c.cpf, cc.bandeira FROM Comprador c LEFT JOIN c.cartaoCredito cc ORDER BY c.cpf",
                Object[].class);
        List<Object[]> compradores = query.getResultList();
        assertEquals(5, compradores.size());
        
        for (Object[] comprador : compradores) {
            logger.log(Level.INFO, "{0}: {1}", new Object[]{comprador[0], comprador[1]});
        }
    }

    @Test
    public void t23_compradoresOfertas() {
        logger.info("Executando t23: SELECT c FROM Comprador c JOIN FETCH c.cartaoCredito cc WHERE c.login = ?1");
        TypedQuery<Comprador> query;
        query = em.createQuery(
                "SELECT c FROM Comprador c JOIN c.cartaoCredito cc WHERE c.login = ?1",
                Comprador.class);
        query.setParameter(1, "zesilva");
        Comprador comprador = query.getSingleResult();
        logger.log(Level.INFO, "{0}: {1}", new Object[]{comprador.getCartaoCredito().getBandeira(), comprador.getLogin()});        
    }
    
    @Test
    public void t24_timestampSQL() {
        logger.info("Executando t24: SELECT current_timestamp() FROM DUAL");
        Query query;
        query = em.createNativeQuery(
                "SELECT current_timestamp() FROM DUAL");
        Date dataCorrente = (Date) query.getSingleResult();
        assertNotNull(dataCorrente);
        logger.log(Level.INFO, dataCorrente.toString());
    }
    
    @Test
    public void t25_categoriaSQL() {
        logger.info("Executando t25: SELECT id, txt_nome, id_categoria_mae FROM tb_categoria WHERE id_categoria_mae is null");
        Query query;
        query = em.createNativeQuery(
                "SELECT id, txt_nome, id_categoria_mae FROM tb_categoria WHERE id_categoria_mae is null",
                Categoria.class);
        List<Categoria> categorias = query.getResultList();
        assertEquals(1, categorias.size());
        
        for (Categoria categoria : categorias) {
            logger.log(Level.INFO, categoria.getNome());
        }
    }
    
    @Test
    public void t26_categoriaSQLNomeada() {
        logger.info("Executando t26: Categoria.PorNomeSQL");
        Query query;
        query = em.createNamedQuery("Categoria.PorNomeSQL");
        query.setParameter(1, "Guitarras");
        List<Categoria> categorias = query.getResultList();
        assertEquals(1, categorias.size());
        
        for (Categoria categoria : categorias) {
            logger.log(Level.INFO, categoria.getNome());
        }                
    }
    
    @Test
    public void t27_categoriaQuantidadeItens() {
        logger.info("Executando t27: Categoria.QuantidadeItensSQL");
        Query query;
        query = em.createNamedQuery("Categoria.QuantidadeItensSQL");
        query.setParameter(1, "Instrumentos Musicais");
        List<Object[]> resultados = query.getResultList();
        assertEquals(1, resultados.size());
        
        for (Object[] resultado : resultados) {
            logger.log(Level.INFO, "{0}: {1}", resultado);        
        }                        
    }
    
    @Test
    public void t28_categoriaQuantidadeItens() {
        logger.info("Executando t28: SELECT c, COUNT(i) FROM Categoria c, Item i WHERE c MEMBER OF i.categorias GROUP BY c");
        Query query = em.createQuery("SELECT c, COUNT(i) FROM Categoria c, Item i WHERE c MEMBER OF i.categorias GROUP BY c");
        List<Object[]> resultados = query.getResultList();
        assertEquals(4, resultados.size());
        
        for (Object[] resultado : resultados) {
            logger.log(Level.INFO, "{0}: {1}", resultado);        
        }        
    }
    
    @Test
    public void t29_categoriaQuantidadeItens() {
        logger.info("Executando t29: SELECT c, COUNT(i) FROM Categoria c, Item i WHERE c MEMBER OF i.categorias GROUP BY c HAVING COUNT(i) >= ?1");
        Query query = em.createQuery("SELECT c, COUNT(i) FROM Categoria c, Item i WHERE c MEMBER OF i.categorias GROUP BY c HAVING COUNT(i) >= ?1");
        query.setParameter(1, (long) 3);
        List<Object[]> resultados = query.getResultList();
        assertEquals(1, resultados.size());
        
        for (Object[] resultado : resultados) {
            logger.log(Level.INFO, "{0}: {1}", resultado);        
        }        
    }
    
    @Test
    public void t30_update() {
        logger.info("Executando t30: UPDATE Vendedor AS v SET v.dataNascimento = ?1 WHERE v.id = ?2");
        Long id = (long) 6;
        Query query = em.createQuery("UPDATE Vendedor AS v SET v.dataNascimento = ?1 WHERE v.id = ?2");
        query.setParameter(1, getData(10, 10, 1983));
        query.setParameter(2, id);
        query.executeUpdate();        
        Vendedor vendedor = em.find(Vendedor.class, id);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");        
        assertEquals(simpleDateFormat.format(getData(10, 10, 1983)), simpleDateFormat.format(vendedor.getDataNascimento()));
        logger.info(vendedor.getDataNascimento().toString());
    }
    
    @Test
    public void t31_delete() {
        Long id = (long) 6;
        logger.info("Executando t31: DELETE Oferta AS o WHERE o.id = ?1");
        Query query = em.createQuery("DELETE Oferta AS o WHERE o.id = ?1");
        query.setParameter(1, id);
        query.executeUpdate();        
        Oferta oferta = em.find(Oferta.class, id);  
        assertNull(oferta);
        logger.log(Level.INFO, "Oferta {0} removida com sucesso.", id);  
    }
}