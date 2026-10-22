param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$OutputFile = "evidencias/registro-requisicoes.txt"
)

$ErrorActionPreference = "Stop"
$BaseUrl = $BaseUrl.TrimEnd('/')
$fullOutput = [System.IO.Path]::GetFullPath($OutputFile)
$outputDir = Split-Path -Parent $fullOutput
if ($outputDir) { New-Item -ItemType Directory -Force -Path $outputDir | Out-Null }
if (Test-Path $fullOutput) { Remove-Item $fullOutput -Force }

Add-Type -AssemblyName System.Net.Http
$client = [System.Net.Http.HttpClient]::new()
$client.Timeout = [TimeSpan]::FromSeconds(30)
$script:lastStatus = 0
$script:lastBody = ""
$script:mismatches = 0

function Invoke-RecordedRequest {
    param(
        [Parameter(Mandatory=$true)][string]$Title,
        [Parameter(Mandatory=$true)][ValidateSet('GET','POST','PUT','DELETE')][string]$Method,
        [Parameter(Mandatory=$true)][string]$Path,
        [string]$Body = $null,
        [int]$ExpectedStatus = 0
    )

    $uri = if ($Path.StartsWith('http')) { $Path } else { "$BaseUrl$Path" }
    $request = [System.Net.Http.HttpRequestMessage]::new([System.Net.Http.HttpMethod]::new($Method), $uri)
    if ($null -ne $Body) {
        $request.Content = [System.Net.Http.StringContent]::new($Body, [System.Text.Encoding]::UTF8, "application/json")
    }

    $response = $client.SendAsync($request).GetAwaiter().GetResult()
    $bodyText = if ($null -eq $response.Content) { "" } else { $response.Content.ReadAsStringAsync().GetAwaiter().GetResult() }
    $statusCode = [int]$response.StatusCode
    $resultText = if ($ExpectedStatus -eq 0) { "Não definido" } elseif ($statusCode -eq $ExpectedStatus) { "OK" } else { "FALHA" }

    Add-Content -LiteralPath $fullOutput -Encoding UTF8 "================================================================"
    Add-Content -LiteralPath $fullOutput -Encoding UTF8 $Title
    Add-Content -LiteralPath $fullOutput -Encoding UTF8 ("Data: " + (Get-Date -Format o))
    Add-Content -LiteralPath $fullOutput -Encoding UTF8 ("Request: {0} {1}" -f $Method, $uri)
    if ($null -ne $Body) { Add-Content -LiteralPath $fullOutput -Encoding UTF8 ("Request body: " + $Body) }
    Add-Content -LiteralPath $fullOutput -Encoding UTF8 ("HTTP status: {0} {1}" -f $statusCode, $response.ReasonPhrase)
    if ($ExpectedStatus -ne 0) {
        Add-Content -LiteralPath $fullOutput -Encoding UTF8 ("Status esperado: HTTP {0}; conferência: {1}" -f $ExpectedStatus, $resultText)
        if ($statusCode -ne $ExpectedStatus) { $script:mismatches++ }
    }
    foreach ($header in $response.Headers) {
        Add-Content -LiteralPath $fullOutput -Encoding UTF8 ("Header {0}: {1}" -f $header.Key, ($header.Value -join ', '))
    }
    if ($null -ne $response.Content) {
        foreach ($header in $response.Content.Headers) {
            Add-Content -LiteralPath $fullOutput -Encoding UTF8 ("Header {0}: {1}" -f $header.Key, ($header.Value -join ', '))
        }
    }
    Add-Content -LiteralPath $fullOutput -Encoding UTF8 "Response body:"
    if ([string]::IsNullOrWhiteSpace($bodyText)) {
        Add-Content -LiteralPath $fullOutput -Encoding UTF8 "<empty>"
    } else {
        Add-Content -LiteralPath $fullOutput -Encoding UTF8 $bodyText
    }
    Add-Content -LiteralPath $fullOutput -Encoding UTF8 ""

    $script:lastStatus = $statusCode
    $script:lastBody = $bodyText
    $request.Dispose()
    $response.Dispose()
    return $bodyText
}

try {
    Write-Host "Testando API em $BaseUrl. As respostas serão registradas em $fullOutput"

    Invoke-RecordedRequest -Title "1. Listar trechos" -Method GET -Path "/api/trechos" -ExpectedStatus 200 | Out-Null
    $body = Invoke-RecordedRequest -Title "2. Criar trecho" -Method POST -Path "/api/trechos" -Body '{"km":123,"crescimentoVegetacao":85,"tipoTrecho":"umido"}' -ExpectedStatus 201
    if ($script:lastStatus -ne 201) { throw "POST de trecho não retornou 201; revise o log." }
    $trechoId = [long](($body | ConvertFrom-Json).id)
    Invoke-RecordedRequest -Title "3. Buscar trecho criado" -Method GET -Path "/api/trechos/$trechoId" -ExpectedStatus 200 | Out-Null
    Invoke-RecordedRequest -Title "4. Gerar relatório com trecho urgente presente" -Method POST -Path "/api/relatorios" -ExpectedStatus 201 | Out-Null
    Invoke-RecordedRequest -Title "5. Listar histórico de relatórios" -Method GET -Path "/api/relatorios" -ExpectedStatus 200 | Out-Null
    Invoke-RecordedRequest -Title "6. Consultar histórico por período" -Method GET -Path "/api/relatorios/periodo?inicio=2020-01-01T00%3A00%3A00&fim=2099-12-31T23%3A59%3A59" -ExpectedStatus 200 | Out-Null
    Invoke-RecordedRequest -Title "7. Atualizar trecho" -Method PUT -Path "/api/trechos/$trechoId" -Body '{"km":123,"crescimentoVegetacao":40,"tipoTrecho":"umido"}' -ExpectedStatus 200 | Out-Null
    Invoke-RecordedRequest -Title "8. Filtrar trechos por vegetação" -Method GET -Path "/api/trechos/vegetacao?minimo=30" -ExpectedStatus 200 | Out-Null
    Invoke-RecordedRequest -Title "9. Excluir trecho" -Method DELETE -Path "/api/trechos/$trechoId" -ExpectedStatus 204 | Out-Null
    Invoke-RecordedRequest -Title "10. Confirmar trecho excluído" -Method GET -Path "/api/trechos/$trechoId" -ExpectedStatus 404 | Out-Null
    Invoke-RecordedRequest -Title "11. Rejeitar vegetação negativa" -Method POST -Path "/api/trechos" -Body '{"km":124,"crescimentoVegetacao":-1,"tipoTrecho":"seco"}' -ExpectedStatus 400 | Out-Null

    $body = Invoke-RecordedRequest -Title "12. Criar equipe" -Method POST -Path "/api/equipes" -Body '{"nomeEquipe":"Equipe de Evidencia","responsavel":"Responsavel de Teste"}' -ExpectedStatus 201
    if ($script:lastStatus -ne 201) { throw "POST de equipe não retornou 201; revise o log." }
    $equipeId = [long](($body | ConvertFrom-Json).id)
    Invoke-RecordedRequest -Title "13. Buscar equipe criada" -Method GET -Path "/api/equipes/$equipeId" -ExpectedStatus 200 | Out-Null
    Invoke-RecordedRequest -Title "14. Atualizar equipe" -Method PUT -Path "/api/equipes/$equipeId" -Body '{"nomeEquipe":"Equipe Atualizada","responsavel":"Responsavel de Teste"}' -ExpectedStatus 200 | Out-Null
    Invoke-RecordedRequest -Title "15. Excluir equipe" -Method DELETE -Path "/api/equipes/$equipeId" -ExpectedStatus 204 | Out-Null

    $body = Invoke-RecordedRequest -Title "16. Criar intervenção" -Method POST -Path "/api/intervencoes" -Body '{"tipo":"Rocada Mecanizada","descricao":"Intervencao criada no teste de API"}' -ExpectedStatus 201
    if ($script:lastStatus -ne 201) { throw "POST de intervenção não retornou 201; revise o log." }
    $intervencaoId = [long](($body | ConvertFrom-Json).id)
    Invoke-RecordedRequest -Title "17. Buscar intervenção criada" -Method GET -Path "/api/intervencoes/$intervencaoId" -ExpectedStatus 200 | Out-Null
    Invoke-RecordedRequest -Title "18. Atualizar intervenção" -Method PUT -Path "/api/intervencoes/$intervencaoId" -Body '{"tipo":"Pulverizacao","descricao":"Intervencao atualizada no teste"}' -ExpectedStatus 200 | Out-Null
    Invoke-RecordedRequest -Title "19. Excluir intervenção" -Method DELETE -Path "/api/intervencoes/$intervencaoId" -ExpectedStatus 204 | Out-Null

    $okCount = 19 - $script:mismatches
    Add-Content -LiteralPath $fullOutput -Encoding UTF8 ("Resumo do script: {0}/19 status esperados conferidos como OK; divergências: {1}." -f $okCount, $script:mismatches)
    if ($script:mismatches -gt 0) { throw "Foram encontrados $script:mismatches status HTTP inesperados. Consulte o arquivo de registro." }
    Write-Host "Os 19 status esperados foram conferidos. Consulte o log e confirme as respostas antes de usar como evidência."
    Write-Host "Registro salvo em: $fullOutput"
}
catch {
    Add-Content -LiteralPath $fullOutput -Encoding UTF8 ("ERRO DO SCRIPT: " + $_.Exception.Message)
    Write-Error $_
    exit 1
}
finally {
    $client.Dispose()
}
