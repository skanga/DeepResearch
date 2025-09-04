// Fixed JavaScript with proper global function exposure
class ResearchApp {
    constructor() {
        this.topicInput = document.getElementById('topic');
        this.runBtn = document.getElementById('runBtn');
        this.clearBtn = document.getElementById('clearBtn');
        this.statusDiv = document.getElementById('status');
        this.resultDiv = document.getElementById('result');
        this.resultContainer = document.getElementById('resultContainer');
        this.spinner = document.getElementById('spinner');
        this.btnText = document.getElementById('btnText');

        this.initializeMarked();
        this.bindEvents();
        this.setupUI();
    }

    initializeMarked() {
        marked.setOptions({
            highlight: function(code, lang) {
                if (Prism.languages[lang]) {
                    return Prism.highlight(code, Prism.languages[lang], lang);
                }
                return code;
            },
            breaks: true,
            gfm: true
        });
    }

    setupUI() {
        this.statusDiv.style.display = 'none';
        this.resultContainer.style.display = 'none';
    }

    bindEvents() {
        // Use proper event listeners instead of onclick
        this.runBtn.addEventListener('click', () => this.runResearch());
        this.clearBtn.addEventListener('click', () => this.clearResults());
        
        // Allow Ctrl+Enter
        this.topicInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && e.ctrlKey) {
                e.preventDefault();
                this.runResearch();
            }
        });
    }

    showLoading() {
        this.runBtn.disabled = true;
        this.spinner.classList.remove('hidden');
        this.btnText.textContent = 'Researching...';
        this.statusDiv.textContent = 'Starting research...';
        this.statusDiv.className = 'status loading';
        this.statusDiv.style.display = 'block';
        this.resultContainer.style.display = 'none';
    }

    hideLoading() {
        this.runBtn.disabled = false;
        this.spinner.classList.add('hidden');
        this.btnText.textContent = 'Run Research';
        this.statusDiv.style.display = 'none';
    }

    showStatus(message, type = 'info', progress = '') {
        this.statusDiv.style.display = 'block';
        this.statusDiv.className = `status ${type}`;
        
        const statusText = document.getElementById('statusText');
        const progressText = document.getElementById('progressText');
        
        if (statusText) statusText.textContent = message;
        if (progressText) progressText.textContent = progress;
    }

    showProgress(message, progress) {
        this.showStatus(message, 'loading', progress);
    }

    showError(message) {
        this.showStatus(message, 'error');
        this.hideLoading();
    }

    showSuccess(message) {
        this.showStatus(message, 'success');
        setTimeout(() => {
            this.statusDiv.style.display = 'none';
        }, 3000);
    }

    renderMarkdown(content) {
        try {
            const html = marked.parse(content);
            this.resultDiv.innerHTML = html;
            this.resultContainer.style.display = 'block';
            this.resultContainer.classList.add('fade-in');
            this.addCopyButtons();
        } catch (error) {
            console.error('Error rendering markdown:', error);
            this.resultDiv.innerHTML = `<pre>${content}</pre>`;
            this.resultContainer.style.display = 'block';
        }
    }

    addCopyButtons() {
        const codeBlocks = this.resultDiv.querySelectorAll('pre code');
        codeBlocks.forEach(block => {
            const pre = block.parentElement;
            const copyBtn = document.createElement('button');
            copyBtn.textContent = 'Copy';
            copyBtn.className = 'copy-btn';
            copyBtn.style.cssText = `
                position: absolute;
                top: 5px;
                right: 5px;
                background: #667eea;
                color: white;
                border: none;
                padding: 5px 10px;
                border-radius: 3px;
                font-size: 12px;
                cursor: pointer;
            `;
            pre.style.position = 'relative';
            pre.appendChild(copyBtn);
            
            copyBtn.addEventListener('click', () => {
                navigator.clipboard.writeText(block.textContent);
                copyBtn.textContent = 'Copied!';
                setTimeout(() => copyBtn.textContent = 'Copy', 2000);
            });
        });
    }

    async runResearch() {
        const topic = this.topicInput.value.trim();
        const researchSteps = document.getElementById('researchSteps').value;
        
        if (!topic) {
            this.showError('Please enter a research topic');
            return;
        }

        this.showLoading();

        try {
            const payload = { topic };
            if (researchSteps !== null && researchSteps !== undefined && researchSteps !== '') {
                const steps = parseInt(researchSteps);
                if (!isNaN(steps) && steps > 0) {
                    payload.researchSteps = steps;
                }
            }

            const controller = new AbortController();
            const timeoutId = setTimeout(() => controller.abort(), 300000); // 5 minutes

            const response = await fetch('/api/research/conduct', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(payload),
                signal: controller.signal
            });

            clearTimeout(timeoutId);

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            const data = await response.json();
            
            if (data.error) {
                throw new Error(data.error);
            }

            this.renderMarkdown(data.summary);
            this.showSuccess('Research completed!');
            
        } catch (error) {
            clearTimeout(timeoutId);
            console.error('Research error:', error);
            
            if (error.name === 'AbortError') {
                this.showError('Research request timed out after 5 minutes. Please try again with a more specific topic or fewer research steps.');
            } else if (error.message.includes('NetworkError') || error.message.includes('Failed to fetch')) {
                this.showError('Network error. Please check your connection and try again.');
            } else {
                this.showError(`Error: ${error.message}`);
            }
        } finally {
            this.hideLoading();
        }
    }

    clearResults() {
        this.topicInput.value = '';
        this.resultDiv.innerHTML = '';
        this.resultContainer.style.display = 'none';
        this.statusDiv.style.display = 'none';
        this.topicInput.focus();
    }
}

// Initialize and expose globally
let app;
document.addEventListener('DOMContentLoaded', () => {
    app = new ResearchApp();
    
    // Expose functions globally for onclick handlers
    window.runResearch = () => app.runResearch();
    window.clearResults = () => app.clearResults();
});

// Alternative: Simple global functions for backward compatibility
function runResearch() {
    if (window.app) {
        window.app.runResearch();
    } else {
        console.error('App not initialized');
    }
}

function clearResults() {
    if (window.app) {
        window.app.clearResults();
    } else {
        console.error('App not initialized');
    }
}

// Ensure the functions are available immediately
window.runResearch = runResearch;
window.clearResults = clearResults;

// Enhanced error handling for the legacy version
document.addEventListener('DOMContentLoaded', () => {
    const topicInput = document.getElementById('topic');
    const runBtn = document.getElementById('runBtn');
    const statusDiv = document.getElementById('status');
    const resultDiv = document.getElementById('result');

    runBtn.addEventListener('click', async () => {
        const topic = topicInput.value.trim();
        
        if (!topic) {
            statusDiv.textContent = 'Please enter a topic';
            return;
        }

        // Show loading state
        statusDiv.textContent = 'Researching...';
        runBtn.disabled = true;
        resultDiv.innerHTML = '';

        try {
            const response = await fetch('/api/research/conduct', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ topic })
            });

            const data = await response.json();
            
            if (data.error) {
                statusDiv.textContent = 'Error: ' + data.error;
            } else {
                statusDiv.textContent = '';
                const html = marked.parse(data.summary);
                resultDiv.innerHTML = html;
            }
        } catch (error) {
            statusDiv.textContent = 'Error: ' + error.message;
        } finally {
            runBtn.disabled = false;
        }
    });
});