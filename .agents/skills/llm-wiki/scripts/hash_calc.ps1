# hash_calc.ps1 - SHA256 Checksum Calculator & Deduplication Checker
# Usage: powershell -ExecutionPolicy Bypass -File .agents/skills/llm-wiki/scripts/hash_calc.ps1 -FilePath "00_Inbox/Sample_Patch_Note_Pipeline.md"

param (
    [Parameter(Mandatory=$true)]
    [string]$FilePath
)

$ErrorActionPreference = "Stop"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$RootDir = (Resolve-Path "$ScriptDir/../../../..").Path
$LogsDir = Join-Path $RootDir "90_Logs"
$LogFile = Join-Path $LogsDir "ingest_log.json"

if (-not (Test-Path $FilePath)) {
    Write-Error "Target file not found: $FilePath"
    exit 1
}

# Calculate SHA256
$FileHash = (Get-FileHash -Path $FilePath -Algorithm SHA256).Hash.ToLower()

Write-Host "Target File: $FilePath"
Write-Host "SHA256 Hash: $FileHash"

# Check against existing ingestion log
$IsDuplicate = $false
if (Test-Path $LogFile) {
    try {
        $jsonContent = Get-Content -Path $LogFile -Raw -Encoding UTF8 | ConvertFrom-Json
        foreach ($entry in $jsonContent) {
            if ($entry.sha256 -eq $FileHash) {
                $IsDuplicate = $true
                Write-Warning "Duplicate detected! Already ingested in: $($entry.filename) at $($entry.timestamp)"
                break
            }
        }
    } catch {
        Write-Warning "Failed to parse log file: $LogFile"
    }
}

if (-not $IsDuplicate) {
    Write-Host "Unique source document. Safe to ingest."
}

[PSCustomObject]@{
    FilePath = $FilePath
    SHA256 = $FileHash
    IsDuplicate = $IsDuplicate
} | ConvertTo-Json
