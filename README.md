# Web Scraper de Notícias - InfoMoney

Esta é uma API RESTful de Web Scraping construída em Java e Spring Boot, projetada para extrair e armazenar notícias do
portal InfoMoney (seção Mercados).

A aplicação utiliza uma abordagem híbrida para coletar dados:

1. Faz o parse do HTML inicial para coletar as notícias visíveis no primeiro carregamento.
2. Chama a API interna (privada) do InfoMoney para buscar notícias adicionais (simulando o clique em "Carregar Mais").

O processamento das notícias (download e parse do HTML de cada artigo) é feito em paralelo usando **Threads Virtuais (
Java 21)** para garantir alta performance de I/O.

---

## 🚀 Funcionalidades Principais

* **Scraping Híbrido:** Extrai as notícias iniciais do HTML da página e busca notícias adicionais "Carregar Mais"
  através da API interna do portal (via `POST`).
* **Processamento Paralelo:** Utiliza **Threads Virtuais** (Java 21) para processar o download e parse de múltiplas
  notícias simultaneamente, otimizando drasticamente o desempenho de I/O de rede.
* **Persistência de Dados:** Salva Notícias e Autores em um banco de dados **PostgreSQL**, com gerenciamento de schema
  via **Flyway**.
* **Evita Duplicatas:** Verifica as URLs existentes no banco em lote (`IN (...)`) antes de processar, garantindo que
  apenas notícias novas sejam salvas.
* **API de Comando (CQRS):** Expõe um endpoint (`POST /api/noticias/buscar`) para disparar o processo de scraping.
* **API de Consulta (CQRS):** Fornece endpoints de relatório para consultar os dados salvos (ex: autores mais ativos e
  notícias por autor).
* **Tratamento de Exceções:** Utiliza um `@RestControllerAdvice` para capturar exceções customizadas (como
  `NegocioException` e `ParametrosDeConsultaInvalidosException`) e retornar respostas de erro padronizadas.

---

## 🛠️ Tecnologias Utilizadas

* **Java 21**
* **Spring Boot 3+**
* **Spring Data JPA** (Persistência)
* **Spring WebFlux (WebClient)** (Cliente HTTP reativo)
* **PostgreSQL** (Banco de dados relacional)
* **Flyway** (Migrations de banco)
* **Jsoup** (Parse de HTML)
* **Docker & Docker Compose** (Ambiente de banco)
* **SpringDoc (Swagger)** (Documentação da API)
* **Lombok**

---

## 🚀 Como Executar a Aplicação

### 1. Subir o Banco de Dados (PostgreSQL)

O banco de dados é gerenciado via Docker Compose. Para iniciá-lo, execute na raiz do projeto:

```bash
docker-compose up -d
```

Isso irá iniciar um container PostgreSQL na porta **5433**, conforme configurado no `application.properties`.

### 2. Executar a Aplicação (Spring Boot)

Com o banco de dados rodando, você pode iniciar a aplicação Spring Boot usando o Maven Wrapper:

```bash
# Em terminais Linux/macOS
./mvnw spring-boot:run

# Em terminais Windows (CMD/PowerShell)
./mvnw.cmd spring-boot:run
```

A aplicação estará disponível em http://localhost:8080/api. O Flyway executará as migrations automaticamente na primeira
inicialização.

---

## Documentação da API (Swagger)

Para acessar a documentação da API e testar os endpoints, acesse o Swagger UI no seu navegador após iniciar a aplicação:

1. URL do Swagger: http://localhost:8080/api/docs

### Endpoints Principais

1. POST /api/noticias/buscar

Dispara o processo de scraping. Recebe um parâmetro ?limit (ex: 15) para definir o número de notícias a buscar.

2. POST /api/noticias/relatorios/autores-mais-ativos

Retorna os autores com mais publicações em um período. Requer um corpo DateRangeRequestDTO.

3. POST /api/noticias/relatorios/autor/{authorId}

Retorna as notícias de um autor específico em um período. Requer um authorId na URL e um DateRangeRequestDTO no corpo.