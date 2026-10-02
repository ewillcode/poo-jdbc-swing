# Roadmap: Sistema de Material de Construção

## Situação em 02/10/2026

O sistema está **funcional e cumpre todos os requisitos**. Também já estão prontos os materiais da 1ª etapa (veja o [README](README.md)).

| Requisito | Status |
| --- | --- |
| Integração com banco de dados | ✅ SQLite via JDBC |
| 4 ou mais tabelas | ✅ 5: `cliente`, `vendedor`, `produto`, `venda`, `item_venda` |
| Várias telas | ✅ 7 telas |
| Padrão MVC | ✅ `model` / `dao` / `controller` / `view`, e as telas só falam com os controllers |
| 1ª etapa: tema | ✅ Material de Construção |
| 1ª etapa: protótipos de tela | ✅ `docs/prototipos/` (prints das telas reais) |
| 1ª etapa: diagrama de classes | ✅ `docs/diagramas/diagrama-classes.png` |
| 1ª etapa: diagrama MER | ✅ `docs/diagramas/mer.png` |
| Testes automatizados | ✅ 13 testes (`mvn test`) |

### O que foi feito no refinamento (branch `refinamento`)

- Projeto Maven movido para a raiz do repositório. Agora basta abrir a pasta do repo no IntelliJ.
- Removidos do git a pasta `target/` (arquivos compilados) e o `descripton.txt.txt`; criado o `.gitignore`.
- Código reescrito de forma legível: uma instrução por linha, nomes claros, comentários explicando o porquê.
- Um controller por assunto: `ClienteController`, `VendedorController`, `ProdutoController`, `VendaController`.
- Telas separadas: `ClienteFrame` e `VendedorFrame`, no lugar do antigo `PessoaFrame` com `if`.
- Bugs corrigidos:
  - CPF agora tem os dígitos verificadores validados;
  - a busca aceita CPF com pontuação;
  - o preço aceita `1.250,00` e dá a mensagem certa quando tem mais de 2 casas;
  - CPF aparece com máscara e os valores aparecem em R$.
- Dados de exemplo inseridos automaticamente quando o banco está vazio.
- Protótipos, diagrama de classes, MER e diagrama da arquitetura gerados em `docs/`.

---

## Divisão em 3 partes

Como o código foi todo reescrito, a prioridade agora é **cada um dominar a sua parte** para a apresentação. A divisão segue as fatias do sistema: cada pessoa passa pelas 4 camadas do MVC (tela → controller → DAO → tabela).

> **Pessoa 1:** ________ · **Pessoa 2:** ________ · **Pessoa 3:** ________

### Pessoa 1: Clientes e Vendedores

**Arquivos:** `model/Cliente`, `model/Vendedor`, `controller/ClienteController`, `controller/VendedorController`, `controller/Validacoes` (nome e CPF), `dao/ClienteDao`, `dao/VendedorDao`, `view/ClienteFrame`, `view/VendedorFrame`, `view/ConsultaClientesFrame`.

- [ ] Ler e entender os arquivos acima. Saber explicar:
  - como a busca por nome **ou** CPF funciona no SQL (`ClienteDao.buscar`);
  - como o algoritmo dos dígitos verificadores do CPF funciona (`Validacoes.cpf`);
  - por que o CPF é guardado sem pontuação e só formatado na tela.
- [ ] Testar na mão: cadastrar, editar, excluir, CPF inválido, CPF repetido, excluir cliente que já comprou.
- [ ] **Melhoria opcional:** em *Consultar Clientes*, mostrar quantas compras o cliente fez e o total gasto (`LEFT JOIN venda` + `GROUP BY`).
- [ ] **Melhoria opcional:** campo de CPF com máscara automática (`JFormattedTextField` + `MaskFormatter`).

### Pessoa 2: Produtos e Venda

**Arquivos:** `model/Produto`, `model/Venda`, `model/ItemVenda`, `controller/ProdutoController`, `controller/VendaController`, `controller/Validacoes` (preço), `dao/ProdutoDao`, `dao/VendaDao`, `view/ProdutoFrame`, `view/VendaFrame`.

- [ ] Ler e entender os arquivos acima. Saber explicar:
  - a **transação** em `VendaDao.inserir` (`setAutoCommit(false)`, `commit`, `rollback`) e por que ela é necessária;
  - por que o `item_venda` guarda o `preco_unitario` (histórico de preço);
  - como o carrinho soma a quantidade quando o mesmo produto é adicionado de novo.
- [ ] Testar na mão: preço com vírgula, preço com 3 casas, venda sem cliente, venda sem itens, adicionar o mesmo produto 2 vezes.
- [ ] **Melhoria opcional:** controle de **estoque** (coluna `estoque` em `produto`, baixa ao finalizar a venda, bloqueio se faltar).
- [ ] **Melhoria opcional:** perguntar antes de fechar a tela de venda com o carrinho cheio.

### Pessoa 3: Hub, Listar Vendas, infraestrutura e entrega

**Arquivos:** `Main`, `DadosExemplo`, `dao/Database`, `dao/BancoDeDadosException`, `view/HubFrame`, `view/BaseFrame`, `view/Formatos`, `view/ListarVendasFrame`, `pom.xml`, `README.md`, `docs/`.

- [ ] Ler e entender os arquivos acima. Saber explicar:
  - como o `Main` liga as camadas (cria DAOs → controllers → telas);
  - o `CREATE TABLE` de cada tabela, as chaves estrangeiras e por que existe o `PRAGMA foreign_keys = ON`;
  - como o Hub some e reaparece quando uma tela é aberta ou fechada (`BaseFrame`).
- [ ] **Entrega da 1ª etapa:** montar o documento para o professor (PDF ou o formato pedido) com tema, protótipos, diagrama de classes e MER. Tudo já está em `docs/`.
- [ ] Abrir o Pull Request de `refinamento` → `main` e avisar o grupo para atualizar (`git pull`).
- [ ] Testar o sistema no **Windows**, onde o visual muda porque usa o tema do sistema.
- [ ] **Melhoria opcional:** filtrar a lista de vendas por cliente ou por período.

---

## Cronograma sugerido

| Fase | O quê | Quem |
| --- | --- | --- |
| 1 | Merge da branch `refinamento` e todo mundo atualiza o projeto | Pessoa 3 |
| 2 | **Entregar a 1ª etapa** (documento com protótipos + diagramas) | Pessoa 3, com revisão dos outros |
| 3 | Cada um estuda e testa a sua parte | Todos |
| 4 | Melhorias opcionais (cada um na sua branch, com `mvn test` passando antes do PR) | Todos |
| 5 | Ensaio da apresentação: cada um demonstra a sua parte e explica o fluxo tela → controller → DAO → tabela | Todos |

## Regras do time

- Uma branch por pessoa e Pull Request para a `main`.
- `Validacoes` e `BaseFrame` são compartilhados: avisem antes de mexer.
- Se mudar uma tela, atualizem o print em `docs/prototipos/`. Se mudar uma tabela, atualizem o `mer.mmd` e o `mer.png`.
