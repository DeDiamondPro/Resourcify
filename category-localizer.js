const https = require('https');

function capitalizeWords(str) {
    return str.replace(/(\b[a-z])/g, match => match.toUpperCase());
}

function fetchData(url, headers = {}) {
    return new Promise((resolve, reject) => {
        https.get(url, { headers }, (res) => {
            let data = '';

            res.on('data', (chunk) => {
                data += chunk;
            });

            res.on('end', () => {
                resolve(JSON.parse(data));
            });

            res.on('error', (err) => {
                reject(err);
            });
        });
    });
}

async function fetchAndProcessData() {
    const modrinthData = await fetchData('https://api.modrinth.com/v2/tag/category');
    const modrinthIndexes = [];
    modrinthData.forEach(item => {
        if (!["resourcepack", "shader", "mod"].includes(item.project_type)) return;
        let header = `"resourcify.categories.${item.header.toLowerCase().replaceAll(" ", "_")}": "${capitalizeWords(item.header)}",`;
        if (!modrinthIndexes.includes(header)) {
            modrinthIndexes.push(header);
        }
        let category = `"resourcify.categories.${item.name.toLowerCase().replaceAll(" ", "_")}": "${capitalizeWords(item.name)}",`
        if(!modrinthIndexes.includes(category)) {
            modrinthIndexes.push(category);
        }
    });
    console.log(`Modrinth (${modrinthIndexes.length}):`);
    modrinthIndexes.forEach(localization => {
        console.log(`  ${localization}`);
    });

    const curseForgeData = await fetchData('https://api.curseforge.com/v1/categories?gameId=432', {'x-api-key': 'cf-api-key'});
    const translationIndexes = [];
    curseForgeData.data.forEach(item => {
        if (![12, 6945, 6552].includes(item.classId)) return;
        let category = `"resourcify.categories.${item.name.toLowerCase().replaceAll(" ", "_")}": "${capitalizeWords(item.name)}",`
        if (!translationIndexes.includes(category) && !modrinthIndexes.includes(category)) {
            translationIndexes.push(category);
        }
    });
    console.log(`CurseForge (${translationIndexes.length}):`);
    translationIndexes.forEach(localization => {
        console.log(`  ${localization}`);
    });
}

fetchAndProcessData();