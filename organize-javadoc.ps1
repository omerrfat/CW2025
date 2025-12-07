# Script to organize Javadoc files into a dedicated javadoc folder
# This script moves all HTML, CSS, JS, and documentation files into a clean structure

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$javadocRoot = Join-Path $projectRoot "javadoc"

Write-Host "Starting Javadoc organization..." -ForegroundColor Green

# Move all HTML files from root to javadoc/
$htmlFiles = Get-ChildItem -Path $projectRoot -Filter "*.html" -ErrorAction SilentlyContinue | Where-Object { -not $_.PSIsContainer }
foreach ($file in $htmlFiles) {
    $destination = Join-Path $javadocRoot $file.Name
    Move-Item -Path $file.FullName -Destination $destination -Force
    Write-Host "Moved: $($file.Name)" -ForegroundColor Yellow
}

# Move other documentation files
$otherFiles = @("element-list", "constant-values.html")
foreach ($fileName in $otherFiles) {
    $filePath = Join-Path $projectRoot $fileName
    if (Test-Path $filePath) {
        $destination = Join-Path $javadocRoot $fileName
        Move-Item -Path $filePath -Destination $destination -Force
        Write-Host "Moved: $fileName" -ForegroundColor Yellow
    }
}

# Move JavaScript search index files
$jsIndexFiles = Get-ChildItem -Path $projectRoot -Filter "*-search-index.js" -ErrorAction SilentlyContinue | Where-Object { -not $_.PSIsContainer }
foreach ($file in $jsIndexFiles) {
    $destination = Join-Path $javadocRoot $file.Name
    Move-Item -Path $file.FullName -Destination $destination -Force
    Write-Host "Moved: $($file.Name)" -ForegroundColor Yellow
}

# Move folders with their contents
$foldersToMove = @("resource-files", "script-files", "index-files", "legal", "com")
foreach ($folderName in $foldersToMove) {
    $sourcePath = Join-Path $projectRoot $folderName
    $destPath = Join-Path $javadocRoot $folderName
    
    if (Test-Path $sourcePath) {
        # If destination already exists, remove it
        if (Test-Path $destPath) {
            Remove-Item -Path $destPath -Recurse -Force
        }
        # Move the folder
        Move-Item -Path $sourcePath -Destination $destPath -Force
        Write-Host "Moved folder: $folderName" -ForegroundColor Yellow
    }
}

Write-Host "`nJavadoc organization complete!" -ForegroundColor Green
Write-Host "All files are now in: $javadocRoot" -ForegroundColor Green
Write-Host "`nYou can now move the 'javadoc' folder outside the project if desired." -ForegroundColor Cyan
