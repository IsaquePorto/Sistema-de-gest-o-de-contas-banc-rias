package unifacisa.lti.projeto.ProjetoContaBancaria.Transacao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransacaoRepository extends JpaRepository<Transacao, Integer> {
	
	//Essa query customizada está obtendo o extrato apartir e até uma data determinada pelo usuário
	
	@Query("select t from Transacao t where t.idConta = :id and t.dataTransacao >= :from and t.dataTransacao <= :to ")
    List<Transacao> findByDataTransacaoBetween(@Param("id") int id, @Param("from") Date from, @Param("to") Date to);
	
}
