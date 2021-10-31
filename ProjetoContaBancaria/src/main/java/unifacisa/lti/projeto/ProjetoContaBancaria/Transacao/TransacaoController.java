package unifacisa.lti.projeto.ProjetoContaBancaria.Transacao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin
public class TransacaoController {
	
	@Autowired
	private TransacaoRepository repository;
	
	@GetMapping("/transacao")
	public List<Transacao> getContas() {	//Endpoint com a finalidade de listar todas as transaçoes feitas
		return repository.findAll();
	}
	
}
