import React from "react";
import ReactQuill, { Quill } from "react-quill";
import 'react-quill/dist/quill.snow.css';

// Configure Quill to use <div> instead of <p> for new lines
const Block = Quill.import('blots/block');
Block.tagName = 'DIV';
Quill.register(Block, true);

const RichTextEditor = ({ value, onChange, darkMode, heading }) => {
  return (
    <div className="space-y-4">
      <label className={`text-gray-700 font-medium ${darkMode ? 'text-white' : 'text-black'}`}>
        {heading}
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
          clipboard: {
            matchVisual: false, // Avoid Quill automatically inserting <p>
          },
        }}
        formats={[
          'header', 'bold', 'italic', 'underline',
          'list', 'bullet', 'link', 'image'
        ]}
        className={`border rounded-lg p-3 shadow-md w-full ${darkMode ? 'bg-gray-800 text-white' : 'bg-white'}`}
      />
    </div>
  );
};

export default RichTextEditor;
