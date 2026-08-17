/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{html,ts}'],
  theme: {
    extend: {
      colors: {
        brand: {
          primary: 'var(--color-primary)',
          secondary: 'var(--color-secondary)',
          accent: 'var(--color-accent)',
          warning: 'var(--color-warning)',
        },
        myntra: {
          pink: 'var(--color-primary)',
          dark: 'var(--color-text-heading)',
          gray: 'var(--color-text-muted)',
          light: 'var(--color-bg-page)',
          green: 'var(--color-accent)',
          orange: 'var(--color-warning)',
        }
      },
      fontFamily: {
        sans: ['var(--font-family-base)'],
      }
    },
  },
  plugins: [],
};
