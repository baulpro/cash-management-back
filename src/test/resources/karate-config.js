function fn() {
    const config = {};
    config.baseUrl = java.lang.System.getProperty('baseUrl', 'http://localhost:8080');
    return config;
}