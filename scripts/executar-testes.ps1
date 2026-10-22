param([string]$OutputFile = "evidencias/resultado-mvn-test.txt")

$ErrorActionPreference = "Continue"
$fullOutput = [System.IO.Path]::GetFullPath($OutputFile)
$outputDir = Split-Path -Parent $fullOutput
if ($outputDir) { New-Item -ItemType Directory -Force -Path $outputDir | Out-Null }

@("Data: $(Get-Date -Format o)", "Comando: mvn test", "--- saída real ---") |
    Set-Content -LiteralPath $fullOutput -Encoding UTF8

try {
    & mvn test 2>&1 | Tee-Object -FilePath $fullOutput -Append
    $exitCode = $LASTEXITCODE
    if ($null -eq $exitCode) { $exitCode = 1 }
} catch {
    $_ | Out-String | Add-Content -LiteralPath $fullOutput -Encoding UTF8
    $exitCode = 1
}

Add-Content -LiteralPath $fullOutput -Encoding UTF8 ("--- código de saída real: {0} ---" -f $exitCode)
if ($exitCode -eq 0) {
    Write-Host "mvn test finalizou com código 0. Confira o relatório: $fullOutput"
} else {
    Write-Error "mvn test não finalizou com sucesso. Consulte a saída real em: $fullOutput"
    exit $exitCode
}
