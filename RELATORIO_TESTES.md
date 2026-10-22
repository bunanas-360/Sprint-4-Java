# Relatório de testes — MOTIVA Sprint 4

## Objetivo

Descrever como verificar de forma reproduzível a API REST e registrar os resultados efetivamente observados, sem tratar resultados esperados como se tivessem ocorrido.

## Estado ao preparar este pacote

**Execução de `mvn test`: não realizada neste ambiente.** O Java está disponível, mas o comando `mvn` não está instalado e não foi possível acessar o Maven Central para obter dependências. Por isso, não há um resultado de compilação ou teste automatizado que possa ser declarado como aprovado.

**Execução contra Oracle FIAP: não realizada neste ambiente.** Não foram fornecidas credenciais de banco para uma conexão de teste. Nenhum código HTTP abaixo é apresentado como resultado de uma execução real.

Foram revisados estaticamente a estrutura de camadas, o POM, os arquivos JSON da coleção Postman, as rotas declaradas, os mapeamentos JPA e os scripts de teste. A revisão estática não substitui a compilação, os testes de integração ou a validação do schema Oracle.

## Como registrar os testes automatizados

Com Java 17 e Maven instalados, na pasta do projeto execute:

```powershell
.\scripts\executar-testes.ps1
```

O script executa `mvn test` e grava a saída real em `evidencias/resultado-mvn-test.txt`. Um código de saída `0` significa que o Maven concluiu o comando sem falhas; consulte também o resumo do Surefire para verificar a contagem de testes e falhas.

Os testes automatizados estão em `src/test/java/br/com/motiva/MotivaApiIntegrationTest.java` e usam H2 em memória, sem acessar o Oracle. A suíte cobre:

1. CRUD de trechos e respostas HTTP, incluindo `404` depois da exclusão;
2. rejeição de crescimento de vegetação negativo (`400`);
3. CRUD de equipes;
4. CRUD de intervenções;
5. geração, persistência, histórico e consulta por período do relatório;
6. rejeição de período em que `inicio` é posterior a `fim` (`400`).

Essa lista descreve o que os testes verificam, não afirma que já passaram.

## Como registrar chamadas reais da API

1. Configure usuário, senha e URL Oracle pelas variáveis de ambiente descritas no `README.md`.
2. Confira o schema existente e a necessidade das sequences antes de executar `sql/sprint4-sequences.sql`. A migração é de execução única e deve ser revisada pela equipe antes de rodar em um schema com dados.
3. Inicie a aplicação com `mvn spring-boot:run` e confirme que a inicialização termina sem erro.
4. Em outro terminal, rode `.\scripts\validar-api.ps1` no PowerShell ou `bash scripts/validar-api.sh` no Bash.
5. Leia `evidencias/registro-requisicoes.txt`. O arquivo é gerado no momento da execução e guarda as respostas HTTP recebidas, os corpos de resposta e a comparação com os status esperados. Se o script falhar, investigue a saída; não remova os registros de erro para apresentar um resultado como aprovado.
6. Capture pelo menos seis telas reais no Postman ou terminal, com método, URL, status HTTP e JSON visíveis. Salve as imagens dentro de `evidencias/`.

## Matriz de verificação manual

| Caso | Status esperado | Evidência necessária |
|---|---:|---|
| Listar trechos | 200 | Captura da resposta JSON |
| Criar trecho | 201 | Corpo criado e cabeçalho `Location` |
| Buscar trecho criado | 200 | JSON com o mesmo ID |
| Atualizar trecho | 200 | JSON com os campos atualizados |
| Excluir trecho | 204 | Resposta sem corpo |
| Buscar trecho excluído | 404 | JSON de erro padronizado |
| Criar equipe | 201 | Corpo criado |
| Criar intervenção | 201 | Corpo criado |
| Gerar relatório | 201 | Resumo e contagens persistidas |
| Consultar histórico | 200 | Lista contendo o relatório criado |
| Consultar período | 200 | Lista filtrada pelo período |
| Vegetação negativa | 400 | Erro de validação |

Os códigos são esperados de acordo com o contrato implementado e só devem ser marcados como resultados obtidos quando aparecerem na saída real.

## Critério de conclusão

A validação estará concluída apenas quando `mvn test` terminar com sucesso, a API iniciar com o Oracle da equipe, as requisições reais apresentarem os status esperados e as capturas forem salvas. Atualize este relatório com a data, saída e observações depois de executar os testes no ambiente da equipe.
