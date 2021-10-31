package unifacisa.lti.projeto.ProjetoContaBancaria.Conta;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import unifacisa.lti.projeto.ProjetoContaBancaria.Transacao.Transacao;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conta {
	
	@ApiModelProperty(value = "Identificador único de cada conta")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int idConta;
	
	@ApiModelProperty(value = "Identificador único do titular da conta")
	private int idPessoa;
	
	@ApiModelProperty(value = "Valor referente ao saldo disponível na conta")
	private double saldo;
	
	@ApiModelProperty(value = "Valor referente ao limite de saque diário que o titular poderá fazer")
	private double limiteSaqueDiario;
	
	@ApiModelProperty(value = "Valor que indica se a conta está bloqueada ou não")
	private boolean flagAtivo;
	
	@ApiModelProperty(value = "Valor que indica o tipo da conta (Poupança, corrente)")
	private int tipoConta;
	
	@ApiModelProperty(value = "Data que indica quando a conta foi aberta")
	private Date dataCriacao;

	// Lista de transações para essa conta ou seja o EXTRATO
	//OrphanRemoval irá servir para que quando eu DELETE essa conta as transações dela também serão deletadas
	@ApiModelProperty(value = "Lista de transacoes que a conta já realizou")
	@OneToMany(mappedBy = "conta", orphanRemoval = true)
	private List<Transacao> transacoes;

}
