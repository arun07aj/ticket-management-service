import { defineConfig, loadEnv } from '@rsbuild/core';
import { pluginReact } from '@rsbuild/plugin-react';

const { publicVars, rawPublicVars } = loadEnv({ prefixes: ['REACT_APP_'] });

export default defineConfig({
    plugins: [pluginReact()],
    source: {
        define: {
            ...publicVars,
            'process.env': JSON.stringify(rawPublicVars),
        },
        // Compile all JS files and exclude core-js
        include: [{ not: /[\\/]core-js[\\/]/ }],
    },
    html: {
        template: './public/index.html',
    },
    // By default is /dist, changing to build as docker build expects this
    output: {
        distPath: {
            root: 'build',
        },
    },
});

