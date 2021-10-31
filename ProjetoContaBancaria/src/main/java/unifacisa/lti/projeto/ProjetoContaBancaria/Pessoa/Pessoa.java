package unifacisa.lti.projeto.ProjetoContaBancaria.Pessoa;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Pessoa {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int idPessoa;
	private String nome;
	private String cpf;
	private Date dataNascimento;

}
