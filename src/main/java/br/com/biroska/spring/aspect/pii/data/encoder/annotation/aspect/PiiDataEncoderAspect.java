package br.com.biroska.spring.aspect.pii.data.encoder.annotation.aspect;

import br.com.biroska.spring.aspect.pii.data.encoder.annotation.PiiDataEncoder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Aspect
@Component
public class PiiDataEncoderAspect {

    @AfterReturning( pointcut = "@annotation( br.com.biroska.spring.aspect.pii.data.encoder.annotation.PiiDataEncoder )",
                     returning = "obj")
    public void encoderAspect(JoinPoint joinPoint, Object obj) throws Throwable {

        if ( obj != null ){

            try {

                System.out.println("PiiDataEncoderAspect.encoderAspect: " + obj);

                Signature signature = joinPoint.getSignature();
                MethodSignature methodSignature = (MethodSignature) signature;

                // Obtem os paths informados na annotation @PiiDataEncoder
                List<String> paths = retrievePathsFromAnnotation(methodSignature);

                // Itera pelos paths informados na annotation
                for (String path : paths) {

                    System.out.println("===================================================================================");

                    // Comecando a bruxaria XD \o/
                    Object auxObjectToModify = obj;

                    System.out.println("\tAnalisando o path = " + path);

                    // Obtem uma lista onde cada elemento é um atributo do path
                    // path: pf.documento.numero
                    // complexAttributes = [ pf, documento, numero ]
                    List<String> complexAttributes = retrieveComplexAttributesList(path);

                    System.out.println("\t\tcomplexAttributes = " + complexAttributes);

                    // seleciona o ultimo elemento da lista de atributos e o retira da lista
                    // Necessario para que seja possivel setar o valor do atributo atraves do seu objeto (penultimo elemento da lista)
                    String lastElement = org.springframework.util.CollectionUtils.lastElement(complexAttributes);
                    System.out.println("\t\tlastElement = " + lastElement);

                    boolean remove = complexAttributes.remove(lastElement);
                    System.out.println("\t\tremoveu o ultimo elemento = " + remove);

                    // Obtem a instacia do ultimo objeto, para que seja possivel sobrescrer o seu atributo
                    auxObjectToModify = retrieveLastObject(auxObjectToModify, complexAttributes);
                    System.out.println("\t\tObjeto a ser modificado = " + auxObjectToModify);

                    // sobrescreve o atributo com o valor criptografado
                    // auxObjectToModify --> Instancia do objeto a ter o atributo atualizado
                    // lastElement       --> Nome do atributo a ser atualizado
                    if (auxObjectToModify != null) {

                        String valueToEncode = (String) FieldUtils.readField(auxObjectToModify, lastElement, Boolean.TRUE);
                        String encodedValue = base64Encoder(valueToEncode);

                        System.out.println("\t\tAtualizando o atributo " + lastElement + " do objeto: " + auxObjectToModify + " com o valor: " + encodedValue );

                        FieldUtils.writeDeclaredField(auxObjectToModify, lastElement, encodedValue, Boolean.TRUE);
                    }
                }
            } catch ( IllegalArgumentException e){
                System.out.println( "if target is null, fieldName is blank or empty or could not be found, or value is not assignable");
            } catch ( IllegalAccessException e) {
                System.out.println("if the field is not made accessible");
            }
        }
    }

    private String base64Encoder(String input){

        if (StringUtils.isNotEmpty( input) ) {

            return Base64.getEncoder().encodeToString( input.getBytes() );
        }

        return input;

    }

    private Object retrieveLastObject(Object auxObjectToModify, List<String> complexAttributes) throws IllegalAccessException {

        System.out.println("\t\tNavegando nos atributos: ");

        for (String attribute : complexAttributes) {
            System.out.println("\t\t\tattribute = " + attribute);
            auxObjectToModify = FieldUtils.readField(auxObjectToModify, attribute, true);

            if ( auxObjectToModify == null ){
                break;
            }

        }
        return auxObjectToModify;
    }

    private List<String> retrieveComplexAttributesList(String path) {
        String[] splitted = path.split("\\.");
        List<String> complexAttributes = new ArrayList<>();

        CollectionUtils.addAll( complexAttributes, splitted );
        return complexAttributes;
    }

    private List<String> retrievePathsFromAnnotation( MethodSignature methodSignature) {

        Method method = methodSignature.getMethod();

        PiiDataEncoder piiDataEncoderAnnotation = method.getAnnotation(PiiDataEncoder.class);
        String[] paths = piiDataEncoderAnnotation.paths();

        List<String> pathsList = Arrays.asList(paths);

        System.out.println("Paths que serao analisados: " + pathsList );

        return pathsList;
    }
}
