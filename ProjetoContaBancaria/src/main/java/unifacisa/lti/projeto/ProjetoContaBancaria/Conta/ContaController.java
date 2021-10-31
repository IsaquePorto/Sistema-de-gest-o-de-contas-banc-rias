package unifacisa.lti.projeto.ProjetoContaBancaria.Conta;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import unifacisa.lti.projeto.ProjetoContaBancaria.Transacao.Transacao;

@RestController
@CrossOrigin
@RequestMapping(path = "", produces = "application/json")
public class ContaController {

	@Autowired
	private ContaService service;

	@ApiOperation(value = "Path que retorna todas as contas do banco.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Retorna uma lista com todas as contas do banco."), })
	@GetMapping("/conta")
	public List<Conta> getContas() {
		return service.getContas();
	}

	@ApiOperation(value = "Path que retorna uma conta específica do banco através de um ID.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Retorna um Response contendo a conta referente ao ID passado."),
			@ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso."),
			@ApiResponse(code = 400, message = "Não foi encontrado um conta referente ao ID passado."), })
	@GetMapping("/conta/{idConta}")
	public ContaResponse<Conta> getConta(@PathVariable int idConta) {
		ContaResponse<Conta> response = new ContaResponse<Conta>();
		if (service.getConta(idConta) == null) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.getErrors().put("1", "Não existe uma conta com esse Id");
			return response;
		}
		response.setData(service.getConta(idConta));
		response.setStatus(HttpStatus.OK.value());
		return response;
	}

	@ApiOperation(value = "Path que realiza a criação de uma conta.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Retorna a própria conta se tiver sido gerada com sucesso."),
			@ApiResponse(code = 400, message = "Alguns campos podem não ter sido preenchidos corretamente."), })
	@PostMapping("/addConta")
	public ContaResponse<Conta> addConta(@RequestBody Conta conta, BindingResult result) {
		ContaResponse<Conta> response = new ContaResponse<Conta>();

		if (result.hasErrors()) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			for (ObjectError error : result.getAllErrors()) {
				String key = String.valueOf(response.getErrors().size() + 1);
				response.getErrors().put(key, error.getDefaultMessage());
			}
			return response;
		}

		response.setData(conta);
		service.addConta(conta);
		response.setStatus(HttpStatus.OK.value());

		return response;
	}

	@ApiOperation(value = "Path que realiza a atualização da conta cujo o ID foi informado na URL.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Retorna a própria conta se tiver sido atualizada com sucesso."),
			@ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso."),
			@ApiResponse(code = 400, message = "Não foi encontrado um conta referente ao ID passado."),
			@ApiResponse(code = 400, message = "Alguns campos podem não ter sido preenchidos corretamente."),
			@ApiResponse(code = 400, message = "A conta está bloqueada."), })
	@PutMapping("/atualizarConta/{idConta}")
	public ContaResponse<Conta> atualizarConta(@RequestBody Conta conta, @PathVariable int idConta,
			BindingResult result) {
		ContaResponse<Conta> response = new ContaResponse<Conta>();

		if (service.getConta(idConta) == null || result.hasErrors()) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			for (ObjectError error : result.getAllErrors()) {
				String key = String.valueOf(response.getErrors().size() + 1);
				response.getErrors().put(key, error.getDefaultMessage());
			}
			return response;
		} else if (service.isBloqueada(idConta)) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.getErrors().put("0", "A conta referente ao id: " + idConta + " se encontra bloqueada!");
			return response;
		} else {
			response.setData(service.atualizarConta(idConta, conta));
			response.setStatus(HttpStatus.OK.value());
			return response;
		}
	}

	@ApiOperation(value = "Path que realiza a exclusão de uma conta.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Conta excluida com sucesso! Retorna uma String informando a exclusão."),
			@ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso."),
			@ApiResponse(code = 400, message = "Não foi encontrado um conta referente ao ID passado."),
			@ApiResponse(code = 400, message = "A conta está bloqueada."), })
	@DeleteMapping("/deletarConta/{idConta}")
	public ContaResponse<String> deletarConta(@PathVariable int idConta) {

		ContaResponse<String> response = new ContaResponse<String>();
		if (service.getConta(idConta) == null) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.getErrors().put("1", "Não existe uma conta para esse Id");
			return response;
		} else if (service.isBloqueada(idConta)) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.getErrors().put("0", "A conta referente ao id: " + idConta + " se encontra bloqueada!");
			return response;
		}

		response.setStatus(HttpStatus.OK.value());
		response.setData("Conta de id: " + idConta + " deletada!");
		service.deleteConta(idConta);
		return response;
	}

	// ENDPOINTS REFERENTES A TRANSACAO

	@ApiOperation(value = "Path que realiza operação de depósito em uma conta através de uma TRANSAÇÃO.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Retorna a transação que foi efetuada com sucesso."),
			@ApiResponse(code = 400, message = "Não foi encontrado um conta referente ao ID passado."),
			@ApiResponse(code = 400, message = "Alguns campos podem não ter sido preenchidos corretamente."),
			@ApiResponse(code = 400, message = "A conta está bloqueada."), })
	@PostMapping("/realizarDeposito")
	public ContaResponse<Transacao> realizarDeposito(@RequestBody Transacao transacao, BindingResult result) {
		ContaResponse<Transacao> response = new ContaResponse<Transacao>();
		if (service.getConta(transacao.getIdConta()) == null || result.hasErrors()) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			for (ObjectError error : result.getAllErrors()) {
				String key = String.valueOf(response.getErrors().size() + 1);
				response.getErrors().put(key, error.getDefaultMessage());
			}
			return response;
		} else if (service.isBloqueada(transacao.getIdConta())) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.getErrors().put("0",
					"A conta referente ao id: " + transacao.getIdConta() + " se encontra bloqueada!");
			return response;
		} else {
			response.setData(transacao);
			service.realizarDeposito(transacao);
			response.setStatus(HttpStatus.OK.value());
			return response;
		}
	}

	@ApiOperation(value = "Path que realiza operação de saque em uma conta através de uma TRANSAÇÃO.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Retorna a transação que foi efetuada com sucesso."),
			@ApiResponse(code = 400, message = "Não foi encontrado um conta referente ao ID passado."),
			@ApiResponse(code = 400, message = "Alguns campos podem não ter sido preenchidos corretamente."),
			@ApiResponse(code = 400, message = "A conta está bloqueada."),
			@ApiResponse(code = 400, message = "Não há dinheiro disponível na conta."), })
	@PostMapping("/realizarSaque")
	public ContaResponse<Transacao> realizarSaque(@RequestBody Transacao transacao, BindingResult result) {
		ContaResponse<Transacao> response = new ContaResponse<Transacao>();
		if (service.getConta(transacao.getIdConta()) == null || result.hasErrors()) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			for (ObjectError error : result.getAllErrors()) {
				String key = String.valueOf(response.getErrors().size() + 1);
				response.getErrors().put(key, error.getDefaultMessage());
			}
			return response;
		} else if (!service.hasSaldo(transacao)) { // Verifica se há saldo na conta:
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.getErrors().put("0", "Não há saldo na conta disponível para essa transação.");
			return response;
		} else if (service.isBloqueada(transacao.getIdConta())) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.getErrors().put("0",
					"A conta referente ao id: " + transacao.getIdConta() + " se encontra bloqueada!");
			return response;
		} else {
			response.setData(transacao);
			service.realizarSaque(transacao);
			response.setStatus(HttpStatus.OK.value());
			return response;
		}
	}

	@ApiOperation(value = "Path que recupera o extrato de transações de uma conta.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Retorna uma lista com todas as transações realizadas pela conta."),
			@ApiResponse(code = 400, message = "Não foi encontrado um conta referente ao ID passado."),
			@ApiResponse(code = 400, message = "A conta está bloqueada."), })
	@GetMapping("/extrato/{idConta}")
	public ContaResponse<List<Transacao>> getExtrato(@PathVariable int idConta) {
		ContaResponse<List<Transacao>> response = new ContaResponse<List<Transacao>>();
		if (service.getConta(idConta) == null) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.getErrors().put("1", "Não existe uma conta com esse Id");
			return response;
		} else if (service.isBloqueada(idConta)) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.getErrors().put("0", "A conta referente ao id: " + idConta + " se encontra bloqueada!");
			return response;
		}
		response.setData(service.getExtrato(idConta));
		response.setStatus(HttpStatus.OK.value());
		return response;
	}

	@ApiOperation(value = "Path que realiza operação de consulta de saldo em determinada conta através de um ID passado na URL.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Retorna um Response em que o data é o saldo da conta."),
			@ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso."),
			@ApiResponse(code = 400, message = "Não foi encontrado um conta referente ao ID passado."), })
	@GetMapping("/saldo/{idConta}")
	public ContaResponse<Double> getSaldo(@PathVariable int idConta) {
		ContaResponse<Double> response = new ContaResponse<Double>();
		if (service.getConta(idConta) == null) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.getErrors().put("1", "Não existe uma conta com esse Id");
			return response;
		}
		response.setData(service.consultaSaldo(idConta));
		response.setStatus(HttpStatus.OK.value());
		return response;
	}

	@ApiOperation(value = " Path que realiza o bloqueio de uma conta. (ID passado na URL)")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Retorna a conta que foi bloqueada em caso de sucesso."),
			@ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso."),
			@ApiResponse(code = 400, message = "Não foi encontrado um conta referente ao ID passado."), })
	@GetMapping("/bloqueia/{idConta}")
	public ContaResponse<Conta> bloquear(@PathVariable int idConta) {
		ContaResponse<Conta> response = new ContaResponse<Conta>();
		if (service.getConta(idConta) == null) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.getErrors().put("1", "Não existe uma conta com esse Id");
			return response;
		}
		response.setData(service.bloqueiaConta(idConta));
		response.setStatus(HttpStatus.OK.value());
		return response;
	}

	// No Postman você deve passar no body da requisiçao uma String com a data de
	// início e a data de fim SEPARADOS por uma vírgula e nesse formato: dd/mm/aaaa.
	@ApiOperation(value = "Path que recupera o extrato de transações de uma conta por período.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Retorna uma lista com todas as transações feitas entre as datas especificadas."),
			@ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso."),
			@ApiResponse(code = 400, message = "Não foi encontrado um conta referente ao ID passado."),
			@ApiResponse(code = 400, message = "Alguns campos podem não ter sido preenchidos corretamente."), })
	@PostMapping("/extratoPeriodo/{idConta}")
	public ContaResponse<List<Transacao>> getExtratoPorPeríodo(@RequestBody String datas, @PathVariable int idConta)
			throws ParseException {
		ContaResponse<List<Transacao>> response = new ContaResponse<List<Transacao>>();
		if (service.getConta(idConta) == null) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.getErrors().put("1", "Não existe uma conta com esse Id");
			return response;
		} else if (service.isBloqueada(idConta)) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.getErrors().put("0", "A conta referente ao id: " + idConta + " se encontra bloqueada!");
			return response;
		}

		String[] datasSeparadas = datas.split(",");

		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		Date data1 = formato.parse(datasSeparadas[0]);

		Date data2 = formato.parse(datasSeparadas[1]);

		response.setData(service.getExtratoPorPeriodo(idConta, data1, data2));
		response.setStatus(HttpStatus.OK.value());
		return response;
	}

}
