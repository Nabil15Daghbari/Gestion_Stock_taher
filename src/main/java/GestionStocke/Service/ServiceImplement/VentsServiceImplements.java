package GestionStocke.Service.ServiceImplement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import GestionStocke.DTO.LigneVenteDto;
import GestionStocke.DTO.VentesDto;
import GestionStocke.Exception.EntityNotFoundException;
import GestionStocke.Exception.ErrorCodes;
import GestionStocke.Exception.InvalidEntityException;
import GestionStocke.Exception.InvalidOperationException;
import GestionStocke.Service.MvtStkService;
import GestionStocke.Service.VentesService;
import GestionStocke.Validator.VenteValidator;
import GestionStocke.entity.Article;
import GestionStocke.entity.LigneVente;
import GestionStocke.entity.Ventes;
import GestionStocke.repostory.Articlerepository;
import GestionStocke.repostory.LigneVenteRepository;
import GestionStocke.repostory.VentesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class VentsServiceImplements implements VentesService  {
	
	  private Articlerepository articleRepository;
	  private VentesRepository ventesRepository;
	  private LigneVenteRepository ligneVenteRepository;
	  private MvtStkService mvtStkService;

	  @Override
	  public VentesDto save(VentesDto dto) {
	    List<String> errors = VenteValidator.venteValidate(dto);
	    if (!errors.isEmpty()) {
	      log.error("Ventes n'est pas valide");
	      throw new InvalidEntityException("L'objet vente n'est pas valide", ErrorCodes.VENTE_NOT_VALID, errors);
	    }

	    List<String> articleErrors = new ArrayList<>();

	    dto.getLigneVentes().forEach(ligneVenteDto -> {
	      Optional<Article> article = articleRepository.findById(ligneVenteDto.getArticle().getId());
	      if (article.isEmpty()) {
	        articleErrors.add("Aucun article avec l'ID " + ligneVenteDto.getArticle().getId() + " n'a ete trouve dans la BDD");
	      }
	    });

	    if (!articleErrors.isEmpty()) {
	      log.error("One or more articles were not found in the DB, {}", errors);
	      throw new InvalidEntityException("Un ou plusieurs articles n'ont pas ete trouve dans la BDD", ErrorCodes.VENTE_NOT_VALID, errors);
	    }

	    Ventes savedVentes = ventesRepository.save(VentesDto.toEntity(dto));

	    dto.getLigneVentes().forEach(ligneVenteDto -> {
	      LigneVente ligneVente = LigneVenteDto.toEntity(ligneVenteDto);
	      ligneVente.setVente(savedVentes);
	      ligneVenteRepository.save(ligneVente);
//	      updateMvtStk(ligneVente);
	    });

	    return VentesDto.fromEntity(savedVentes);
	  }

	  @Override
	  public VentesDto findById(Integer id) {
	    if (id == null) {
	      log.error("Ventes ID is NULL");
	      return null;
	    }
	    return ventesRepository.findById(id)
	        .map(VentesDto::fromEntity)
	        .orElseThrow(() -> new EntityNotFoundException("Aucun vente n'a ete trouve dans la BDD", ErrorCodes.VENTE_NOT_FOUND));
	  }

	  @Override
	  public VentesDto findByCode(String code) {
	    if (!StringUtils.hasLength(code)) {
	      log.error("Vente CODE is NULL");
	      return null;
	    }
	    return ventesRepository.findVentesByCode(code)
	        .map(VentesDto::fromEntity)
	        .orElseThrow(() -> new EntityNotFoundException(
	            "Aucune vente client n'a ete trouve avec le CODE " + code, ErrorCodes.VENTE_NOT_VALID
	        ));
	  }

	  @Override
	  public List<VentesDto> findAll() {
	    return ventesRepository.findAll().stream()
	        .map(VentesDto::fromEntity)
	        .collect(Collectors.toList());
	  }

	  @Override
	  public void delete(Integer id) {
	    if (id == null) {
	      log.error("Vente ID is NULL");
	      return;
	    }
	    List<LigneVente> ligneVentes = ligneVenteRepository.findAllByVenteId(id);
	    if (!ligneVentes.isEmpty()) {
	      throw new InvalidOperationException("Impossible de supprimer une vente ...",
	          ErrorCodes.VENTE_ALREADY_IN_USE);
	    }
	    ventesRepository.deleteById(id);
	  }

	  
	
}
