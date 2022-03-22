package br.com.db1.avaliacao.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import br.com.db1.avaliacao.modelo.Produto;

@Repository
public interface ProdutoRepository extends CrudRepository<Produto, Long> {
	

}
