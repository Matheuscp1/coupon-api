# coupon-api

API REST para gerenciamento de **cupons de desconto**, desenvolvida em **Spring Boot 3** com **Java 21**, H2 em memória e Docker.

---

## 🚀 Tecnologias

- Java 21 (Eclipse Temurin)
- Spring Boot 3
- Spring Data JPA / Hibernate
- H2 Database (in-memory)
- Bean Validation (`@NotNull`, `@Size`, `@DecimalMin`)
- Maven
- Docker / Docker Compose
- JUnit5 + Mockito + SpringBootTest + MockMvc
- Swagger / OpenAPI
- Jacoco cobertura de testes

---

## 💾 Requisitos

- Java 21 ou superior
- Maven 3.8+
- Docker & Docker Compose (opcional, mas recomendado)

---

## ⚡ Executando localmente

1. Clone o repositório:

git clone https://github.com/seuusuario/coupon-api.git
cd coupon-api

Build do projeto:

mvn clean package

Rodar a aplicação:

mvn spring-boot:run

A API estará disponível em: http://localhost:8080

📌 Endpoints
Método	Endpoint	Descrição
POST	/coupons	Criar novo cupom
GET	/coupons	Listar cupons ativos (não deletados)
DELETE	/coupons/{id}	Soft delete de cupom pelo ID

🐳 Executando com Docker

Build da imagem:

docker compose build

Rodar containers:

docker compose up

API disponível em: http://localhost:8080

Container: coupon-api

Parar e remover containers:

docker compose down

🧪 Testes

Unitários: CouponDomainTest, CouponServiceTest

Integração: CouponIntegrationTest com @SpringBootTest + H2

Cobertura >= 80%

Executar testes:

mvn clean test

Verificar cobertura:

mvn clean verify

📑 Decisões técnicas

Domínio encapsulado

Validações (sanitizeCode, validateDiscountValue, validateExpirationDate) dentro de Coupon.

Soft delete implementado com campo deleted + @Where no JPA.

Bean Validation

@NotNull, @Size, @DecimalMin para validação automática pelo Spring.

Ambiente docker

Testes de integração

Cobrem fluxo real: persistência + validações + soft delete.

Swagger/OpenAPI

Documentação de endpoints disponível em localhost:8080/swagger-ui/index.html

Acesso ao banco H2 localhost:8080/h2-console/
Usuario: sa o usuario do banco não possui senha