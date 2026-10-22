#!/usr/bin/env bash
# Registra respostas HTTP reais. Execute com a API ativa e o Oracle configurado.
set -euo pipefail
BASE_URL="${BASE_URL:-http://localhost:8080}"
OUT="${OUT:-evidencias/registro-requisicoes.txt}"
BASE_URL="${BASE_URL%/}"
mkdir -p "$(dirname "$OUT")"
: > "$OUT"
LAST_STATUS=""
LAST_BODY=""
MISMATCHES=0
REQUEST_COUNT=0

request() {
  local title="$1" method="$2" path="$3" body="${4-}" expected="${5-}"
  local url="$BASE_URL$path" response result='Não definido'
  local -a args
  args=(-sS -X "$method" -w $'\n__HTTP_STATUS__:%{http_code}')
  if [[ -n "$body" ]]; then args+=(-H 'Content-Type: application/json' -d "$body"); fi
  response="$(curl "${args[@]}" "$url")"
  LAST_STATUS="${response##*$'\n__HTTP_STATUS__:'}"
  LAST_BODY="${response%$'\n__HTTP_STATUS__:'*}"
  REQUEST_COUNT=$((REQUEST_COUNT + 1))
  if [[ -n "$expected" ]]; then
    if [[ "$LAST_STATUS" == "$expected" ]]; then result='OK'; else result='FALHA'; MISMATCHES=$((MISMATCHES + 1)); fi
  fi
  {
    echo '================================================================'
    echo "$title"
    echo "Data: $(date -Is)"
    echo "Request: $method $url"
    if [[ -n "$body" ]]; then echo "Request body: $body"; fi
    echo "HTTP status: $LAST_STATUS"
    if [[ -n "$expected" ]]; then echo "Status esperado: HTTP $expected; conferência: $result"; fi
    echo 'Response body:'
    if [[ -n "$LAST_BODY" ]]; then printf '%s\n' "$LAST_BODY"; else echo '<empty>'; fi
    echo
  } | tee -a "$OUT"
}

extract_id() {
  printf '%s' "$1" | sed -n 's/.*"id"[[:space:]]*:[[:space:]]*\([0-9][0-9]*\).*/\1/p' | head -n 1
}

request '1. Listar trechos' GET '/api/trechos' '' 200
request '2. Criar trecho' POST '/api/trechos' '{"km":123,"crescimentoVegetacao":85,"tipoTrecho":"umido"}' 201
[[ "$LAST_STATUS" == "201" ]] || { echo "POST trecho deveria retornar 201 (recebido $LAST_STATUS)" >&2; exit 1; }
TRECHO_ID="$(extract_id "$LAST_BODY")"
[[ -n "$TRECHO_ID" ]] || { echo 'Não foi possível obter o id do trecho criado.' >&2; exit 1; }
request '3. Buscar trecho criado' GET "/api/trechos/$TRECHO_ID" '' 200
request '4. Gerar relatório com trecho urgente presente' POST '/api/relatorios' '' 201
request '5. Listar histórico de relatórios' GET '/api/relatorios' '' 200
request '6. Consultar histórico por período' GET '/api/relatorios/periodo?inicio=2020-01-01T00%3A00%3A00&fim=2099-12-31T23%3A59%3A59' '' 200
request '7. Atualizar trecho' PUT "/api/trechos/$TRECHO_ID" '{"km":123,"crescimentoVegetacao":40,"tipoTrecho":"umido"}' 200
request '8. Filtrar trechos por vegetação' GET '/api/trechos/vegetacao?minimo=30' '' 200
request '9. Excluir trecho' DELETE "/api/trechos/$TRECHO_ID" '' 204
request '10. Confirmar trecho excluído' GET "/api/trechos/$TRECHO_ID" '' 404
request '11. Rejeitar vegetação negativa' POST '/api/trechos' '{"km":124,"crescimentoVegetacao":-1,"tipoTrecho":"seco"}' 400

request '12. Criar equipe' POST '/api/equipes' '{"nomeEquipe":"Equipe de Evidencia","responsavel":"Responsavel de Teste"}' 201
[[ "$LAST_STATUS" == "201" ]] || { echo "POST equipe deveria retornar 201 (recebido $LAST_STATUS)" >&2; exit 1; }
EQUIPE_ID="$(extract_id "$LAST_BODY")"
[[ -n "$EQUIPE_ID" ]] || { echo 'Não foi possível obter o id da equipe criada.' >&2; exit 1; }
request '13. Buscar equipe criada' GET "/api/equipes/$EQUIPE_ID" '' 200
request '14. Atualizar equipe' PUT "/api/equipes/$EQUIPE_ID" '{"nomeEquipe":"Equipe Atualizada","responsavel":"Responsavel de Teste"}' 200
request '15. Excluir equipe' DELETE "/api/equipes/$EQUIPE_ID" '' 204

request '16. Criar intervenção' POST '/api/intervencoes' '{"tipo":"Rocada Mecanizada","descricao":"Intervencao criada no teste de API"}' 201
[[ "$LAST_STATUS" == "201" ]] || { echo "POST intervenção deveria retornar 201 (recebido $LAST_STATUS)" >&2; exit 1; }
INTERVENCAO_ID="$(extract_id "$LAST_BODY")"
[[ -n "$INTERVENCAO_ID" ]] || { echo 'Não foi possível obter o id da intervenção criada.' >&2; exit 1; }
request '17. Buscar intervenção criada' GET "/api/intervencoes/$INTERVENCAO_ID" '' 200
request '18. Atualizar intervenção' PUT "/api/intervencoes/$INTERVENCAO_ID" '{"tipo":"Pulverizacao","descricao":"Intervencao atualizada no teste"}' 200
request '19. Excluir intervenção' DELETE "/api/intervencoes/$INTERVENCAO_ID" '' 204

echo "Resumo do script: $((REQUEST_COUNT - MISMATCHES))/$REQUEST_COUNT status esperados conferidos como OK; divergências: $MISMATCHES." | tee -a "$OUT"
echo "Registro gravado em: $OUT"
if [[ "$MISMATCHES" -gt 0 ]]; then echo 'Há status inesperados; corrija os erros antes de usar o registro como evidência.' >&2; exit 1; fi
