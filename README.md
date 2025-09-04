# DeepResearch

A Java Spring Boot service that provides AI-powered research capabilities using LangChain4j and various LLM providers (Ollama, Groq, OpenAI, etc.). It autonomously generates research queries, performs web searches, and creates comprehensive summaries.

## ✨ Features

- **AI-Powered Research**: Uses LangChain4j with multiple LLM providers
- **Multiple Search Backends**: DuckDuckGo, Tavily, Perplexity support
- **Beautiful Web UI**: Professional markdown rendering with syntax highlighting
- **Real-time Feedback**: Loading indicators and status updates
- **Flexible Configuration**: Environment variable support
- **Comprehensive Debug Logging**: Detailed logging for troubleshooting
- **Responsive Design**: Works on desktop and mobile

## 🚀 Quick Start

### Prerequisites
- Java 21 or higher
- Maven 3.6+
- An LLM provider (OpenAI, Inception, Anthropic, Ollama, Groq, etc.)

### 1. Clone and Build
```bash
git clone <repository-url>
cd DeepResearch
mvn clean package
```

### 2. Configure Environment Variables

#### Default: Use Inception (for speed)
```bash
# API key is the only thing needed, since others are defaults 
export RESEARCH_API_KEY=your_inception_api_key

# These are not needed but merely mentioned here for completeness
export RESEARCH_LLM_PROVIDER=inception
export RESEARCH_MODEL_NAME=mercury-coder
export RESEARCH_BASE_URL=https://api.inceptionlabs.ai/v1
```

#### Option A: Use Ollama (Local)
```bash
# Install Ollama
curl -fsSL https://ollama.ai/install.sh | sh

# Pull a model
ollama pull llama2

# Set environment variables
export RESEARCH_LLM_PROVIDER=ollama
export RESEARCH_MODEL_NAME=llama2
export RESEARCH_BASE_URL=http://localhost:11434
```

#### Option B: Use Groq
```bash
export RESEARCH_LLM_PROVIDER=groq
export RESEARCH_MODEL_NAME=mixtral-8x7b-32768
export RESEARCH_BASE_URL=https://api.groq.com/openai/v1
export RESEARCH_API_KEY=your_groq_api_key
```

#### Option C: Use OpenAI
```bash
export RESEARCH_LLM_PROVIDER=openai
export RESEARCH_MODEL_NAME=gpt-4
export RESEARCH_BASE_URL=https://api.openai.com/v1
export RESEARCH_API_KEY=your_openai_api_key
```

### 3. Run the Application
```bash
mvn spring-boot:run
```

### 4. Access the Web Interface
Open your browser to: `http://localhost:8080`

## 🎨 Web Interface

The application includes a beautiful, responsive web interface with:
- **Markdown rendering** with syntax highlighting
- **Real-time loading indicators**
- **Error handling and feedback**
- **Copy buttons for code blocks**
- **Mobile-responsive design**

## 🔧 Configuration

### Environment Variables
| Variable | Description | Default |
|----------|-------------|---------|
| `RESEARCH_LLM_PROVIDER` | LLM provider (ollama, groq, openai) | `ollama` |
| `RESEARCH_MODEL_NAME` | Model name to use | `llama2` |
| `RESEARCH_BASE_URL` | Base URL for LLM API | `http://localhost:11434` |
| `RESEARCH_API_KEY` | API key for cloud providers | ` ` |
| `MAX_RESEARCH_LOOPS` | Number of research iterations | `3` |
| `SEARCH_API` | Search backend (duckduckgo, tavily, perplexity) | `duckduckgo` |
| `MAX_TOKENS` | Max tokens per source | `1000` |

### Application Properties
The application uses Spring Boot configuration. You can override settings in `application.yml`.

## 📊 API Endpoints

### Research API
- `POST /api/research/conduct` - Start research
- `GET /api/research/health` - Health check
- `GET /api/research/debug` - Debug information

### Example Usage
```bash
# Start research
curl -X POST http://localhost:8080/api/research/conduct \
  -H "Content-Type: application/json" \
  -d '{"topic":"Latest advances in quantum computing"}'

# Health check
curl http://localhost:8080/api/research/health

# Debug info
curl http://localhost:8080/api/research/debug
```

## 🐛 Debug Mode

Enable debug logging by setting:
```bash
export LOGGING_LEVEL_COM_EXAMPLE=DEBUG
```

## 🧪 Testing

### Manual Testing
1. Start the application
2. Open `http://localhost:8080`
3. Enter a research topic
4. Click "Run Research"
5. Watch the loading indicator and results

### API Testing
```bash
# Test with a simple topic
curl -X POST http://localhost:8080/api/research/conduct \
  -H "Content-Type: application/json" \
  -d '{"topic":"What is artificial intelligence?"}'
```

## 📝 Examples

### Research Topics
- "Latest advances in quantum computing"
- "2024 FDA approved cancer drugs"
- "Climate change mitigation strategies"
- "Machine learning applications in healthcare"

### Sample Output
The application generates comprehensive markdown reports with:
- Executive summary
- Key findings
- Sources and references
- Formatted tables and lists
- Code examples (if applicable)

## 🔍 Troubleshooting

### Common Issues

#### Research takes too long
- Check your LLM provider connection
- Reduce `MAX_RESEARCH_LOOPS` if needed
- Ensure your API key is valid

#### No results appear
- Check browser console for errors
- Verify environment variables
- Test with a simple topic first

### Debug Commands
```bash
# Check configuration
curl http://localhost:8080/api/research/debug

# Test connection
curl http://localhost:8080/api/research/health
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Add comprehensive logging
4. Test with multiple LLM providers
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [LangChain4j](https://github.com/langchain4j/langchain4j) for AI integration
- [Marked.js](https://marked.js.org/) for markdown rendering
- [Prism.js](https://prismjs.com/) for syntax highlighting
- [Spring Boot](https://spring.io/projects/spring-boot) for the framework