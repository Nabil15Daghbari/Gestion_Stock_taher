package GestionStocke.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)

@Entity
public class Article extends AuditModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Column(name = "code_Article")
	private String codeArticle;
	//@NotBlank
	private String designation;

	private BigDecimal prixUnitaire;

	private BigDecimal tauxTva;

	private BigDecimal prixUniteaireTtc;
	
	private String quantite;

	private Integer seuilQuantite ;

	@Column(name = "code_barre")
	private String codeBarre;



	private String photo;

	@Column(name = "identreprise")
	private Integer idEntreprise;

	@ManyToOne
	@JoinColumn(name = "idCategory")
	private Category category;

	@OneToMany(mappedBy = "article")
	private List<LigneVente> LigneVentes = new ArrayList<LigneVente>();

	@OneToMany(mappedBy = "article")
	private List<LigneCommandeClient> ligneCommaneClients = new ArrayList<LigneCommandeClient>();

	@OneToMany(mappedBy = "article")
	private List<LigneCommandeFornisseur> ligneCommandeFournisseurs = new ArrayList<LigneCommandeFornisseur>();

	@OneToMany(mappedBy = "article")
	private List<MvtStk> MvtStks = new ArrayList<MvtStk>();

	@OneToMany(mappedBy = "article")
	private List<Alerte> alertes = new ArrayList<>();
}
