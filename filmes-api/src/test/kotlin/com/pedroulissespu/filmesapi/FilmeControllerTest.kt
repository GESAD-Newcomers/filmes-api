package com.pedroulissespu.filmesapi

import com.fasterxml.jackson.databind.ObjectMapper
import com.pedroulissespu.filmesapi.model.Filme
import com.pedroulissespu.filmesapi.repository.FilmesRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.http.MediaType

@SpringBootTest
@AutoConfigureMockMvc
class FilmeControllerTest {

    @Autowired lateinit var mockMvc : MockMvc

    @Autowired lateinit var  filmesRepository: FilmesRepository
    @Test
    fun `test find all`(){
        filmesRepository.save(Filme(titulo = "Teste" , atores = "Testes" , genero = "Teste" , classificacao = 1 , preco = "12.99"))

        mockMvc.perform(MockMvcRequestBuilders.get("/filmes"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("\$").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("\$[0].id").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$[0].titulo").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$[0].atores").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$[0].genero").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$[0].classificacao").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$[0].preco").isString)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun`test find by id`(){
        val filme = filmesRepository.save(Filme(titulo = "Teste" , atores = "Testes" , genero = "Teste" , classificacao = 1 , preco = "12.99"))

        mockMvc.perform(MockMvcRequestBuilders.get("/filmes/${filme.id}"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("\$").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.id").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.titulo").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.atores").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.genero").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.classificacao").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.preco").isString)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test create filme`(){
        val filme = Filme(titulo = "Teste" , atores = "Testes" , genero = "Teste" , classificacao = 1 , preco = "12.99")
        val json = ObjectMapper().writeValueAsString(filme)
        filmesRepository.deleteAll()
        mockMvc.perform(MockMvcRequestBuilders.post("/filmes")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.titulo").value(filme.titulo))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.atores").value(filme.atores))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.genero").value(filme.genero))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.classificacao").value(filme.classificacao))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.preco").value(filme.preco))
            .andDo(MockMvcResultHandlers.print())

        Assertions.assertFalse(filmesRepository.findAll().isEmpty())
    }

    @Test
    fun `test update account`(){
        val filme = filmesRepository.save(Filme(titulo = "Teste" , atores = "Testes" , genero = "Teste" , classificacao = 1 , preco = "12.99"))
            .copy(titulo = "Updated")
        val json = ObjectMapper().writeValueAsString(filme)
        mockMvc.perform(MockMvcRequestBuilders.put("/filmes/${filme.id}")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.titulo").value(filme.titulo))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.atores").value(filme.atores))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.genero").value(filme.genero))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.classificacao").value(filme.classificacao))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.preco").value(filme.preco))
            .andDo(MockMvcResultHandlers.print())

        val findById = filmesRepository.findById(filme.id!!)
        Assertions.assertTrue(findById.isPresent)
        Assertions.assertEquals(filme.titulo,findById.get().titulo)
    }

    @Test
    fun `test delete filme`(){
        val filme = filmesRepository
            .save(Filme(titulo = "Teste" , atores = "Testes" , genero = "Teste" , classificacao = 1 , preco = "12.99"))
        mockMvc.perform(MockMvcRequestBuilders.delete("/filmes/${filme.id}"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())

        val findById = filmesRepository.findById(filme.id!!)
        Assertions.assertFalse(findById.isPresent)
    }

    @Test
    fun `test create filme validation error empty titulo`(){
        val filme = Filme(titulo = "" , atores = "Testes" , genero = "Teste" , classificacao = 1 , preco = "12.99")
        val json = ObjectMapper().writeValueAsString(filme)
        filmesRepository.deleteAll()
        mockMvc.perform(MockMvcRequestBuilders.post("/filmes")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[titulo] não pode estar em branco !"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test create filme validation error titulo should be 5 characters`(){
        val filme = Filme(titulo = "test" , atores = "Testes" , genero = "Teste" , classificacao = 1 , preco = "12.99")
        val json = ObjectMapper().writeValueAsString(filme)
        filmesRepository.deleteAll()
        mockMvc.perform(MockMvcRequestBuilders.post("/filmes")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[titulo] deve ter no minimo 5 caracteres!"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test create filme validation error empty atores`(){
        val filme = Filme(titulo = "test" , atores = "" , genero = "Teste" , classificacao = 1 , preco = "12.99")
        val json = ObjectMapper().writeValueAsString(filme)
        filmesRepository.deleteAll()
        mockMvc.perform(MockMvcRequestBuilders.post("/filmes")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[atores] não pode estar em branco !"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test create filme validation error atores should be 4 characters`(){
        val filme = Filme(titulo = "test" , atores = "tes" , genero = "Teste" , classificacao = 1 , preco = "12.99")
        val json = ObjectMapper().writeValueAsString(filme)
        filmesRepository.deleteAll()
        mockMvc.perform(MockMvcRequestBuilders.post("/filmes")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[atores] deve ter no minimo 4 caracteres!"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test create filme validation error empty genero`(){
        val filme = Filme(titulo = "test" , atores = "" , genero = "Teste" , classificacao = 1 , preco = "12.99")
        val json = ObjectMapper().writeValueAsString(filme)
        filmesRepository.deleteAll()
        mockMvc.perform(MockMvcRequestBuilders.post("/filmes")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[genero] não pode estar em branco !"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test create filme validation error genero should be 5 characters`(){
        val filme = Filme(titulo = "test" , atores = "tes" , genero = "Teste" , classificacao = 1 , preco = "12.99")
        val json = ObjectMapper().writeValueAsString(filme)
        filmesRepository.deleteAll()
        mockMvc.perform(MockMvcRequestBuilders.post("/filmes")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[genero] deve ter no minimo 5 caracteres"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test create filme validation error empty classificacao`(){
        val filme = Filme(titulo = "test" , atores = "" , genero = "Teste" , classificacao = 1 , preco = "12.99")
        val json = ObjectMapper().writeValueAsString(filme)
        filmesRepository.deleteAll()
        mockMvc.perform(MockMvcRequestBuilders.post("/filmes")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[classificacao] não pode estar em branco !"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test create filme validation error genero should be 0 classificacao`(){
        val filme = Filme(titulo = "test" , atores = "tes" , genero = "Teste" , classificacao = 1 , preco = "12.99")
        val json = ObjectMapper().writeValueAsString(filme)
        filmesRepository.deleteAll()
        mockMvc.perform(MockMvcRequestBuilders.post("/filmes")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[classificacao] deve ter pelo menos uma classificação"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test create filme validation error empty preco`(){
        val filme = Filme(titulo = "test" , atores = "" , genero = "Teste" , classificacao = 1 , preco = "12.99")
        val json = ObjectMapper().writeValueAsString(filme)
        filmesRepository.deleteAll()
        mockMvc.perform(MockMvcRequestBuilders.post("/filmes")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[preco] não pode estar em branco !"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test create filme validation error genero should be 5 preco`(){
        val filme = Filme(titulo = "test" , atores = "tes" , genero = "Teste" , classificacao = 1 , preco = "12.99")
        val json = ObjectMapper().writeValueAsString(filme)
        filmesRepository.deleteAll()
        mockMvc.perform(MockMvcRequestBuilders.post("/filmes")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[preco] deve ter no minimo 5 caracteres"))
            .andDo(MockMvcResultHandlers.print())
    }
}