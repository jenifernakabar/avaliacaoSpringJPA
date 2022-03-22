package br.com.db1.avaliacao.controller.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.db1.avaliacao.modelo.ApiMessage;
import br.com.db1.avaliacao.modelo.Produto;
import br.com.db1.avaliacao.repository.ProdutoRepository;

@RestController
@RequestMapping("/api")
public class ProdutoRest {

	private ProdutoRepository produtoRepository;

	@Autowired
	public ProdutoRest(ProdutoRepository produtoRepository) {
		super();
		this.produtoRepository = produtoRepository;
	}

	@PostMapping(path = { "/produtos" })
	public ResponseEntity cadastrarProduto(@RequestBody @Validated Produto produto) {

		if (produto.getNome().isEmpty() || produto.getNome().isBlank()) {
			return new ResponseEntity<>(new ApiMessage("Campo nome invalido", 400), HttpStatus.BAD_REQUEST);
		}

		if (produto.getDescricao().isEmpty() || produto.getDescricao().isBlank()) {
			return new ResponseEntity<>(new ApiMessage("Campo descrição invalido", 400), HttpStatus.BAD_REQUEST);
		}
		if (produto.getPreco().intValue() <= 0 || produto.getPreco() == null) {
			return new ResponseEntity<>(new ApiMessage("Campo Preço invalido", 400), HttpStatus.BAD_REQUEST);
		}
		if (produto.getQuantidadeEstoque() < 0) {
			return new ResponseEntity<>(new ApiMessage("Campo quantidade invalido", 400), HttpStatus.BAD_REQUEST);
		}
		produtoRepository.save(produto);
		return new ResponseEntity<>(new ApiMessage("Produto " + produto.getNome() + " criado", 201),
				HttpStatus.CREATED);

	}

	@GetMapping(path = { "/produtos/{id}" })
	public ResponseEntity buscarProduto(@PathVariable String id) {
		try {
			Optional<Produto> produto = produtoRepository.findById(Long.valueOf(id));
			if (produto.isPresent()) {
				return ResponseEntity.ok(produto);
			} else {
				return new ResponseEntity<>(new ApiMessage("Produto Não Encontrado", 400), HttpStatus.BAD_REQUEST);
			}

		} catch (NumberFormatException e) {
			return new ResponseEntity<>(new ApiMessage("O id deve ser numerico", 400), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/produtos")
	public ResponseEntity getListaTodosProdutos() {
		return ResponseEntity.ok(produtoRepository.findAll());
	}

	@PutMapping(path = { "/produtos/{id}" })
	public ResponseEntity atualizarProduto(@PathVariable String id, @RequestBody Produto produto) {
		if (produto.getNome().isEmpty() || produto.getNome().isBlank()) {
			return new ResponseEntity<>(new ApiMessage("Campo nome invalido", 400), HttpStatus.BAD_REQUEST);
		}

		if (produto.getDescricao().isEmpty() || produto.getDescricao().isBlank()) {
			return new ResponseEntity<>(new ApiMessage("Campo descrição invalido", 400), HttpStatus.BAD_REQUEST);
		}
		if (produto.getPreco().intValue() <= 0 || produto.getPreco() == null) {
			return new ResponseEntity<>(new ApiMessage("Campo Preço invalido", 400), HttpStatus.BAD_REQUEST);
		}
		if (produto.getQuantidadeEstoque() < 0) {
			return new ResponseEntity<>(new ApiMessage("Campo quantidade invalido", 400), HttpStatus.BAD_REQUEST);
		}
		
		try {
			Optional<Produto> produtoDB = produtoRepository.findById(Long.valueOf(id));
			if (produtoDB.isPresent()) {
				produtoDB.get().setDescricao(produto.getDescricao());
				produtoDB.get().setNome(produto.getNome());
				produtoDB.get().setPreco(produto.getPreco());
				produtoDB.get().setQuantidadeEstoque(produto.getQuantidadeEstoque());
				int estoqueDB = produtoDB.get().getQuantidadeEstoque() + produto.getQuantidadeEstoque();
				if (estoqueDB < 0) {
					return new ResponseEntity<>(new ApiMessage("Quantidade do estoque nao pode ser negativa", 400), HttpStatus.BAD_REQUEST);
				} else {
					produtoRepository.save(produtoDB.get());
					return new ResponseEntity<>(
							new ApiMessage("Produto " + produtoDB.get().getNome() + " Atualizado", 204),
							HttpStatus.ACCEPTED);
				}

			} else {
				return new ResponseEntity<>(new ApiMessage("Produto Não Encontrado", 400), HttpStatus.BAD_REQUEST);
			}

		} catch (NumberFormatException e) {
			return new ResponseEntity<>(new ApiMessage("O id deve ser numerico", 400), HttpStatus.BAD_REQUEST);
		}

	}

	@PatchMapping(path = { "/produtos/{id}/{quantidadeEstoque}" })
	public ResponseEntity atualizarEstoque(@PathVariable String id, @PathVariable String quantidadeEstoque) {
		try {
			Optional<Produto> produtoDB = produtoRepository.findById(Long.valueOf(id));
			if (produtoDB.isPresent()) {
				int estoqueDB = produtoDB.get().getQuantidadeEstoque() + Integer.valueOf(quantidadeEstoque);
				if (estoqueDB < 0) {
					return new ResponseEntity<>(new ApiMessage("Estoque insulficiente", 400), HttpStatus.BAD_REQUEST);
				}
				produtoDB.get().setQuantidadeEstoque(estoqueDB);
				produtoRepository.save(produtoDB.get());
				return new ResponseEntity<>(new ApiMessage("Estoque do produto " + produtoDB.get().getNome()
						+ " atualizado para " + produtoDB.get().getQuantidadeEstoque(), 204), HttpStatus.ACCEPTED);
			} else {
				return new ResponseEntity<>(new ApiMessage("Produto Não Encontrado", 400), HttpStatus.BAD_REQUEST);
			}

		} catch (NumberFormatException e) {
			return new ResponseEntity<>(new ApiMessage("Ambos valores precisam ser numericos", 400),
					HttpStatus.BAD_REQUEST);
		}

	}

	@DeleteMapping(path = { "/produtos/{id}" })
	public ResponseEntity deletarProduto(@PathVariable String id) {
		try {
			produtoRepository.deleteById(Long.valueOf(id));
			return new ResponseEntity<>(new ApiMessage("Produto Deletado", 204), HttpStatus.NO_CONTENT);

		} catch (NumberFormatException e) {
			return new ResponseEntity<>(new ApiMessage("O id deve ser numerico", 400), HttpStatus.BAD_REQUEST);
		} catch (EmptyResultDataAccessException e) {
			return new ResponseEntity<>(new ApiMessage("ID não encontrado", 400), HttpStatus.BAD_REQUEST);
		}

	}
}
