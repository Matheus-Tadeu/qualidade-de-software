# language: pt

Funcionalidade: Mensagem

  @smoke @high
  Cenário: Registrar Mensagem
    Quando registrar uma nova mensagem
    Então a mensagem é registrada com sucesso
    E deve ser apresentada

  @smoke @high
  Cenário: Buscar Mensagem
    Dado que uma mensagem já foi publicada
    Quando efetuar a buscar da mensagem
    Então a mensagem é exibida com sucesso

  @low
  Cenario: Alterar Mensagem
    Dado que uma mensagem já foi publicada
    Quando efetuar requisição para alterar mensagem
    Então a mensagem é atualizada com sucesso
    E deve ser apresentada

  @high
  Cenario: Remover Mensagem
    Dado que uma mensagem já foi publicada
    Quando requisitar a remoção da mensagem
    Então a mensagem é removida com sucesso