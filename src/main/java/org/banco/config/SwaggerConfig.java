package org.banco.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String API_TITULO = "AT-PB: CRUD Conta Bancária";
    private static final String API_DESCRICAO =
            "API para gerenciamento de contas bancárias, incluindo abertura, consulta e encerramento de contas.";
    private static final String API_VERSAO = "1.0.0";
    private static final String NOME_LICENCA = "Apache 2.0";
    private static final String URL_LICENCA = "https://www.apache.org/licenses/LICENSE-2.0";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(API_TITULO)
                        .description(API_DESCRICAO)
                        .version(API_VERSAO)
                        .license(new License()
                                .name(NOME_LICENCA)
                                .url(URL_LICENCA)));
    }
}
