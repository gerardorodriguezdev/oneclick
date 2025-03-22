if (config.devServer) {
    const directories = config.devServer.static

    config.devServer = {
        ...config.devServer,
        static: directories.map(item => ({
            directory: item,
            watch: {
                ignored: '**/local/**',
                usePolling: false
            },
        })),
        historyApiFallback: {
            rewrites: [{
                from: /./,
                to: '/'
            }]
        },
    }
}
