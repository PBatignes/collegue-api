package dev.collegue.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.stereotype.Service;

import dev.collegue.dto.CollegueDto;
import dev.collegue.entite.Collegue;
import dev.collegue.exception.CollegueNonTrouveException;
import dev.collegue.repository.CollegueRepository;

/**
 * @author Batignes Pierre
 *
 */
@Service
public class CollegueService {

	private CollegueRepository collegueRepo;

	/**
	 * Constructor
	 *
	 * @param collegueRepository
	 */
	public CollegueService(CollegueRepository collegueRepository) {
		super();
		this.collegueRepo = collegueRepository;
	}

	public List<String> getMatriculesByNom(String nom) {

		return collegueRepo.findByNom(nom).stream().map(col -> col.getMatricule()).collect(Collectors.toList());
	}

	public Collegue getCollegueByMatricule(String matricule) {

		Optional<Collegue> collegue = collegueRepo.getOneByMatricule(matricule);

		if (collegue.isPresent()) {
			return collegue.get();
		} else {
			throw new CollegueNonTrouveException("Collegue non trouvé");
		}
	}

	@Transactional
	public Collegue postCollegue(@Valid CollegueDto nouvCollegue) {

		StringBuilder email = new StringBuilder();
		email.append(nouvCollegue.getNom().toLowerCase()).append(".").append(nouvCollegue.getPrenoms().toLowerCase())
				.append("@email.com");

		Collegue collegue = new Collegue(UUID.randomUUID().toString(), nouvCollegue.getNom(), nouvCollegue.getPrenoms(),
				email.toString(), nouvCollegue.getDateDeNaissance(), nouvCollegue.getPhotoUrl());

		collegueRepo.save(collegue);

		return collegue;
	}

	@Transactional
	public Collegue updateCollegue(CollegueDto modifCollegue) {

		Optional<Collegue> findCollegue = collegueRepo.getOneByMatricule(modifCollegue.getMatricule());

		if (findCollegue.isPresent()) {

			Collegue colleguePresent = findCollegue.get();

			if (modifCollegue.getNom() != null)
				colleguePresent.setNom(modifCollegue.getNom());

			if (modifCollegue.getPrenoms() != null)
				colleguePresent.setPrenoms(modifCollegue.getPrenoms());

			if (modifCollegue.getDateDeNaissance() != null)
				colleguePresent.setDateDeNaissance(modifCollegue.getDateDeNaissance());

			if (modifCollegue.getEmail() != null)
				colleguePresent.setEmail(modifCollegue.getEmail());

			if (modifCollegue.getPhotoUrl() != null)
				colleguePresent.setPhotoUrl(modifCollegue.getPhotoUrl());

			collegueRepo.save(colleguePresent);

			return colleguePresent;

		} else {
			throw new CollegueNonTrouveException("Modification impossible, collegue non trouvé");
		}

	}

}
