import React from 'react';
import { createRoot } from 'react-dom/client';
import AuthApplication from './auth/AuthApplication';
import './styles.css';

createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <AuthApplication />
  </React.StrictMode>,
);
