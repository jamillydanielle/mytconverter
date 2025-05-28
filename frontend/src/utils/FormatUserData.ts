export const formatUserData = (string: string) => {
    return string
        .split('_') // Divide a string em palavras usando o underline como delimitador
        .map(word => {
            // Verifica se a palavra tem 2 caracteres
            if (word.length === 2) {
                return word.toUpperCase(); // Retorna a palavra em maiúsculas se tiver 2 caracteres
            }
            // Capitaliza a primeira letra e deixa o restante em minúsculas
            return word.charAt(0).toUpperCase() + word.slice(1).toLowerCase();
        })
        .join(' '); // Junta as palavras de volta em uma string com espaços
};

