/**
 * 流式处理工具
 * 处理文本的流式输出、代码块处理等功能
 */

/**
 * 处理带有代码块的文本输出
 * @param {string} text 完整的输出文本
 * @param {object} options 选项对象
 * @param {function} options.onOutput 输出回调函数
 * @param {function} options.onComplete 完成回调函数
 * @param {function} options.onAbort 中止回调函数
 * @param {AbortSignal} options.signal 中止信号
 * @param {object} options.speedOptions 速度选项
 */
export function processStreamingText(text, options) {
  const { 
    onOutput, 
    onComplete, 
    onAbort, 
    signal,
    speedOptions = { 
      baseDelay: 40, // 增加基础延迟，让输出速度减慢一半
      randomness: 20  // 增加随机性
    }
  } = options;
  
  // 分析代码块和普通文本
  const segments = splitTextIntoSegments(text);
  
  // 如果已中止，直接退出
  if (signal && signal.aborted) {
    if (onAbort) onAbort();
    return;
  }
  
  // 注册中止处理函数
  const abortHandler = () => {
    clearTimeout(timeoutId);
    if (onAbort) onAbort();
  };
  
  if (signal) {
    signal.addEventListener('abort', abortHandler);
  }
  
  // 开始输出各个段落
  let currentIndex = 0;
  
  const outputNextSegment = () => {
    // 检查是否中止
    if (signal && signal.aborted) {
      if (onAbort) onAbort();
      return;
    }
    
    if (currentIndex < segments.length) {
      const segment = segments[currentIndex++];
      
      // 输出此段内容
      if (onOutput) onOutput(segment.content);
      
      // 根据内容类型决定延迟
      let delay;
      
      if (segment.type === 'codeblock') {
        // 代码块需要更长的停顿
        delay = speedOptions.baseDelay * 4;
      } else if (segment.type === 'codestart' || segment.type === 'codeend') {
        // 代码块开始和结束标记也需要较长停顿
        delay = speedOptions.baseDelay * 3;
      } else {
        // 普通文本根据内容调整延迟
        delay = speedOptions.baseDelay;
        
        // 句尾停顿更长
        if (/[.!?。！？]$/.test(segment.content)) {
          delay *= 2;
        }
        // 换行也停顿更长
        else if (/\n$/.test(segment.content)) {
          delay *= 1.5;
        }
      }
      
      // 添加随机变化
      delay += Math.random() * speedOptions.randomness;
      
      // 安排下一个段落输出
      timeoutId = setTimeout(outputNextSegment, delay);
    } else {
      // 全部输出完成
      if (signal) {
        signal.removeEventListener('abort', abortHandler);
      }
      if (onComplete) onComplete();
    }
  };
  
  // 开始第一个段落的输出
  let timeoutId = setTimeout(outputNextSegment, 0);
}

/**
 * 将文本分割为段落，特别处理代码块
 * @param {string} text 要分割的文本
 * @returns {Array} 段落数组
 */
export function splitTextIntoSegments(text) {
  const segments = [];
  let currentPosition = 0;
  let inCodeBlock = false;
  
  // 查找所有代码块标记位置
  let codeMarkerPositions = [];
  let pos = -1;
  
  while ((pos = text.indexOf('```', pos + 1)) !== -1) {
    codeMarkerPositions.push(pos);
  }
  
  // 如果没有代码块，直接按句子分割普通文本
  if (codeMarkerPositions.length === 0) {
    return splitPlainText(text);
  }
  
  // 处理代码块和普通文本
  for (let i = 0; i < codeMarkerPositions.length; i++) {
    const markerPos = codeMarkerPositions[i];
    
    if (!inCodeBlock) {
      // 处理代码块前的普通文本
      if (markerPos > currentPosition) {
        const plainText = text.substring(currentPosition, markerPos);
        segments.push(...splitPlainText(plainText));
      }
      
      // 添加代码块开始标记
      const nextLineBreak = text.indexOf('\n', markerPos);
      if (nextLineBreak !== -1) {
        // 包括语言标识符在内的第一行
        segments.push({
          type: 'codestart',
          content: text.substring(markerPos, nextLineBreak + 1)
        });
        currentPosition = nextLineBreak + 1;
      } else {
        // 没有换行符，整个都是开始标记
        segments.push({
          type: 'codestart',
          content: text.substring(markerPos)
        });
        currentPosition = text.length;
      }
      
      inCodeBlock = true;
    } else {
      // 处理代码块内容，直到结束标记
      const codeContent = text.substring(currentPosition, markerPos);
      
      if (codeContent) {
        segments.push({
          type: 'codeblock',
          content: codeContent
        });
      }
      
      // 添加代码块结束标记
      segments.push({
        type: 'codeend',
        content: text.substring(markerPos, markerPos + 3)
      });
      
      currentPosition = markerPos + 3;
      inCodeBlock = false;
    }
  }
  
  // 处理最后剩余的文本
  if (currentPosition < text.length) {
    const remainingText = text.substring(currentPosition);
    
    if (inCodeBlock) {
      // 如果还在代码块中，作为代码块内容处理
      segments.push({
        type: 'codeblock',
        content: remainingText
      });
    } else {
      // 否则作为普通文本处理
      segments.push(...splitPlainText(remainingText));
    }
  }
  
  return segments;
}

/**
 * 将普通文本分割成更小的段落
 * @param {string} text 普通文本
 * @returns {Array} 段落数组
 */
function splitPlainText(text) {
  if (!text) return [];
  
  const segments = [];
  
  // 按句子和段落分割
  const paragraphs = text.split(/(\n\s*\n)/);
  
  paragraphs.forEach(paragraph => {
    if (!paragraph.trim()) {
      // 空段落直接添加
      if (paragraph) {
        segments.push({ type: 'text', content: paragraph });
      }
      return;
    }
    
    // 如果是短段落，整体添加
    if (paragraph.length < 80) {
      segments.push({ type: 'text', content: paragraph });
      return;
    }
    
    // 长段落按句子分割
    const sentences = paragraph.match(/[^.!?。！？]+[.!?。！？]+|[^.!?。！？]+$/g);
    if (sentences) {
      sentences.forEach(sentence => {
        if (sentence.trim()) {
          segments.push({ type: 'text', content: sentence });
        }
      });
    } else {
      // 没有找到句子，可能是很长的单一内容，按固定长度分割
      let start = 0;
      while (start < paragraph.length) {
        const end = Math.min(start + 50, paragraph.length);
        segments.push({
          type: 'text',
          content: paragraph.substring(start, end)
        });
        start = end;
      }
    }
  });
  
  return segments;
}

/**
 * 检测文本是否包含完整的代码块
 * @param {string} text 要检查的文本
 * @returns {object} 包含检查结果的对象
 */
export function checkCodeBlockStatus(text) {
  if (!text) return { complete: true, count: 0 };
  
  const markers = text.match(/```/g) || [];
  const count = markers.length;
  
  return {
    complete: count % 2 === 0,
    count: count,
    inProgress: count % 2 === 1
  };
}
