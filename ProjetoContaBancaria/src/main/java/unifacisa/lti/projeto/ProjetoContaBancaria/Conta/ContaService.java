package unifacisa.lti.projeto.ProjetoContaBancaria.Conta;

import java.util.Date;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import unifacisa.lti.projeto.ProjetoContaBancaria.Transacao.Transacao;
import unifacisa.lti.projeto.ProjetoContaBancaria.Transacao.TransacaoRepository;

@Service
public class ContaService {

	@Autowired
	private ContaRepository repository;

	@Autowired
	private TransacaoRepository transRepository; // Injeção de dependência

	// GET
	public List<Conta> getContas() {
		return repository.findAll();
	}

	// GET/1
	public Conta getConta(int idConta) {
		return repository.findById(idConta).orElse(null);
	}

	// POST
	public Conta addConta(Conta novo) {

		novo.setDataCriacao(new Date(System.currentTimeMillis()));
		novo.setLimiteSaqueDiario(1000);
		novo.setFlagAtivo(true);
		return repository.save(novo);
	}

	// DELETE
	public void deleteConta(int idConta) {
		repository.deleteById(idConta);
	}

	// PUT
	public Conta atualizarConta(int idConta, Conta conta) {
		Conta novaConta = repository.findById(idConta).orElse(null);
		novaConta.setIdPessoa(conta.getIdPessoa());
		novaConta.setTipoConta(conta.getTipoConta());
		novaConta.setLimiteSaqueDiario(conta.getLimiteSaqueDiario());
		return repository.save(novaConta);
	}

	public String realizarSaque(Transacao transacao) {
		Conta conta = repository.findById(transacao.getIdConta()).orElse(null);
		conta.setSaldo(conta.getSaldo() - transacao.getValor());
		transacao.setConta(conta);

		Date data = new Date(System.currentTimeMillis());
		transacao.setDataTransacao(data);
		transacao.setTipoTransacao("Saque");
		transRepository.save(transacao);
		return "Saque realizado com sucesso";
	}

	public String realizarDeposito(Transacao transacao) {
		Conta conta = repository.findById(transacao.getIdConta()).orElse(null);

		conta.setSaldo(conta.getSaldo() + transacao.getValor());
		transacao.setConta(conta);

		Date data = new Date(System.currentTimeMillis());
		transacao.setDataTransacao(data);
		transacao.setTipoTransacao("Deposito");
		transRepository.save(transacao);
		return "Deposito realizado com sucesso";
	}

	// Método para verificar se há saldo disponível na conta
	public boolean hasSaldo(Transacao transacao) {
		Conta conta = repository.findById(transacao.getIdConta()).orElse(null);
		return transacao.getValor() <= conta.getSaldo() ? true : false;
	}

	public List<Transacao> getExtrato(int idConta) {
		Conta conta = repository.findById(idConta).orElse(null);
		return conta.getTransacoes();
	}

	public double consultaSaldo(int idConta) {
		Conta conta = repository.findById(idConta).orElse(null);
		return conta.getSaldo();
	}

	public Conta bloqueiaConta(int idConta) {
		Conta conta = repository.findById(idConta).orElse(null);
		conta.setFlagAtivo(false);
		return repository.save(conta);
	}

	// Verifica se a conta está bloqueada
	public boolean isBloqueada(int idConta) {
		Conta conta = repository.findById(idConta).orElse(null);
		return conta.isFlagAtivo() ? false : true;
	}

	public List<Transacao> getExtratoPorPeriodo(int idConta, Date from, Date to) {

		return transRepository.findByDataTransacaoBetween(idConta, from, to);
	}

}
