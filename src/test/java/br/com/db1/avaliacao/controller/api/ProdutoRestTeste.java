package br.com.db1.avaliacao.controller.api;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.com.db1.avaliacao.modelo.Produto;
import br.com.db1.avaliacao.repository.ProdutoRepository;


public class ProdutoRestTeste {

	private static final String URL = "http://localhost:8080/api/produtos";

	private MockMvc mockMvc;
	
	@Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoRest produtoRest;
	
	@SuppressWarnings("deprecation")
	@BeforeEach
    void setUp() {
		MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(produtoRest)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

	@Test
	void quandoChamarApiBuscarTodosRetornarStatusOk() throws Exception {
		
		
		when(produtoRepository.findAll()).thenReturn(new ArrayList<Produto>());

		
		mockMvc.perform(MockMvcRequestBuilders.get(URL).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	void quandoChamarApiBuscarPorIdRetornarUmProdutoEStatusOK() throws Exception {
		Produto produto = new Produto();
		
		Optional<Produto> retorno = Optional.of(produto);
		
		when(produtoRepository.findById(1L)).thenReturn(retorno);

		
		mockMvc.perform(MockMvcRequestBuilders.get(URL+"/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	void quandoChamarApiBuscarPorIdNaoRetornarUmProdutoEStatusNotFound() throws Exception {
		
		Optional<Produto> retorno = Optional.empty();
		
		when(produtoRepository.findById(1L)).thenReturn(retorno);

		
		mockMvc.perform(MockMvcRequestBuilders.get(URL+"/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	 @Test
	    void quandoChamarApiDeletarERetornarStatusOk() throws Exception {
	        //when
	        doNothing().when(produtoRepository).deleteById(1L);
	        // then
	        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/1")
	                .contentType(MediaType.APPLICATION_JSON))
	                .andExpect(status().isNoContent());
	    }
	 
	 @Test
	    void quandoChamarApiDeletarNaoEncontrarIdNoBD() throws Exception {
	        
	        doThrow(EmptyResultDataAccessException.class).when(produtoRepository).deleteById(1L);
	       
	        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/1")
	                .contentType(MediaType.APPLICATION_JSON))
	                .andExpect(status().isBadRequest());
	    }
	 
	 @Test
	    void quandoChamarApiCadastrarRetornarSucesso() throws Exception {
		 	Produto produto = new Produto("Caneta", new BigDecimal(100), 50, "Azul");
	        
	        mockMvc.perform(post(URL)
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(asJsonString(produto)))
	                .andExpect(status().isCreated());
	               
	    }
	 
	 @Test
	    void quandoChamarApiCadastrarComValoresInvalidosRetornarErro() throws Exception {
		 	Produto produto = new Produto(" ", new BigDecimal(100), -1, "Azul");
	        

	        mockMvc.perform(post(URL)
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(asJsonString(produto)))
	                .andExpect(status().isBadRequest());
	               
	        Mockito.verify(produtoRepository,times(0)).save(produto);
	    }
	 
	 @Test
	    void quandoChamarApiAtualizarRetornarStatusOk() throws Exception {
		 Produto produtoBD = new Produto("Caneta", new BigDecimal(100), 50, "Azul");
		 
		 Optional<Produto> retorno = Optional.of(produtoBD);
		 when(produtoRepository.findById(1L)).thenReturn(retorno);
		 
		 Produto produtoAtualizado = new Produto();
		 produtoAtualizado.setNome("Canetas");
		 produtoAtualizado.setPreco(new BigDecimal(50));
		 produtoAtualizado.setQuantidadeEstoque(50);
		 produtoAtualizado.setDescricao("Verde");
		 
		 MockHttpServletRequestBuilder builder =
	              MockMvcRequestBuilders.put(URL + "/1")
	                                    .contentType(MediaType.APPLICATION_JSON)
	                                    .accept(MediaType.APPLICATION_JSON)
	                                    .characterEncoding("UTF-8")
	                                    .content(asJsonString(produtoAtualizado));

		   mockMvc.perform(builder)
	                .andExpect(status().isAccepted());	   
			               
	    }
	 @Test
	    void quandoChamarApiAtualizarPassandoIdQueNaoExiste() throws Exception {
		 Produto produtoBD = new Produto("Caneta", new BigDecimal(100), 50, "Azul");
		 
		 Optional<Produto> retorno = Optional.empty();
		 when(produtoRepository.findById(1L)).thenReturn(retorno);
		 
		 Produto produtoAtualizado = new Produto();
		 produtoAtualizado.setNome("Canetas");
		 produtoAtualizado.setPreco(new BigDecimal(50));
		 produtoAtualizado.setQuantidadeEstoque(50);
		 produtoAtualizado.setDescricao("Verde");
		 
		 MockHttpServletRequestBuilder builder =
	              MockMvcRequestBuilders.put(URL + "/1")
	                                    .contentType(MediaType.APPLICATION_JSON)
	                                    .accept(MediaType.APPLICATION_JSON)
	                                    .characterEncoding("UTF-8")
	                                    .content(asJsonString(produtoAtualizado));

		   mockMvc.perform(builder)
	                .andExpect(status().isBadRequest());	   
			               
	    }
	 @Test
	    void quandoChamarApiAtualizarPassandoValorDeEstoqueNegativo() throws Exception {
		 Produto produtoBD = new Produto("Caneta", new BigDecimal(100), 50, "Azul");
		 
		 Optional<Produto> retorno = Optional.of(produtoBD);
		 when(produtoRepository.findById(1L)).thenReturn(retorno);
		 
		 Produto produtoAtualizado = new Produto();
		 produtoAtualizado.setNome("Canetas");
		 produtoAtualizado.setPreco(new BigDecimal(50));
		 produtoAtualizado.setQuantidadeEstoque(-60);
		 produtoAtualizado.setDescricao("Verde");
		 
		 MockHttpServletRequestBuilder builder =
	              MockMvcRequestBuilders.put(URL + "/1")
	                                    .contentType(MediaType.APPLICATION_JSON)
	                                    .accept(MediaType.APPLICATION_JSON)
	                                    .characterEncoding("UTF-8")
	                                    .content(asJsonString(produtoAtualizado));

		   mockMvc.perform(builder)
	                .andExpect(status().isBadRequest());	   
			               
	    }
	 @Test
	    void quandoChamarApiAtualizarPassandoValorNomeEmBranco() throws Exception {
		 Produto produtoBD = new Produto(" ", new BigDecimal(100), 50, "Azul");
		 
		 Optional<Produto> retorno = Optional.of(produtoBD);
		 when(produtoRepository.findById(1L)).thenReturn(retorno);
		 
		 Produto produtoAtualizado = new Produto();
		 produtoAtualizado.setNome("Canetas");
		 produtoAtualizado.setPreco(new BigDecimal(50));
		 produtoAtualizado.setQuantidadeEstoque(-60);
		 produtoAtualizado.setDescricao("Verde");
		 
		 MockHttpServletRequestBuilder builder =
	              MockMvcRequestBuilders.put(URL + "/1")
	                                    .contentType(MediaType.APPLICATION_JSON)
	                                    .accept(MediaType.APPLICATION_JSON)
	                                    .characterEncoding("UTF-8")
	                                    .content(asJsonString(produtoAtualizado));

		   mockMvc.perform(builder)
	                .andExpect(status().isBadRequest());	   
			               
	    }
	 @Test
	    void quandoChamarApiAtualizarPassandoValorDescricaoEmBranco() throws Exception {
		 Produto produtoBD = new Produto("Caneta", new BigDecimal(100), 50, "");
		 
		 Optional<Produto> retorno = Optional.of(produtoBD);
		 when(produtoRepository.findById(1L)).thenReturn(retorno);
		 
		 Produto produtoAtualizado = new Produto();
		 produtoAtualizado.setNome("Canetas");
		 produtoAtualizado.setPreco(new BigDecimal(50));
		 produtoAtualizado.setQuantidadeEstoque(-60);
		 produtoAtualizado.setDescricao("Verde");
		 
		 MockHttpServletRequestBuilder builder =
	              MockMvcRequestBuilders.put(URL + "/1")
	                                    .contentType(MediaType.APPLICATION_JSON)
	                                    .accept(MediaType.APPLICATION_JSON)
	                                    .characterEncoding("UTF-8")
	                                    .content(asJsonString(produtoAtualizado));

		   mockMvc.perform(builder)
	                .andExpect(status().isBadRequest());	   
			               
	    }
	 @Test
	    void QuandoIncrementarEstoqueRetornarStatusOk() throws Exception {
		 Produto produtoBD = new Produto("Caneta", new BigDecimal(100), 50, "Azul");
		 
		 Optional<Produto> retorno = Optional.of(produtoBD);
		 when(produtoRepository.findById(1L)).thenReturn(retorno);

		 MockHttpServletRequestBuilder builder =
	              MockMvcRequestBuilders.patch(URL + "/1" + "/10")
	                                    .contentType(MediaType.APPLICATION_JSON)
	                                    .accept(MediaType.APPLICATION_JSON)
	                                    .characterEncoding("UTF-8");

		   mockMvc.perform(builder)
	                .andExpect(status().isAccepted());	  
	    }
	 @Test
	    void QuandoDescrementarEstoqueComInsulficiente() throws Exception {
		 Produto produtoBD = new Produto("Caneta", new BigDecimal(100), 50, "Azul");
		 
		 Optional<Produto> retorno = Optional.of(produtoBD);
		 when(produtoRepository.findById(1L)).thenReturn(retorno);

		 MockHttpServletRequestBuilder builder =
	              MockMvcRequestBuilders.patch(URL + "/1" + "/-60")
	                                    .contentType(MediaType.APPLICATION_JSON)
	                                    .accept(MediaType.APPLICATION_JSON)
	                                    .characterEncoding("UTF-8");

		   mockMvc.perform(builder)
	                .andExpect(status().isBadRequest());	  
	    }
	 @Test
	    void QuandoDescrementarEstoqueComIdInexistente() throws Exception {
		 Produto produtoBD = new Produto("Caneta", new BigDecimal(100), 50, "Azul");
		 
		 Optional<Produto> retorno = Optional.empty();
		 when(produtoRepository.findById(1L)).thenReturn(retorno);

		 MockHttpServletRequestBuilder builder =
	              MockMvcRequestBuilders.patch(URL + "/1" + "/-5")
	                                    .contentType(MediaType.APPLICATION_JSON)
	                                    .accept(MediaType.APPLICATION_JSON)
	                                    .characterEncoding("UTF-8");

		   mockMvc.perform(builder)
	                .andExpect(status().isBadRequest());	  
	    }
	 @Test
	    void QuandoDescrementarEstoqueComIdInvalido() throws Exception {
		 Produto produtoBD = new Produto("Caneta", new BigDecimal(100), 50, "Azul");
		 
		 Optional<Produto> retorno = Optional.empty();
		 when(produtoRepository.findById(1L)).thenReturn(retorno);

		 MockHttpServletRequestBuilder builder =
	              MockMvcRequestBuilders.patch(URL + "/X" + "/-5")
	                                    .contentType(MediaType.APPLICATION_JSON)
	                                    .accept(MediaType.APPLICATION_JSON)
	                                    .characterEncoding("UTF-8");

		   mockMvc.perform(builder)
	                .andExpect(status().isBadRequest());	  
	    }
	 @Test
	    void QuandoDescrementarEstoqueComValorEstoqueInvalido() throws Exception {
		 Produto produtoBD = new Produto("Caneta", new BigDecimal(100), 50, "Azul");
		 
		 Optional<Produto> retorno = Optional.empty();
		 when(produtoRepository.findById(1L)).thenReturn(retorno);

		 MockHttpServletRequestBuilder builder =
	              MockMvcRequestBuilders.patch(URL + "/1" + "/X")
	                                    .contentType(MediaType.APPLICATION_JSON)
	                                    .accept(MediaType.APPLICATION_JSON)
	                                    .characterEncoding("UTF-8");

		   mockMvc.perform(builder)
	                .andExpect(status().isBadRequest());	  
	    }
	 
		public static String asJsonString(Object produto) {
	        try {
	            ObjectMapper objectMapper = new ObjectMapper();
	            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	            objectMapper.registerModules(new JavaTimeModule());

	            return objectMapper.writeValueAsString(produto);
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
	    }
	    
}
