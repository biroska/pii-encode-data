package br.com.biroska.spring.aspect.pii.data.encoder.controller;

import br.com.biroska.spring.aspect.pii.data.encoder.annotation.PiiDataEncoder;
import br.com.biroska.spring.aspect.pii.data.encoder.model.Documento;
import br.com.biroska.spring.aspect.pii.data.encoder.model.PessoaFisica;
import br.com.biroska.spring.aspect.pii.data.encoder.model.PessoaFisicaWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("pessoa-fisica")
public class PessoaFisicaController {

    @PiiDataEncoder(paths= {"pf.documento.numero"})
    @GetMapping(path = "/{id}", produces = "application/json")
    public PessoaFisicaWrapper getBook(@PathVariable int id) {

        PessoaFisicaWrapper joeDoe = getJoeDoe();

        System.out.println("PessoaFisicaController.getBook: " + joeDoe );

        return joeDoe;
    }

    @PiiDataEncoder(paths= {"pf.documento.numero", "pf.nome"})
    @PostMapping(path = "/", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public PessoaFisicaWrapper create( PessoaFisica resource) {

        PessoaFisicaWrapper joeDoe = getJoeDoe();

        System.out.println("PessoaFisicaController.create: " + joeDoe);

        return joeDoe;
    }

    private PessoaFisicaWrapper getJoeDoe(){

        return PessoaFisicaWrapper.builder().pf(
                    PessoaFisica.builder().id(1l).nome("Joe Doe").documento(
                        Documento.builder().id(1l).numero("123.456.789-00").build())
                    .build() )
                .build();

    }

}
