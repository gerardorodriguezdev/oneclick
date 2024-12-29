if (config.devServer) {
    config.devServer = {
        ...config.devServer,
        historyApiFallback: {
            rewrites: [{
                from: /./,
                to: '/'
            }]
        },
        proxy: [{
            context: ['/api'],
            target: 'http://localhost:3000'
        }]
    }
}
