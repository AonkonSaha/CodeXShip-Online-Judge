import React from "react";
import MonacoEditor from "@monaco-editor/react";
import styled from "styled-components";

// Styled-components for the editor
const EditorWrapper = styled.div`
  display: flex;
  flex-direction: column;
  height: 100%;
`;

const EditorHeader = styled.div`
  display: flex;
  justify-content: flex-end;
  padding: 10px;
  background-color: #ffffff;
  border-bottom: 1px solid #ddd;
`;

const LanguageSelector = styled.div`
  display: flex;
  align-items: center;
`;

const LanguageLabel = styled.label`
  margin-right: 10px;
  font-size: 14px;
  color: #555;
`;

const LanguageSelect = styled.select`
  padding: 5px 10px;
  font-size: 14px;
  border-radius: 4px;
  border: 1px solid #ddd;
  background-color: #fff;
  color: #333;
  cursor: pointer;

  &:focus {
    border-color: #3b82f6;
    outline: none;
    box-shadow: 0 0 3px rgba(59, 130, 246, 0.5);
  }
`;

const StyledEditor = styled(MonacoEditor)`
  flex-grow: 1;
  height: 400px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #f9f9f9;
`;

const CodeEditor = ({ code, setCode, language, setLanguage }) => {
  const handleEditorChange = (value, event) => {
    setCode(value);
  };

  const handleLanguageChange = (e) => {
    setLanguage(e.target.value);
  };

  return (
    <EditorWrapper>
      <EditorHeader>
        <LanguageSelector>
          <LanguageLabel htmlFor="language">Language:</LanguageLabel>
          <LanguageSelect 
            id="language" 
            value={language} 
            onChange={handleLanguageChange} 
            aria-label="Select language"
          >
            <option value="javascript">JavaScript</option>
            <option value="python">Python</option>
            <option value="cpp">C++</option>
            <option value="java">Java</option>
            <option value="csharp">C#</option>
            <option value="ruby">Ruby</option>
            <option value="go">Go</option>
          </LanguageSelect>
        </LanguageSelector>
      </EditorHeader>

      <StyledEditor
        language={language}
        theme="vs-dark"
        value={code}
        onChange={handleEditorChange}
        options={{
          selectOnLineNumbers: true,
          automaticLayout: true,
          fontSize: 14,
          minimap: { enabled: false },
          tabSize: 2,
          insertSpaces: true,
        }}
      />
    </EditorWrapper>
  );
};

export default CodeEditor;
