const { join } = require('path');

/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    join(__dirname, 'src/**/*.{html,ts}'),
  ],
  theme: {
    extend: {},
  },
  plugins: [],
};
