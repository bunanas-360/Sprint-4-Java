# Evidências reais de execução

Não há capturas ou respostas HTTP pré-preenchidas nesta pasta. Elas precisam ser produzidas ao executar o projeto no ambiente da equipe.

## 1. Testes automatizados

No PowerShell, na raiz do projeto:

```powershell
.\scripts\executar-testes.ps1
```

O script grava a saída real em `resultado-mvn-test.txt`.

## 2. Testes da API REST

Com a API iniciada e conectada ao Oracle, em outro PowerShell:

```powershell
.\scripts\validar-api.ps1
```

O script faz 19 requisições, incluindo CRUD de trechos, equipes e intervenções, validação de dados e geração/consulta de relatórios. As respostas recebidas são escritas em `registro-requisicoes.txt`. Também pode ser usado `bash scripts/validar-api.sh` em ambiente Bash.

## 3. Capturas de tela

Use `Win + Shift + S` para registrar pelo menos seis requisições no Postman ou terminal. Deixe visíveis a requisição, a URL, o status HTTP e o corpo da resposta. Sugestão de nomes:

- `01-listar-trechos.png`
- `02-criar-trecho-201.png`
- `03-atualizar-trecho-200.png`
- `04-excluir-trecho-204.png`
- `05-buscar-trecho-excluido-404.png`
- `06-gerar-relatorio-201.png`
- `07-historico-relatorios-200.png`

Não crie respostas ilustrativas e as apresente como capturas reais. Resultados esperados estão descritos em `RELATORIO_TESTES.md`.
