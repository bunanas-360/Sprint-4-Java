# Relatório acadêmico — Challenge MOTIVA, Sprint 4

## Objetivo

Migrar o protótipo de console da MOTIVA, com persistência via JDBC na Sprint 3, para uma API REST organizada em camadas, usando Spring Boot, Spring Data JPA e Java 17, preservando o modelo de domínio e as tabelas Oracle existentes sempre que compatíveis.

## Arquitetura

A aplicação separa as responsabilidades em `model`, `repository`, `service`, `controller`, `dto` e `exception`. As entidades JPA mapeiam as tabelas; os repositories declaram operações de persistência por `JpaRepository`; os services concentram validações e regras de negócio; os controllers recebem requisições HTTP e devolvem JSON; DTOs definem os contratos de entrada e saída; e `ApiExceptionHandler` padroniza erros.

## Recursos implementados

A API expõe CRUD para trechos rodoviários, equipes de manutenção e intervenções operacionais. Os endpoints retornam códigos HTTP específicos para criação (`201`), consulta e atualização (`200`), exclusão (`204`), recurso inexistente (`404`) e requisições inválidas (`400`). O contrato inclui Bean Validation, DTOs e tratamento global de erros.

O `GeradorRelatorioService` aplica os limiares preservados do sistema anterior: URGENTE a partir de 80; CRÍTICO a partir de 50 e abaixo de 80; ATENÇÃO a partir de 30 e abaixo de 50; NORMAL abaixo de 30. O serviço conta os trechos por categoria, constrói um resumo e grava o relatório em `RELATORIO_PRIORIDADE`. A API disponibiliza geração de relatório, consulta ao histórico, pesquisa por ID e filtro por período.

## Queries derivadas

Os repositories incluem consultas derivadas pelo nome do método, como `findByCrescimentoVegetacaoGreaterThanEqual(BigDecimal minimo)`, `findByTipoTrechoIgnoreCase`, `findByNomeEquipeContainingIgnoreCase` e `findByDataGeracaoBetweenOrderByDataGeracaoDesc`. Não foi inserido SQL manual em Services ou Controllers nem código JDBC manual (`Connection`, `PreparedStatement` ou `ResultSet`) nas camadas migradas.

## Banco de dados e estratégia de IDs

Os scripts de referência presentes no pacote modelam as quatro tabelas com colunas `IDENTITY`, e não incluem sequences nomeadas. Para usar `@SequenceGenerator` sem recriar as tabelas, foi preparado `sql/sprint4-sequences.sql`, que cria sequences explícitas a partir do maior ID existente. Essa migração deve ser conferida pela equipe e executada uma única vez no schema Oracle correto; não deve ser tratada como uma sequência que já existia na Sprint 3. O Hibernate utiliza `ddl-auto=validate`, portanto valida a estrutura, em vez de recriar as tabelas.

## Estratégia de testes

A suíte `MotivaApiIntegrationTest` usa Spring Boot + MockMvc e H2 em memória, sem acessar a base Oracle. Ela foi preparada para verificar o CRUD das três entidades principais, códigos HTTP, rejeição de vegetação negativa, persistência e consulta do histórico de relatórios e rejeição de intervalos de datas inválidos. Os scripts em `scripts/` fazem chamadas HTTP reais contra uma instância ativa e registram códigos e corpos de resposta.

**Estado de execução:** a suíte não foi executada durante a preparação deste pacote. Neste ambiente, o Java está presente, mas Maven não está instalado e a conexão com Maven Central não estava disponível. A conexão com o Oracle da equipe também depende das credenciais locais. Assim, este relatório não declara `BUILD SUCCESS`, aprovação dos testes ou respostas HTTP como resultados reais. Consulte `RELATORIO_TESTES.md` para executar e registrar os resultados observados.

## Conclusão

A implementação transforma as operações de persistência repetitivas da versão JDBC em interfaces Spring Data JPA e expõe as funcionalidades por HTTP/JSON. A conclusão da validação prática requer executar os testes automatizados, iniciar a aplicação conectada ao Oracle, verificar as respostas dos endpoints, guardar as evidências e publicar o histórico de commits real no GitHub.
