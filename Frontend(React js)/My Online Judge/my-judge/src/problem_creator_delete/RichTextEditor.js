import React from "react";
import ReactQuill from "react-quill";
import 'react-quill/dist/quill.snow.css'; // Import Quill styles

const RichTextEditor = ({ value, onChange, darkMode }) => {
  return (
    <div className="space-y-4">
      <label className={`text-gray-700 font-medium ${darkMode ? 'text-white' : 'text-black'}`} htmlFor="editor">
        Problem Description
      </label>
      <ReactQuill
        value={value}
        onChange={onChange}
        theme="snow"
        modules={{
          toolbar: [
            [{ header: [1, 2, false] }],
            ['bold', 'italic', 'underline'],
            [{ list: 'ordered' }, { list: 'bullet' }],
            ['link', 'image'],
          ],
        }}
        className={`border rounded-lg p-3 shadow-md w-full ${darkMode ? 'bg-gray-800 text-white' : 'bg-white'}`}
      />
    </div>
  );
};

export default RichTextEditor;
