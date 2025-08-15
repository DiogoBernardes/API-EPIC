const express = require('express');
const multer = require('multer');
const Tesseract = require('tesseract.js');
require('dotenv').config();

const app = express();
const upload = multer({ dest: 'uploads/' });

app.post('/api/ocr/upload', upload.single('file'), async (req, res) => {
  const { path } = req.file;
  const { data: { text } } = await Tesseract.recognize(path, 'por');
  res.json({ text });
});

app.listen(process.env.PORT || 3000);
