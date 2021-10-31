package unifacisa.lti.projeto.ProjetoContaBancaria.Transacao;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import unifacisa.lti.projeto.ProjetoContaBancaria.Conta.Conta;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transacao {

	// Tudo que é referente a deposito e saque

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int idTransacao;
	private int idConta;
	private double valor;
	private Date dataTransacao;
	private String tipoTransacao;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "conta_id")
	private Conta conta;
}
