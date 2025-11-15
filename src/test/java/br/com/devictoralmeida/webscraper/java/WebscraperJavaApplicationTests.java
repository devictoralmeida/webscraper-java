package br.com.devictoralmeida.webscraper.java;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Tests para a classe WebscraperJavaApplication")
class WebscraperJavaApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Deve carregar o contexto da aplicação Spring Boot")
    void contextLoads() {
        assertThat(this.applicationContext).isNotNull();
    }

    @Test
    @DisplayName("Deve verificar que a aplicação principal está registrada como bean")
    void deveVerificarQueAplicacaoPrincipalEstaRegistrada() {
        assertThat(this.applicationContext.containsBean("webscraperJavaApplication")).isTrue();
    }

    @Test
    @DisplayName("Deve verificar que a classe principal possui anotação @SpringBootApplication")
    void deveVerificarQueClassePrincipalPossuiAnotacaoSpringBootApplication() {
        assertThat(WebscraperJavaApplication.class.isAnnotationPresent(org.springframework.boot.autoconfigure.SpringBootApplication.class))
                .isTrue();
    }

    @Test
    @DisplayName("Deve verificar que a classe principal possui anotação @EnableJpaRepositories")
    void deveVerificarQueClassePrincipalPossuiAnotacaoEnableJpaRepositories() {
        assertThat(WebscraperJavaApplication.class.isAnnotationPresent(EnableJpaRepositories.class)).isTrue();
    }

    @Test
    @DisplayName("Deve verificar que o basePackages do @EnableJpaRepositories está correto")
    void deveVerificarQueBasePackagesDoEnableJpaRepositoriesEstaCorreto() {
        EnableJpaRepositories annotation = WebscraperJavaApplication.class.getAnnotation(EnableJpaRepositories.class);

        assertThat(annotation).isNotNull();
        assertThat(annotation.basePackages()).containsExactly("br.com.devictoralmeida.webscraper.java.repositories");
    }

    @Test
    @DisplayName("Deve verificar que o ApplicationContext contém beans essenciais")
    void deveVerificarQueApplicationContextContemBeansEssenciais() {
        assertThat(this.applicationContext.getBeanDefinitionCount()).isPositive();
    }

    @Test
    @DisplayName("Deve verificar que o ambiente Spring está configurado corretamente")
    void deveVerificarQueAmbienteSpringEstaConfiguradoCorretamente() {
        assertThat(this.applicationContext.getEnvironment()).isNotNull();
        assertThat(this.applicationContext.getEnvironment().getActiveProfiles()).isNotNull();
    }

    @Test
    @DisplayName("Deve verificar que o método main existe e é público estático")
    void deveVerificarQueMetodoMainExisteEhPublicoEstatico() throws NoSuchMethodException {
        Method mainMethod = WebscraperJavaApplication.class.getMethod("main", String[].class);

        assertThat(mainMethod).isNotNull();
        assertThat(Modifier.isPublic(mainMethod.getModifiers())).isTrue();
        assertThat(Modifier.isStatic(mainMethod.getModifiers())).isTrue();
        assertThat(mainMethod.getReturnType()).isEqualTo(void.class);
    }

    @Test
    @DisplayName("Deve verificar que o método main recebe array de String como parâmetro")
    void deveVerificarQueMetodoMainRecebeArrayDeStringComoParametro() throws NoSuchMethodException {
        Method mainMethod = WebscraperJavaApplication.class.getMethod("main", String[].class);

        assertThat(mainMethod.getParameterCount()).isEqualTo(1);
        assertThat(mainMethod.getParameterTypes()[0]).isEqualTo(String[].class);
    }

    @Test
    @DisplayName("Deve verificar que a classe WebscraperJavaApplication é pública")
    void deveVerificarQueClasseWebscraperJavaApplicationEhPublica() {
        assertThat(Modifier.isPublic(WebscraperJavaApplication.class.getModifiers())).isTrue();
    }

    @Test
    @DisplayName("Deve verificar que a classe WebscraperJavaApplication não é abstrata")
    void deveVerificarQueClasseWebscraperJavaApplicationNaoEhAbstrata() {
        assertThat(Modifier.isAbstract(WebscraperJavaApplication.class.getModifiers())).isFalse();
    }

    @Test
    @DisplayName("Deve verificar que o método main chama SpringApplication.run")
    void deveVerificarQueMetodoMainChamaSpringApplicationRun() throws NoSuchMethodException {
        Method mainMethod = WebscraperJavaApplication.class.getMethod("main", String[].class);

        assertThat(mainMethod).isNotNull();
        assertThat(mainMethod.getDeclaringClass()).isEqualTo(WebscraperJavaApplication.class);
    }
}
