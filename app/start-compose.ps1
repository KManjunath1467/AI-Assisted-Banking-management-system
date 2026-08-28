if (-not $env:OPENAI_API_KEY) {
    Write-Host "NOTE: OPENAI_API_KEY environment variable is not set. You can set it using:"
    Write-Host "  `$env:OPENAI_API_KEY='sk-...'"
    Write-Host "Or if using a custom base URL (e.g., Ollama / local):"
    Write-Host "  `$env:OPENAI_BASE_URL='http://localhost:11434/v1'"
}

Write-Host "Starting solution locally using docker compose..."
docker compose -f ./compose.yaml up --build