# Adicionando segurança a uma API REST com Spring Security

Curso ministrado por Gleyson Sampaio, através da plataforma DIO no Bootcamp Santander FullStack Java Angular.

## SpringBoot Security

É um grupo de filtros de servelet que ajudam a adicionar autenticação e autorização ao seu aplicativo da web.

### Terminologias

- Autenticação: é o processo de **verificação** da identidade de um usuário, com base nas credenciais fornecidas.
- Autorização: é o processo de determinar se um usuário **tem permissão** adequada para executar uma ação ou ler dados específicos, supondo que o usuário seja autenticado com êxito.
- Princípio: refere-se ao usuário autenticado no momento.
- Autoridade concedida: informações adicionais sobre permissão do usuário autenticado, (bloqueado ou sessão expirada).
- Função: grupo de permissões do usuário autenticado.

## Habilitando segurança com Spring

Criamos o projeto através do [Spring Initializr](https://start.spring.io/), adicionando as dependência Spring Web e Spring Security.

Após adquirir o projeto e descompactá-lo e executarmos, seguimos para o navegador com a porta disponibilizada, descrita no console e adicionamos `user` e a senha gerada no console.

## Autenticação simples

Para demonstrações do sistema, essa abordagem não se torna interessante. Para que não seja necessário a inserção de usuário e senha a cada inicialização da aplicação, fazemos algumas configurações de segurança no `application.properties`.
Ex:

``` property: application.properties
spring.security.user.name=user123
spring.security.user.password=user123
spring.security.user.roles=USERS
```

## Em memória

Esta configuração permite criar mais de um usuário e perfis de acesso.

É necessário criar uma [classe](dio-spring-security/src/main/java/dio/diospringsecurity/WebSecurityConf.java) que estenda `WebSecurityConfigurerAdapter`.

`{bycrypt}, {pbkdf2}, {scrypt}, {sha256} e o {noop}` são implementações de criptografia utilizadas pelo Spring Security.

Criamos a classe [`WelcomeController`](dio-spring-security/src/main/java/dio/diospringsecurity/WelcomeController.java) para indicar os perfis de autenticação.

## Configuração Adapter

Podemos deixar as configurações centralizadas na classe `WebSecurityConf` removendo configurações adicionais `@PreAuthorize()` em nossos controllers.

## Autenticação com banco de dados

O Spring Boot dispões de uma facilidade para integrar aplicações com banco de dados com o Spring Data JPA.

Adicionamos o Spring Data JPA no pom.xml:

``` xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

Criamos uma classe [User.java](dio-spring-security/src/main/java/dio/diospringsecurity/model/User.java).

E um repositório para interagir com a nossa entidade User.java e realizar as operações de CRUD necessárias.

Usamos a interface `UserDetailsService` para recuperar dados relacionados ao usuário. Ele possui o método `loadUserByUsername()` que pode ser substituído para personalizar o processo de localização do usuário.

Criamos uma classe `SecurityDataService.java` que implementará a `UserDetailsService` para retornar um usuário para contexto de segurança conforme nosso banco de dados.

Desta forma, posso remover a implementação de usuário em memória e utilizar o usuário do nosso banco de dados.

Adicionamos uma carga inicial de usuários em nossa aplicação, criando a classe `StartApplication.java` que implementa a interface `CommandLineRunner` para executar ao iniciar a aplicação.

Por dim precisamos adicionar a dependência do banco de dados.

``` xml
<dependency>
  <groupId>com.h2database</groupId>
  <artifactId>h2</artifactId>
  <scope>runtime</scope>
</dependency>
```

## JWT - Json web token

É um padrão da internet para criação de dados com assinatura opcional e/ou criptografia cujo conteúdo contém o JSON que afirma algum número de declarações. Os tokens são assinados usando um segredo privado ou uma chave pública/privada.

### Estrutura do JWT

É dividida em 3 partes: `header`, `payload` e `signature`.

- Header: ou cabeçalho normalmente consiste em duas partes; o tipo de token, que é JWT e o algoritmo de assinatura que está sendo utilizado, como HMAC, SHA256 ou RSA.
- Payload: é a estrutura do corpo contendo as informações de autenticação e autorização de um usuário.
Ex:

``` json
  {
    "sub": "alice",
    "name": "ALICE HATA",
    "roles": ["USERS", "MANAGERS"]
  }
```

- Signature: é onde você deve pegar o cabeçalho codificado, o payload codificado, a chave secreta, o algoritmo especificado no cabeçalho e assiná-lo, garantindo autenticação e autenticidade de toda a estrutura.

## Projeto Spring Security + JWT

Desenvolvemos um aplicativo com um usuário com perfis de acesso para geração e validação do token que é transferido pelos clientes da nossa API.

### Criamos o projeto

`dio-spring-security-jwt` com o [Initializr](https://start.spring.io/) com as dependências
`Spring Data JPA`, `Spring Web`, `Spring Security`, `H2 Database` e extraímos para nossa área de trabalho.

Adicionamos a dependência do jsonwebtoken no arquivo `pom.xml` e atualizamos o projeto.

``` xml
  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.7.0</version>
  </dependency>
```

Por boas práticas,seguiremos estruturando o projeto em pacotes de acordo com suas responsabilidades:

- model: camada que contém as entidades da aplicação
- dto: camada que contém os dtos da aplicação
- repository: camada com os repositórios com base no Spring Data JPA
- service: camada com regra de negócio e comunicação com a base de dados vias repositories.
- controller: camada com recursos https expostos na API.
- security: camada responsável por toda configuração de segurança.

Classes relevantes para a aplicação:

- SwaggerConfig: responsável pela documentação da API.
- JWTObject: representa um Objeto correspondente a estrutura JWT
- JWTCreator: responsável por gerar token com base no objeto e ou instanciar o objeto JWT com base no token.

Criamos a classe modelo User.java.
Criamos a classe de repositório UserRepository.java.
Criamos a classe de negócio UserService.java.
Criamos a classe UserController.java para disponibilizar um recurso HTTP para cadastrar usuário.

O cadastro deve possui um atributo de confirmação antes de estar disponível para acessar nossa API.

### Configurando o JWT no projeto

Criamos uma representação deste objeto e o mecanismo de geração e interpretação do token, chamamos a classe de `JWTObject`.

A classe `SecurityConfig.java` é o componente que receberá as propriedades e credenciais do token via `application.properties`.

security.config.prefix = prefixo do token
security.config.key = sua chave privada
security.config.expiration = tempo de expiração do token.

Adiciono as propriedades para o token:

``` property: application.properties
security.config.prefix=Bearer
security.config.key=SECRET_KEY
security.config.expiration=3600000
```

Criamos a classe `JWTCreator.java`, responsável por gerar o token com base no objeto e vice-versa.

Criamos a classe `JWTFilter.java`, responsável por validar o token que será recebido nas requisições.

Adicionamos algumas configurações de banco de dados para visualizar o H2 Database na Web.

``` ##H2 Database Connection Properties
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=sa
spring.jpa.show-sql: true
spring.h2.console.enabled=true
```

Criamos a classe WebSecurityConfig para centralizar toda a configuração de segurança da API.

Removemos o `scope` em runtime para habilitar o banco de dados h2 na web.

Salvamos um usuário executando um POST: http://localhost8080/users, passando o json no body.

``` json
  {
    "nome": "ALICE",
    "username": "alice",
    "password": "alice123",
    "roles": ["USERS", "MANAGERS"]
  }
```

E confirmamos no console do H2 Database.
Após ter adicionado o usuário, temos que gerar o token com base nos dados passados pelo login de acesso. Então, criamos os DTO's de login e sessão.

A classe `Login` receberá os dados para a realização do login da aplicação e a classe `Sessao` representa uma sessão do sistema contento o token gerado.

Criamos a classe LoginService com o recurso de realizar o login e a geração de token.

E testamos executando uma requisição POST: http://localhost8080/users, passando o json no body:

``` json
  {
    "username": "alice",
    "password": "alice123"
  }
```

## Mais

O conteúdo do curso se encontra no [gitbook](https://glysns.gitbook.io/spring-framework/spring-security/spring-security-e-jwt) e o repositório com o projeto do curso está disponível no github do [Gleyson Sampaio](https://github.com/digitalinnovationone/dio-springboot).
