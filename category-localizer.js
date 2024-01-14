// Small script to fetch https://api.modrinth.com/v2/tag/category and generate what's needed to localize categories

const https = require('https');

https.get('https://api.modrinth.com/v2/tag/category', (res) => {
    let data = '';

    res.on('data', (chunk) => {
        data += chunk;
    });

    res.on('end', () => {
        const jsonData = JSON.parse(data);
        const translationIndexes = []
        jsonData.forEach(item => {
            if (!["resourcepack", "shader", "mod"].includes(item.project_type)) return
            let header = `"resourcify.categories.${item.project_type}.${item.header.toLowerCase().replace(" ", "_")}": "${capitalizeWords(item.header)}",`
            if (!translationIndexes.includes(header)) {
                translationIndexes.push(header)
            }
            translationIndexes.push(`"resourcify.categories.${item.project_type}.${item.name.toLowerCase().replace(" ", "_")}": "${capitalizeWords(item.name)}",`)
        })
        translationIndexes.sort().forEach(localization => {
            console.log(`  ${localization}`)
        })
    });
});

function capitalizeWords(str) {
    return str.replace(/(\b[a-z])/g, match => match.toUpperCase());
}