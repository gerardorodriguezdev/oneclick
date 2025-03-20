if (config.devServer) {
    config.devServer = {
        ...config.devServer,
        port: 3000,
        historyApiFallback: {
            rewrites: [{
                from: /./,
                to: '/'
            }]
        },
        proxy: [{
            context: ['/api'],
            target: 'http://localhost:8080'
        }]
    }
}
