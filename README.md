# Tools Challenge - API REST de Pagamentos

API REST desenvolvida em Java/Spring Boot para processamento de pagamentos com cartão de crédito, conforme o desafio **Tools Java Challenge**.

A aplicação permite **realizar pagamentos**, **consultar transações** e **estornar pagamentos autorizados**, utilizando JSON e protocolo HTTP.

## Arquitetura

A API segue uma arquitetura em camadas: o **controller** expõe os endpoints REST, o **service** concentra as regras de negócio e validações, e o **repository** persiste as entidades via JPA. DTOs de entrada e saída ficam separados das entidades de domínio, com conversão feita pelo **mapper**. Erros de negócio são tratados de forma centralizada pelo **GlobalExceptionHandler**, mapeando exceções customizadas para respostas HTTP padronizadas.

---

## Tecnologias

- Java 17
- Spring Boot 3.5
- Spring Data JPA
- H2 Database (memória)
- Lombok
- SpringDoc OpenAPI (Swagger)
- JUnit 5 + Mockito + MockMvc

---

## Como executar

### Pré-requisitos

- JDK 17+
- Maven 3.8+ (ou use o Maven Wrapper incluído no projeto)

### Subir a aplicação

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
mvnw.cmd spring-boot:run
```

A API ficará disponível em: `http://localhost:8080`

### Executar os testes

```bash
./mvnw test
```

---

## Documentação interativa (Swagger)

Com a aplicação rodando, acesse:

**http://localhost:8080/swagger-ui/index.html**

Os endpoints estão numerados (1 → 4) para facilitar o teste do fluxo completo. Cada operação já possui exemplos de JSON prontos para execução.

---

## Console H2

Para visualizar os dados persistidos em memória:

**http://localhost:8080/h2-console**

| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:mem:toolschallenge` |
| User | `sa` |
| Password | *(vazio)* |

> **Persistência em memória:** o banco H2 roda em memória com `create-drop`. Os dados existem apenas enquanto a aplicação está em execução.

---

## Endpoints

| # | Método | URL | HTTP | Descrição |
|---|---|---|---|---|
| 1 | `POST` | `/pagamentos` | `201` | Realizar pagamento |
| 2 | `GET` | `/pagamentos/{id}` | `200` | Consultar transação por ID |
| 3 | `POST` | `/pagamentos/{id}/estorno` | `200` | Estornar transação |
| 4 | `GET` | `/pagamentos` | `200` | Listar todas as transações |

---

## Exemplos de requisição e resposta

### 1. Pagamento

**Request**

```http
POST /pagamentos
Content-Type: application/json
```

```json
{
  "transacao": {
    "cartao": "4444********1234",
    "id": "100023568900001",
    "descricao": {
      "valor": 500.50,
      "dataHora": "01/05/2021 18:30:00",
      "estabelecimento": "PetShop Mundo cão"
    },
    "formaPagamento": {
      "tipo": "AVISTA",
      "parcelas": 1
    }
  }
}
```

**Response — HTTP 201 Created**

O header `Location` aponta para `GET /pagamentos/{id}` da transação criada.

**Autorizado**

```json
{
  "transacao": {
    "cartao": "4444********1234",
    "id": "100023568900001",
    "descricao": {
      "valor": 500.50,
      "dataHora": "01/05/2021 18:30:00",
      "estabelecimento": "PetShop Mundo cão",
      "nsu": "1234567890",
      "codigoAutorizacao": "147258369",
      "status": "AUTORIZADO"
    },
    "formaPagamento": {
      "tipo": "AVISTA",
      "parcelas": 1
    }
  }
}
```

> `nsu` e `codigoAutorizacao` são gerados aleatoriamente em pagamentos autorizados.

**Negado** (valor nulo ou ≤ 0 — transação é salva e retorna `201`)

```json
{
  "transacao": {
    "cartao": "4444********1234",
    "id": "100023568900001",
    "descricao": {
      "valor": 0,
      "dataHora": "01/05/2021 18:30:00",
      "estabelecimento": "PetShop Mundo cão",
      "nsu": null,
      "codigoAutorizacao": null,
      "status": "NEGADO"
    },
    "formaPagamento": {
      "tipo": "AVISTA",
      "parcelas": 1
    }
  }
}
```

### 2. Consulta por ID

```http
GET /pagamentos/100023568900001
```

**Response — HTTP 200 OK**

Mesma estrutura do pagamento:

```json
{
  "transacao": {
    "cartao": "4444********1234",
    "id": "100023568900001",
    "descricao": {
      "valor": 500.50,
      "dataHora": "01/05/2021 18:30:00",
      "estabelecimento": "PetShop Mundo cão",
      "nsu": "1234567890",
      "codigoAutorizacao": "147258369",
      "status": "AUTORIZADO"
    },
    "formaPagamento": {
      "tipo": "AVISTA",
      "parcelas": 1
    }
  }
}
```

### 3. Estorno

```http
POST /pagamentos/100023568900001/estorno
```

**Response — HTTP 200 OK**

Mesma estrutura do pagamento, com `status` alterado para `CANCELADO`:

```json
{
  "transacao": {
    "cartao": "4444********1234",
    "id": "100023568900001",
    "descricao": {
      "valor": 500.50,
      "dataHora": "01/05/2021 18:30:00",
      "estabelecimento": "PetShop Mundo cão",
      "nsu": "1234567890",
      "codigoAutorizacao": "147258369",
      "status": "CANCELADO"
    },
    "formaPagamento": {
      "tipo": "AVISTA",
      "parcelas": 1
    }
  }
}
```

### 4. Listar transações

```http
GET /pagamentos
```

**Response — HTTP 200 OK**

Lista de transações:

```json
[
  {
    "transacao": {
      "cartao": "4444********1234",
      "id": "100023568900001",
      "descricao": {
        "valor": 500.50,
        "dataHora": "01/05/2021 18:30:00",
        "estabelecimento": "PetShop Mundo cão",
        "nsu": "1234567890",
        "codigoAutorizacao": "147258369",
        "status": "AUTORIZADO"
      },
      "formaPagamento": {
        "tipo": "AVISTA",
        "parcelas": 1
      }
    }
  }
]
```

---

## Regras de negócio

### Status da transação

| Status | Descrição                                                                   |
|---|-----------------------------------------------------------------------------|
| `AUTORIZADO` | Pagamento aprovado: gera `nsu` e `codigoAutorizacao`                        |
| `NEGADO` | Pagamento recusado (ex.: valor inválido): sem `nsu` nem `codigoAutorizacao` |
| `CANCELADO` | Transação estornada                                                         |

### Tipos de pagamento

| Tipo | Parcelas |
|---|---|
| `AVISTA` | Exatamente 1 |
| `PARCELADO LOJA` | Maior que 1 |
| `PARCELADO EMISSOR` | Maior que 1 |

Combinações inválidas de tipo e parcelas retornam HTTP **400** (não geram status `NEGADO`).

### Outras regras

- O campo `id` da transação deve ser **único**
- Pagamento com **valor nulo ou ≤ 0** retorna status `NEGADO`, mas a transação é **persistida** com HTTP **201**
- Tentativa de pagamento com **ID duplicado** retorna HTTP **409**
- Estorno só é permitido para transações **AUTORIZADAS** (transações `NEGADAS` ou já `CANCELADAS` retornam HTTP **400**)
- Campos obrigatórios ausentes retornam HTTP **400**
- `parcelas` menor que 1 retorna HTTP **400**
- Valores inválidos de enum (`status`, `tipo`) retornam HTTP **400**

---

## Tratamento de erros

Erros são retornados no formato:

```json
{
  "timestamp": "2026-06-19T10:30:00",
  "status": 400,
  "mensagem": "Campo obrigatório: id"
}
```

| HTTP | Situação | Exemplo de mensagem |
|---|---|---|
| `400` | Campo obrigatório ausente | `Campo obrigatório: id` |
| `400` | Tipo/parcelas inválidos | `Pagamento à vista deve ter exatamente 1 parcela.` |
| `400` | Parcelas inválidas | `O número de parcelas deve ser maior ou igual a 1.` |
| `400` | Estorno não permitido | `Transação já está cancelada.` |
| `400` | Estorno de transação negada | `Não é possível estornar uma transação negada.` |
| `404` | Transação não encontrada | `Transação não encontrada para o id: 100023568900001` |
| `409` | ID duplicado | `Já existe uma transação cadastrada com o id: 100023568900001` |

---

## Estrutura do projeto

```
src/main/java/com/toolschallenge/tools_challenge/
├── config/openapi/       # Configuração Swagger e exemplos
│   └── doc/              # Anotações OpenAPI por endpoint
├── controller/           # Endpoints REST
├── dto/
│   ├── request/          # DTOs de entrada
│   └── response/         # DTOs de saída
├── entity/               # Entidades JPA
├── enums/                # Status e tipos de pagamento
├── exception/            # Exceções e handler global
├── mapper/               # Conversão entre DTO e entidade
├── repository/           # Acesso ao banco
├── service/              # Regras de negócio
└── util/                 # Utilitários (geração de NSU/código)
```

---

## Testes

| Tipo | Classe | Responsabilidade |
|---|---|---|
| Unitário | `TransacaoServiceTest` | Regras de negócio e validações |
| Unitário | `TransacaoMapperTest` | Conversão entre DTO e entidade |
| Controller | `PagamentoControllerTest` | Status HTTP e contrato REST |
| Integração | `PagamentoIntegrationTest` | Fluxo completo com banco H2 |

