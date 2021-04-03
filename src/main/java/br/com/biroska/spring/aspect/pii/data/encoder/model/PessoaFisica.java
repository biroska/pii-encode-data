package br.com.biroska.spring.aspect.pii.data.encoder.model;

import lombok.*;


@Data
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PessoaFisica {

    private long id;

    private String nome;

    private Documento documento;

}
