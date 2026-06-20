package com.toolschallenge.tools_challenge.config.openapi;

public final class OpenApiExamples {

    private OpenApiExamples() {
    }

    public static final String ID_TRANSACAO = "100023568900001";

    public static final String PAGAMENTO_REQUEST = """
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
            """;

    public static final String PAGAMENTO_RESPONSE = """
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
            """;

    public static final String ESTORNO_RESPONSE = """
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
            """;

    public static final String LISTA_RESPONSE = """
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
            """;
}
