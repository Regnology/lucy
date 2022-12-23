function setupProxy({ tls }) {
  const conf = [
    {
      context: ['/api', '/services', '/management', '/v3/api-docs', '/h2-console', '/auth', '/health'],
      // For production
      //target: `http${tls ? 's' : ''}://localhost:8080/lucy`,
      // For development
      target: `http${tls ? 's' : ''}://localhost:8080/`,
      secure: false,
      changeOrigin: tls,
    },
  ];
  return conf;
}

module.exports = setupProxy;
