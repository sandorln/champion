# lint_wiki.ps1 - LLM Wiki Governance & Integrity Audit Script
# Usage: powershell -ExecutionPolicy Bypass -File .agents/skills/llm-wiki/scripts/lint_wiki.ps1

$ErrorActionPreference = "Stop"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$RootDir = (Resolve-Path "$ScriptDir/../../../..").Path
$WikiDir = Join-Path $RootDir "20_Wiki"
$LogsDir = Join-Path $RootDir "90_Logs"

if (-not (Test-Path $LogsDir)) {
    New-Item -ItemType Directory -Path $LogsDir -Force | Out-Null
}

$AllWikiFiles = Get-ChildItem -Path $WikiDir -Filter "*.md" -Recurse -File

# 1. Collect all valid page base names
$ExistingPages = @{}
foreach ($f in $AllWikiFiles) {
    $baseName = [System.IO.Path]::GetFileNameWithoutExtension($f.Name)
    $ExistingPages[$baseName] = $f.FullName
}

$BrokenLinks = @()
$IncomingLinksCount = @{}
foreach ($baseName in $ExistingPages.Keys) {
    $IncomingLinksCount[$baseName] = 0
}

# 2. Regex for [[Target]] or [[Target|Alias]]
$LinkRegex = [regex]'\[\[([^\]|]+)(?:\|[^\]]+)?\]\]'

# 3. Parse and check links in each file
foreach ($f in $AllWikiFiles) {
    $sourceBaseName = [System.IO.Path]::GetFileNameWithoutExtension($f.Name)
    $lines = [System.IO.File]::ReadAllLines($f.FullName, [System.Text.Encoding]::UTF8)
    
    for ($i = 0; $i -lt $lines.Length; $i++) {
        $line = $lines[$i]
        $matches = $LinkRegex.Matches($line)
        foreach ($m in $matches) {
            $target = $m.Groups[1].Value.Trim()
            if ($ExistingPages.ContainsKey($target)) {
                if ($target -ne $sourceBaseName) {
                    $IncomingLinksCount[$target] = $IncomingLinksCount[$target] + 1
                }
            } else {
                $BrokenLinks += [PSCustomObject]@{
                    SourceFile = $f.Name
                    Line = ($i + 1)
                    Target = $target
                }
            }
        }
    }
}

# 4. Detect orphan pages (excluding Indexes.md)
$OrphanPages = @()
foreach ($key in $IncomingLinksCount.Keys) {
    if ($key -ne "Indexes" -and $IncomingLinksCount[$key] -eq 0) {
        $OrphanPages += $key
    }
}

# 5. Build Lint Report Markdown
$ReportDate = (Get-Date).ToString("yyyy-MM-dd HH:mm:ss")
$sb = [System.Text.StringBuilder]::new()
[void]$sb.AppendLine("# LLM Wiki Lint Report")
[void]$sb.AppendLine("")
[void]$sb.AppendLine("- Audit Timestamp: $ReportDate")
[void]$sb.AppendLine("- Total Wiki Files: $($AllWikiFiles.Count)")
[void]$sb.AppendLine("- Broken Links: $($BrokenLinks.Count)")
[void]$sb.AppendLine("- Orphan Pages: $($OrphanPages.Count)")
[void]$sb.AppendLine("")
[void]$sb.AppendLine("---")
[void]$sb.AppendLine("")
[void]$sb.AppendLine("## 1. Broken Links")

if ($BrokenLinks.Count -eq 0) {
    [void]$sb.AppendLine("- No broken links detected. (Clean)")
} else {
    [void]$sb.AppendLine("| Source File | Line | Missing Target |")
    [void]$sb.AppendLine("|---|---|---|")
    foreach ($bl in $BrokenLinks) {
        [void]$sb.AppendLine("| $($bl.SourceFile) | $($bl.Line) | [[$($bl.Target)]] |")
    }
}

[void]$sb.AppendLine("")
[void]$sb.AppendLine("---")
[void]$sb.AppendLine("")
[void]$sb.AppendLine("## 2. Orphan Pages")
[void]$sb.AppendLine("> Pages not linked or referenced by any other wiki page.")
[void]$sb.AppendLine("")

if ($OrphanPages.Count -eq 0) {
    [void]$sb.AppendLine("- No orphan pages detected. All nodes are connected. (Clean)")
} else {
    foreach ($op in $OrphanPages) {
        [void]$sb.AppendLine("- [[$op]]")
    }
}

[void]$sb.AppendLine("")
[void]$sb.AppendLine("---")
[void]$sb.AppendLine("")
[void]$sb.AppendLine("## 3. Recommended Actions")

if ($BrokenLinks.Count -gt 0 -or $OrphanPages.Count -gt 0) {
    [void]$sb.AppendLine("- For broken links: Create the missing Concept/Entity or fix typos.")
    [void]$sb.AppendLine("- For orphan pages: Link them in 20_Wiki/Indexes.md or related concept pages.")
} else {
    [void]$sb.AppendLine("- Wiki knowledge graph integrity is 100% verified.")
}

$ReportPath = Join-Path $LogsDir "lint_report.md"
[System.IO.File]::WriteAllText($ReportPath, $sb.ToString(), [System.Text.Encoding]::UTF8)

Write-Host "========================================"
Write-Host "LLM Wiki Lint Completed!"
Write-Host " - Scanned Files: $($AllWikiFiles.Count)"
Write-Host " - Broken Links: $($BrokenLinks.Count)"
Write-Host " - Orphan Pages: $($OrphanPages.Count)"
Write-Host " - Report Location: $ReportPath"
Write-Host "========================================"

if ($BrokenLinks.Count -gt 0 -or $OrphanPages.Count -gt 0) {
    exit 1
} else {
    exit 0
}
