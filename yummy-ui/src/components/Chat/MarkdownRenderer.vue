<template>
  <div class="markdown-content" v-html="renderedContent"></div>
</template>

<script>
import marked from 'marked';
import hljs from 'highlight.js';
import 'highlight.js/styles/atom-one-dark.css'; // 使用深色主题
import katex from 'katex';
import 'katex/dist/katex.min.css'; // 导入KaTeX样式

export default {
  name: 'MarkdownRenderer',
  props: {
    content: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      renderedContent: ''
    }
  },
  watch: {
    content: {
      immediate: true,
      handler(newVal) {
        this.renderMarkdown(newVal);
      }
    }
  },
  created() {
    // 配置marked使用highlight.js
    marked.setOptions({
      highlight: function(code, language) {
        const validLanguage = hljs.getLanguage(language) ? language : 'plaintext';
        return hljs.highlight(validLanguage, code).value;
      },
      breaks: true,
      gfm: true
    });
  },
  mounted() {
    // 添加复制代码按钮的全局处理函数
    window.copyCodeToClipboard = this.copyCodeToClipboard;
  },
  methods: {
    renderMarkdown(content) {
      if (!content) {
        this.renderedContent = '';
        return;
      }
      
      // 创建一个自定义的渲染器
      const renderer = new marked.Renderer();
      
      // 自定义代码块渲染
      renderer.code = (code, language) => {
        // 确定语言显示名称
        const displayLang = this.getLanguageDisplayName(language || 'plaintext');
        // 确保语言是highlight.js支持的
        const validLanguage = hljs.getLanguage(language) ? language : 'plaintext';
        // 高亮处理代码
        const highlightedCode = hljs.highlight(validLanguage, code).value;
        
        // 创建带有语言标签和复制按钮的代码块
        return `
          <div class="code-block">
            <div class="code-header">
              <span class="language-label">${displayLang}</span>
              <div class="copy-code" onclick="copyCodeToClipboard(this)">
                <i class="el-icon-document-copy"></i> 复制代码
              </div>
            </div>
            <pre><code class="hljs language-${validLanguage}">${highlightedCode}</code></pre>
          </div>
        `;
      };
      
      // 配置marked使用自定义渲染器
      marked.use({ renderer });
      
      // 修复未闭合的代码块
      const fixedContent = this.fixUnclosedCodeBlocks(content);
      
      // 先处理LaTeX公式
      const contentWithMath = this.renderLatexInText(fixedContent);
      
      // 渲染Markdown
      this.renderedContent = marked(contentWithMath);
      
      // 渲染完毕后处理math标签中的LaTeX公式
      this.$nextTick(() => {
        this.renderLatexPostProcess();
      });
    },
    
    // 预处理文本中的LaTeX公式 - 修复行内公式渲染问题
    renderLatexInText(text) {
      // 1. 先标记代码块和行内代码
      let processedText = text;
      const codeBlocks = [];
      const inlineCodes = [];
      
      // 收集代码块
      processedText = processedText.replace(/```[\s\S]*?```/g, match => {
        const placeholder = `CODE_BLOCK_${codeBlocks.length}`;
        codeBlocks.push(match);
        return placeholder;
      });
      
      // 收集行内代码
      processedText = processedText.replace(/`[^`]+`/g, match => {
        const placeholder = `INLINE_CODE_${inlineCodes.length}`;
        inlineCodes.push(match);
        return placeholder;
      });
      
      // 2. 处理块级公式
      // 处理 $$...$$
      processedText = processedText.replace(/\$\$([\s\S]+?)\$\$/g, (match, formula) => {
        return `<div class="math block" data-math-type="double-dollar">$$${formula}$$</div>`;
      });
      
      // 处理 \[...\]
      processedText = processedText.replace(/\\\[([\s\S]+?)\\\]/g, (match, formula) => {
        return `<div class="math block" data-math-type="bracket">$${formula}$</div>`;
      });
      
      // 3. 处理行内公式
      // 处理 $...$ - 确保前后不是数字或字母，避免误匹配
      processedText = processedText.replace(/([^\\]|^)\$([^\s$][^$]*?[^\s\\])\$/g, (match, pre, formula) => {
        return `${pre}<span class="math inline" data-math-type="single-dollar">$${formula}$</span>`;
      });
      
      // 处理 \(...\) - 特殊处理，直接提取公式内容，不保留分隔符
      processedText = processedText.replace(/\\\(([\s\S]+?)\\\)/g, (match, formula) => {
        return `<span class="math inline" data-math-type="parenthesis">${formula}</span>`;
      });
      
      // 4. 恢复代码块和行内代码
      for (let i = 0; i < inlineCodes.length; i++) {
        processedText = processedText.replace(`INLINE_CODE_${i}`, inlineCodes[i]);
      }
      
      for (let i = 0; i < codeBlocks.length; i++) {
        processedText = processedText.replace(`CODE_BLOCK_${i}`, codeBlocks[i]);
      }
      
      return processedText;
    },
    
    // 在Markdown渲染完成后处理LaTeX公式
    renderLatexPostProcess() {
      if (!this.$el) return;
      
      try {
        // 处理行内公式
        const inlineElements = this.$el.querySelectorAll('.math.inline');
        inlineElements.forEach(element => {
          try {
            // 获取公式类型
            const mathType = element.getAttribute('data-math-type');
            let formula = element.textContent || '';
            
            // 根据不同类型提取公式内容
            if (mathType === 'single-dollar' && formula.startsWith('$') && formula.endsWith('$')) {
              formula = formula.substring(1, formula.length - 1);
            } else if (mathType === 'parenthesis') {
              // 对于 \( \) 类型，内容已经提取，不需要额外处理
            }
            
            // 清空元素内容，以准备渲染
            element.innerHTML = '';
            
            // 渲染公式
            katex.render(formula, element, {
              displayMode: false,
              throwOnError: false,
              output: 'html'
            });
          } catch (e) {
            console.error('行内LaTeX渲染失败:', e, element.textContent);
            element.classList.add('math-error');
            element.textContent = '⚠️ ' + element.textContent;
          }
        });
        
        // 处理块级公式
        const blockElements = this.$el.querySelectorAll('.math.block');
        blockElements.forEach(element => {
          try {
            // 获取公式类型
            const mathType = element.getAttribute('data-math-type');
            let formula = element.textContent || '';
            
            // 根据不同类型提取公式内容
            if (mathType === 'double-dollar' && formula.startsWith('$$') && formula.endsWith('$$')) {
              formula = formula.substring(2, formula.length - 2);
            } else if (mathType === 'bracket' && formula.startsWith('$') && formula.endsWith('$')) {
              formula = formula.substring(1, formula.length - 1);
            }
            
            // 清空元素内容
            element.innerHTML = '';
            
            // 渲染公式
            katex.render(formula, element, {
              displayMode: true,
              throwOnError: false,
              output: 'html'
            });
          } catch (e) {
            console.error('块级LaTeX渲染失败:', e, element.textContent);
            element.classList.add('math-error');
            element.textContent = '⚠️ ' + element.textContent;
          }
        });
      } catch (e) {
        console.error('LaTeX后处理总体错误:', e);
      }
    },
    
    // 修复未闭合的代码块，防止实时渲染出错
    fixUnclosedCodeBlocks(content) {
      // 计算代码块标记数量
      const codeBlockMarkers = content.match(/```/g) || [];
      
      // 如果代码块标记是奇数，添加一个结束标记
      if (codeBlockMarkers.length % 2 === 1) {
        // 检查最后一个是不是开始标记
        const lastIndex = content.lastIndexOf('```');
        const textAfterLastMarker = content.substring(lastIndex + 3);
        
        // 如果后面没有结束标记，则此标记是一个开始标记，需要临时添加结束标记
        if (!textAfterLastMarker.includes('```')) {
          return content + '\n```';
        }
      }
      
      return content;
    },
    
    copyCodeToClipboard(el) {
      const codeBlock = el.closest('.code-block').querySelector('code');
      const textToCopy = codeBlock.textContent;
      
      navigator.clipboard.writeText(textToCopy).then(() => {
        const originalText = el.innerHTML;
        el.innerHTML = '<i class="el-icon-check"></i> 已复制';
        
        setTimeout(() => {
          el.innerHTML = originalText;
        }, 2000);
      }).catch(err => {
        console.error('复制失败:', err);
      });
    },
    
    // 根据语言代码获取显示名称
    getLanguageDisplayName(lang) {
      if (!lang) return 'plaintext';
      
      const languageMap = {
        'js': 'JavaScript',
        'ts': 'TypeScript',
        'html': 'HTML',
        'css': 'CSS',
        'scss': 'SCSS',
        'java': 'Java',
        'py': 'Python',
        'python': 'Python',
        'rb': 'Ruby',
        'go': 'Go',
        'c': 'C',
        'cpp': 'C++',
        'cs': 'C#',
        'php': 'PHP',
        'sh': 'Shell',
        'bash': 'Bash',
        'sql': 'SQL',
        'json': 'JSON',
        'xml': 'XML',
        'yaml': 'YAML',
        'md': 'Markdown',
        'vue': 'Vue'
      };
      
      return languageMap[lang.toLowerCase()] || lang;
    }
  }
}
</script>

<style lang="scss" scoped>
.markdown-content {
  width: 100%;
  line-height: 1.6;
  
  ::v-deep {
    // 调整代码样式以适应消息气泡
    code {
      font-family: Consolas, Monaco, 'Andale Mono', monospace;
      padding: 2px 4px;
      border-radius: 3px;
      
      // 在用户气泡内部的代码样式，需要针对性调整
      .user-bubble & {
        background-color: rgba(255,255,255,0.3);
        color: white;
      }
      
      // 正常AI消息中的行内代码样式 - 背景色加深
      &:not(.hljs) {
        background-color: rgba(0, 0, 0, 0.1);
        color: #333;
      }
    }
    
    // 调整代码块样式 - 使用深色主题
    .code-block {
      margin: 12px 0;
      border-radius: 6px;
      overflow: hidden;
      border: 1px solid #282c34;
      
      // 用户气泡内的代码块需要特殊样式
      .user & {
        border-color: rgba(255,255,255,0.2);
      }
    }
    
    .code-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 6px 16px;
      background-color: #434155;
      font-size: 12px;
      color: #dfdfdf; // 浅色文字
      border-bottom: 1px solid #434155;
      
      .language-label {
        font-weight: bold;
      }
      
      .copy-code {
        cursor: pointer;
        display: flex;
        align-items: center;
        
        i {
          margin-right: 4px;
        }
        
        &:hover {
          color: #61afef; // 高亮蓝色
        }
      }
    }
    
    pre {
      margin: 0;
      padding: 0;
      background-color: transparent;
      
      code.hljs {
        padding: 16px;
        border-radius: 0;
        display: block;
        overflow-x: auto;
        font-family: Consolas, Monaco, 'Andale Mono', monospace;
        font-size: 14px;
        line-height: 1.5;
        background-color: #282c34 !important; // 强制使用深色背景
      }
    }
    
    // 新增：链接样式 - 蓝色+下划线
    a {
      color: #1e88e5;
      text-decoration: underline;
      
      &:hover {
        color: #0d47a1;
      }
      
      // 在用户气泡内部的链接样式，需要针对性调整
      .user-bubble & {
        color: #ffffff;
        text-decoration: underline;
        font-weight: bold;
        
        &:hover {
          color: #e3f2fd;
        }
      }
    }
    
    // LaTeX公式样式
    .math {
      &.block {
        display: block;
        padding: 10px 0;
        overflow-x: auto;
        text-align: center;
        margin: 1em 0;
        background-color: #f9f9f9;
        border-radius: 4px;
      }
      
      &.inline {
        padding: 0 2px;
        display: inline-flex; // 改为inline-flex以确保内容正确显示
        vertical-align: middle;
        
        .katex {
          display: inline-flex; // 确保KaTeX内容正确显示
          align-items: center;
        }
      }
      
      &.math-error {
        background-color: #fff0f0;
        color: #e53935;
        border: 1px dashed #e53935;
        padding: 2px 4px;
        border-radius: 3px;
        font-family: monospace;
      }
    }
    
    // 调整KaTeX样式以确保行内公式正确显示
    .katex {
      text-rendering: auto;
      font: normal 1.1em 'KaTeX_Main', 'Times New Roman', serif;
      line-height: 1.2;
      white-space: nowrap;
    }
    
    // 块级公式样式
    .katex-display {
      overflow-x: auto;
      overflow-y: hidden;
      padding: 5px 0;
      margin: 0.5em 0;
      
      &::-webkit-scrollbar {
        height: 3px;
      }
      
      &::-webkit-scrollbar-thumb {
        background-color: rgba(0,0,0,0.2);
      }
    }
    
    // 其他 Markdown 元素样式
    p {
      margin-bottom: 12px;
    }
    
    ul, ol {
      padding-left: 24px;
      margin-bottom: 12px;
    }
    
    table {
      border-collapse: collapse;
      margin: 12px 0;
      width: 100%;
      
      th, td {
        border: 1px solid #e5e7eb;
        padding: 8px 12px;
        text-align: left;
      }
      
      th {
        background-color: #f6f8fa;
        font-weight: bold;
      }
      
      tr:nth-child(even) {
        background-color: #fafafa;
      }
    }
    
    blockquote {
      border-left: 4px solid #ddd;
      padding-left: 16px;
      color: #666;
      margin: 12px 0;
    }
    
    hr {
      border: none;
      border-top: 1px solid #e5e7eb;
      margin: 16px 0;
    }
    
    img {
      max-width: 100%;
      height: auto;
    }
  }
}
</style>
