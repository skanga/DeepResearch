# Ollama Setup Guide

## Quick Setup

1. **Install Ollama**:
   - Download from https://ollama.ai
   - Or use: `curl -fsSL https://ollama.ai/install.sh | sh`

2. **Pull the model**:
   ```bash
   ollama pull llama2
   ```

3. **Start Ollama**:
   ```bash
   ollama serve
   ```

4. **Verify it's working**:
   ```bash
   curl http://localhost:11434/api/tags
   ```

## Alternative Models

If `llama2` is too large for your system, try these smaller models:

```bash
# Smaller models
ollama pull tinyllama
ollama pull phi
ollama pull codellama:7b
```

Then update `application.yml`:
```yaml
research:
  modelName: tinyllama  # or phi, or codellama:7b
```

## Troubleshooting

### Model not found error
- Ensure Ollama is running: `ollama serve`
- Check available models: `ollama list`
- Pull the model: `ollama pull <model-name>`

### Connection issues
- Check if Ollama is running on port 11434
- Try accessing: http://localhost:11434 in your browser
- Check firewall settings

### Memory issues
- Use smaller models like `tinyllama` or `phi`
- Monitor memory usage with `htop` or `top`