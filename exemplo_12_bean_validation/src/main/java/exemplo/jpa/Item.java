package exemplo.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "TB_ITEM")
public class Item implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Oferta> ofertas;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "TB_ITENS_CATEGORIAS", joinColumns = {
        @JoinColumn(name = "ID_ITEM")},
            inverseJoinColumns = {
                @JoinColumn(name = "ID_CATEGORIA")})
    private List<Categoria> categorias;
    @NotBlank
    @Size(max = 150)
    @Column(name = "TXT_TITULO")
    private String titulo;
    @NotBlank
    @Size(max = 500)
    @Column(name = "TXT_DESCRICAO")
    private String descricao;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_VENDEDOR", referencedColumnName = "ID_USUARIO")
    private Vendedor vendedor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    public List<Oferta> getOfertas() {
        return ofertas;
    }

    public boolean temOfertas() {
        return !this.ofertas.isEmpty();
    }
    
    public void adicionar(Oferta oferta) {
        if (this.ofertas == null) {
            this.ofertas = new ArrayList<>();
        }

        this.ofertas.add(oferta);
        oferta.setItem(this);
    }

    public boolean remover(Oferta oferta) {
        return ofertas.remove(oferta);
    }

    public void adicionar(Categoria categoria) {
        if (this.categorias == null) {
            this.categorias = new ArrayList<>();
        }

        categorias.add(categoria);
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Item)) {
            return false;
        }
        Item other = (Item) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "exemplo.jpa.Item[ id=" + id + " ]";
    }

}
